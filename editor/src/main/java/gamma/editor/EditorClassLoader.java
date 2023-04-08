package gamma.editor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * EditorClassLoader is set as the context class loader as soon as the editor starts with {@code Thread.currentThread().setContextClassLoader(new EditorClassLoader())}.
 * Finds classes and resources inside the project's build directory when they are not found in the classpath.
 * Used to load component classes when the "Add component" button is clicked and when resources are used in the editor.
 *
 * @author Nico
 */
public class EditorClassLoader extends ClassLoader {

	// TODO: Detect if the project uses maven or gradle

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try(URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(EditorApplication.currentPath() + "/target/classes").toURI().toURL()})) {
			return classLoader.loadClass(name);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public URL findResource(String name) {
		try {
			File file = new File(EditorApplication.currentPath() + "/src/main/resources/" + name);
			return file.exists() ? file.toURI().toURL() : null;
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
