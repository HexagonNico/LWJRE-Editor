package gamma.editor;

import gamma.editor.controls.EditorScene;
import gamma.editor.gui.PopupModalGui;
import gamma.engine.tree.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class DynamicLoader {

	private static final HashMap<String, Class<?>> NODE_CLASSES = new HashMap<>();
	private static final WatchService WATCH_SERVICE;

	static {
		try {
			WATCH_SERVICE = FileSystems.getDefault().newWatchService();
			Files.walkFileTree(ProjectPath.sourcesFolder(), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					dir.register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Collection<Class<?>> getNodeClasses() {
		return NODE_CLASSES.values();
	}

	private static Process reloadProcess;

	public static void listenForChanges() {
		WatchKey watchKey = WATCH_SERVICE.poll();
		if(watchKey != null) {
			for(WatchEvent<?> event : watchKey.pollEvents()) {
				if(event.context().toString().endsWith(".java")) {
					reloadProject();
					break;
				}
			}
			watchKey.reset();
		}
		if(reloadProcess != null && !reloadProcess.isAlive()) {
			if(reloadProcess.exitValue() == 0) {
				Thread.currentThread().setContextClassLoader(new EditorClassLoader());
				reloadProject(ProjectPath.append("target/classes"));
				reloadProject(ProjectPath.append("build/classes/java/main"));
				EditorScene.reload();
			} else {
				PopupModalGui.show("Error", "Could not compile sources", true);
			}
			reloadProcess = null;
			PopupModalGui.hide();
		}
	}

	public static void reloadProject() {
		PopupModalGui.show("Reloading...", "Compiling sources...");
		try {
			// TODO: Execute this in the right directory
			reloadProcess = Runtime.getRuntime().exec("mvn clean install");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void reloadProject(Path path) {
		if(Files.exists(path)) try(Stream<Path> files = Files.walk(path)) {
			files.forEach(file -> {
				if(file.toString().endsWith(".class")) try {
					String className = path.relativize(file).toString().replace('/', '.').replace(".class", "");
					Class<?> classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
						NODE_CLASSES.put(className, classObject);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO: Do this again if dependencies change
	public static void reloadDependencies() {
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
								NODE_CLASSES.put(className, classObject);
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

	public static void closeWatchService() {
		try {
			WATCH_SERVICE.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
