package io.github.lwjre.editor.models;

import io.github.lwjre.editor.ProjectPath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of a dynamic class loader that can load classes from the {@code target/classes} directory and resources from the {@link ProjectPath#resourcesFolder()} directory.
 *
 * @author Nico
 */
public class EditorClassLoader extends ClassLoader {

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			Path path = ProjectPath.append("target/classes/" + name.replace('.', '/') + ".class");
			byte[] bytes = Files.readAllBytes(path);
			return this.defineClass(name, bytes, 0, bytes.length);
		} catch (IOException e) {
			throw new ClassNotFoundException("Failed to load class: " + name, e);
		}
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
}
