package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.gui.AlertPopupGui;
import io.github.lwjre.editor.gui.PopupModalGui;
import io.github.lwjre.engine.nodes.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Class responsible for dynamically loading project and dependencies classes.
 *
 * @author Nico
 */
public class ProjectClassesLoader implements Closeable {

	/** Maps the class name to the class object */
	private final HashMap<String, Class<?>> nodeClasses = new HashMap<>();
	/** Detects when the project's classes change */
	private final WatchService watchService;

	/** Popup to show when the classes are being reloaded */
	private final PopupModalGui reloadingPopup = new PopupModalGui("Reloading project...", "mvn install");
	/** Popup to show if there is a compilation error in the project */
	private final AlertPopupGui errorPopup = new AlertPopupGui("Loading error", "There was an error compiling the project");

	/**
	 * Creates the loader.
	 *
	 * @throws UncheckedIOException If an {@code IOException} occurs
	 */
	public ProjectClassesLoader() {
		try {
			this.watchService = FileSystems.getDefault().newWatchService();
			Files.walkFileTree(ProjectPath.sourcesFolder(), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
					directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Initializes the loader by loading the classes from the project's {@code target/classes} folder and from the project's dependencies.
	 */
	public void init() {
		this.reloadingPopup.setContent("mvn install -DskipTests");
		this.reloadingPopup.open();
		RuntimeHelper.execute("mvn install -DskipTests").onExit(() -> {
			this.reloadingPopup.setContent("Loading dependencies");
			this.loadNodeClassesFromDependencies();
			this.reloadingPopup.setContent("Loading 'target/classes'");
			this.loadNodeClasses(ProjectPath.append("target/classes"));
			this.reloadingPopup.close();
		}).onError(() -> {
			this.reloadingPopup.close();
			this.errorPopup.open();
		});
	}

	/**
	 * Listens for changes in the project's classes and reloads the project if that is the case.
	 *
	 * @return True if a change is detected, otherwise false
	 */
	public boolean listenForChanges() {
		boolean result = false;
		this.reloadingPopup.draw();
		this.errorPopup.draw();
		// TODO: Listen for changes in dependencies
		WatchKey watchKey = this.watchService.poll();
		if(watchKey != null) {
			for(WatchEvent<?> event : watchKey.pollEvents()) {
				if(event.context().toString().endsWith(".java")) {
					this.reloadingPopup.setContent("mvn install -DskipTests");
					this.reloadingPopup.open();
					result = true;
					RuntimeHelper.execute("mvn install -DskipTests").onExit(() -> {
						EditorClassLoader.changeCurrentThreadClassLoader();
						this.reloadingPopup.setContent("Loading 'target/classes'");
						this.loadNodeClasses(ProjectPath.append("target/classes"));
						EditorScene.reload();
						this.reloadingPopup.close();
					}).onError(() -> {
						this.reloadingPopup.close();
						this.errorPopup.open();
					});
					break;
				}
			}
			watchKey.reset();
		}
		return result;
	}

	/**
	 * Loads classes that extends {@link Node} from {@code .class} files in the given directory.
	 *
	 * @param path The directory to look into
	 */
	private void loadNodeClasses(Path path) {
		if(Files.exists(path)) try(Stream<Path> files = Files.walk(path)) {
			files.filter(file -> file.toString().endsWith(".class")).forEach(file -> {
				try {
					String className = path.relativize(file).toString().replace('/', '.').replace(".class", "");
					Class<?> classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					if (Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
						this.nodeClasses.put(className, classObject);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads classes that extends {@link Node} from the project's dependencies.
	 */
	private void loadNodeClassesFromDependencies() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(ProjectPath.append("pom.xml").toFile());
			NodeList dependencies = document.getElementsByTagName("dependency");
			for(int i = 0; i < dependencies.getLength(); i++) {
				Element dependency = (Element) dependencies.item(i);
				String groupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
				String artifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = dependency.getElementsByTagName("version").item(0).getTextContent();
				// TODO: Make sure that this works on windows
				try(JarFile jarFile = new JarFile(Path.of(System.getProperty("user.home"), "/.m2/repository/", groupId.replace('.', '/'), artifactId, version, artifactId + "-" + version + ".jar").toFile())) {
					jarFile.stream().forEach(jarEntry -> {
						String name = jarEntry.getName();
						if(name.endsWith(".class")) try {
							String className = name.replace('/', '.').replace(".class", "");
							Class<?> classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
							if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
								this.nodeClasses.put(className, classObject);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the loaded {@link Node} classes.
	 *
	 * @return A {@link Collection} of the loaded {@code Node} classes
	 */
	public Collection<Class<?>> getNodeClasses() {
		return this.nodeClasses.values();
	}

	@Override
	public void close() throws IOException {
		this.watchService.close();
	}
}
