package gamma.editor;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class ProjectPath {

	private static Path current = Path.of("demo");

	public static Path append(String... path) {
		return Path.of(current.toString(), path);
	}

	public static Path resourcesFolder() {
		return append("src/main/resources");
	}

	public static Path resourcesFolder(String... path) {
		return Path.of(resourcesFolder().toString(), path);
	}

	public static List<? extends Class<?>> getNodeClasses() {
		ArrayList<Class<?>> result = new ArrayList<>();
		// TODO: Only load when dependencies change
		fromDependencies(result);
		// TODO: Only load after `mvn clean install`
		fromProject(result, append("target/classes"));
		fromProject(result, append("build/classes/java/main"));
		return result;
	}

	private static void fromDependencies(ArrayList<Class<?>> result) {
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
							Class<?> classObject = Thread.currentThread().getContextClassLoader().loadClass(name.replace('/', '.').replace(".class", ""));
							if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
								result.add(classObject);
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

	private static void fromProject(ArrayList<Class<?>> result, Path path) {
		if(Files.exists(path)) try(Stream<Path> files = Files.walk(path)) {
			files.forEach(file -> {
				if(file.toString().endsWith(".class")) try {
					String name = path.relativize(file).toString();
					Class<?> classObject = Thread.currentThread().getContextClassLoader().loadClass(name.replace('/', '.').replace(".class", ""));
					if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
						result.add(classObject);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
