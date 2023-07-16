package io.github.lwjre.editor.utils;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.nodes.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Static utility class used to look for classes that extend {@link Node} in {@code .class} files in the file system or in {@code .jar} files in the project's dependencies.
 *
 * @author Nico
 */
public class ClassFilesLoader {

	/**
	 * Looks for classes that extend {@link Node}.
	 *
	 * @return A {@link HashSet} containing the full name of all classes found
	 */
	public static HashSet<String> lookForClasses() {
		HashSet<String> result = new HashSet<>();
		lookForClassesInProject(result);
		lookForClassesInDependencies(result);
		return result;
	}

	/**
	 * Looks for classes in the current project's {@code target/classes} directory.
	 *
	 * @param result The resulting {@link Set}
	 */
	private static void lookForClassesInProject(Set<String> result) {
		Path path = ProjectPath.append("target/classes");
		if(Files.exists(path)) try(Stream<Path> files = Files.walk(path); URLClassLoader classLoader = new URLClassLoader(new URL[] {path.toUri().toURL()})) {
			files.filter(file -> file.toString().endsWith(".class")).forEach(file -> {
				try {
					String className = path.relativize(file).toString().replace('/', '.').replace(".class", "");
					Class<?> classObject = classLoader.loadClass(className);
					if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
						result.add(className);
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
	 * Looks for classes in the current project's dependencies.
	 *
	 * @param result The resulting {@link Set}
	 */
	private static void lookForClassesInDependencies(Set<String> result) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(ProjectPath.append("pom.xml").toFile());
			NodeList dependencies = document.getElementsByTagName("dependency");
			for(int i = 0; i < dependencies.getLength(); i++) {
				Element dependency = (Element) dependencies.item(i);
				// TODO: Replace ${project.property} with actual properties
				String groupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
				String artifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = dependency.getElementsByTagName("version").item(0).getTextContent();
				// TODO: Make sure that this works on windows
				Path path = Path.of(System.getProperty("user.home"), "/.m2/repository/", groupId.replace('.', '/'), artifactId, version, artifactId + "-" + version + ".jar");
				try(JarFile jarFile = new JarFile(path.toFile()); URLClassLoader classLoader = new URLClassLoader(new URL[] {path.toUri().toURL()})) {
					jarFile.stream().forEach(jarEntry -> {
						String name = jarEntry.getName();
						if(name.endsWith(".class")) try {
							String className = name.replace('/', '.').replace(".class", "");
							Class<?> classObject = classLoader.loadClass(className);
							if(Node.class.isAssignableFrom(classObject) && !Modifier.isAbstract(classObject.getModifiers())) {
								result.add(className);
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
}
