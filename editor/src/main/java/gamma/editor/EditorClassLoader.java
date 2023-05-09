package gamma.editor;

import gamma.engine.tree.Node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * EditorClassLoader is set as the context class loader as soon as the editor starts with {@code Thread.currentThread().setContextClassLoader(new EditorClassLoader())}.
 * Finds classes and resources inside the project's build directory when they are not found in the classpath.
 * Used to load component classes when the "Add component" button is clicked and when resources are used in the editor.
 *
 * @author Nico
 */
public class EditorClassLoader extends ClassLoader {

	private final URL[] urls;

	public EditorClassLoader() {
		try {
			this.urls = new URL[] {
					new File("demo/target/classes").toURI().toURL(),
					new File("demo/build/classes/java/main").toURI().toURL()
			};
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try(URLClassLoader classLoader = new URLClassLoader(this.urls)) {
			return classLoader.loadClass(name);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public URL findResource(String name) {
		try {
			File file = new File("demo/src/main/resources/" + name);
			return file.exists() ? file.toURI().toURL() : null;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static List<? extends Class<?>> getNodeClasses() {
		try(JarFile engineJar = new JarFile(new File(System.getProperty("user.home") + "/.m2/repository/gamma/engine/gamma-engine-core/1.0-SNAPSHOT/gamma-engine-core-1.0-SNAPSHOT.jar"))) {
			return engineJar.stream()
					.map(JarEntry::getName)
					.filter(name -> name.endsWith(".class"))
					.map(name -> {
						try {
							return Class.forName(name.replace('/', '.').replace(".class", ""));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							return Object.class;
						}
					}).filter(nodeClass -> Node.class.isAssignableFrom(nodeClass) && !Modifier.isAbstract(nodeClass.getModifiers())).toList();
		} catch (IOException e) {
			e.printStackTrace();
			return List.of();
		}
	}
}
