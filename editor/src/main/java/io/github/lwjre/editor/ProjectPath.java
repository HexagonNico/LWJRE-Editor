package io.github.lwjre.editor;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class responsible for holding the path to the currently open project.
 *
 * @author Nico
 */
public final class ProjectPath {

	/** Path to the folder containing the current project */
	private static Path current = Path.of(".");

	/**
	 * Checks if the current path is a maven project.
	 *
	 * @return True if we are inside a project, otherwise false
	 */
	public static boolean isInsideProject() {
		return Files.exists(append("pom.xml")) && (Files.exists(resourcesFolder()) || Files.exists(sourcesFolder()));
	}

	/**
	 * Changes the current project path.
	 * Needs to be called when opening a project.
	 *
	 * @param path The new project path
	 */
	public static void setCurrent(String path) {
		current = Path.of(path);
	}

	/**
	 * Returns the current project path.
	 *
	 * @return The current project path
	 */
	public static Path current() {
		return current;
	}

	/**
	 * Returns the given path appended to the current project path.
	 *
	 * @param path The path to append
	 * @return The given path appended to the current project path
	 */
	public static Path append(String... path) {
		return Path.of(current.toString(), path);
	}

	/**
	 * Returns the path to the {@code src/main/java} folder for the current project.
	 *
	 * @return The path to the {@code src/main/java} folder for the current project
	 */
	public static Path sourcesFolder() {
		return append("src/main/java");
	}

	/**
	 * Returns the path to the {@code src/main/resources} folder for the current project.
	 *
	 * @return The path to the {@code src/main/resources} folder for the current project
	 */
	public static Path resourcesFolder() {
		return append("src/main/resources");
	}

	/**
	 * Returns the given path appended to the current project's resources folder.
	 * Used to get a specific resource.
	 *
	 * @param path The path to append
	 * @return The given path appended to the current project's resources folder
	 */
	public static Path resourcesFolder(String... path) {
		return Path.of(resourcesFolder().toString(), path);
	}
}
