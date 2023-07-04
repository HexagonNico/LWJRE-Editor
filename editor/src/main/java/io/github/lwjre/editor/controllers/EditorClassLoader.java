package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The editor's class loader.
 * Used to load classes from the project's {@code target/classes} directory and resources from the {@code src/main/resources} directory.
 *
 * @author Nico
 */
public class EditorClassLoader extends ClassLoader implements Closeable {

	/**
	 * Changes the context class loader of the current thread to a new instance of {@link EditorClassLoader}
	 * and closes the current class loader if it is an instance of {@link Closeable}.
	 *
	 * @see Thread#setContextClassLoader(ClassLoader)
	 *
	 * @throws UncheckedIOException If an {@link IOException} occurs while trying to close the current class loader
	 */
	public static void changeCurrentThreadClassLoader() {
		Thread thread = Thread.currentThread();
		if(thread.getContextClassLoader() instanceof Closeable closeable) try {
			closeable.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		thread.setContextClassLoader(new EditorClassLoader());
	}

	/** Used to load classes from the file system */
	private final URLClassLoader urlClassLoader;

	/**
	 * Constructs the editor class loader.
	 *
	 * @throws RuntimeException If the {@code target/classes} folder path cannot be converted to url
	 */
	public EditorClassLoader() {
		try {
			this.urlClassLoader = new URLClassLoader(new URL[] {
					ProjectPath.append("target/classes").toUri().toURL()
			});
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.urlClassLoader.loadClass(name);
	}

	@Override
	protected URL findResource(String name) {
		try {
			Path path = ProjectPath.resourcesFolder(name);
			return Files.exists(path) ? path.toUri().toURL() : null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		this.urlClassLoader.close();
	}
}
