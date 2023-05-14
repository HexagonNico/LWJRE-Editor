package gamma.editor;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditorClassLoader extends ClassLoader implements Closeable {

	private final URLClassLoader classLoader;

	public EditorClassLoader() {
		try {
			this.classLoader = new URLClassLoader(new URL[] {
					ProjectPath.append("target/classes").toUri().toURL(),
					ProjectPath.append("build/classes/java/main").toUri().toURL()
			});
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return this.classLoader.loadClass(name);
	}

	@Override
	public URL findResource(String name) {
		try {
			Path path = ProjectPath.resourcesFolder(name);
			return Files.exists(path) ? path.toUri().toURL() : null;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		this.classLoader.close();
	}
}
