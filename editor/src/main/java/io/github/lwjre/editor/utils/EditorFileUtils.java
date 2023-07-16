package io.github.lwjre.editor.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class used to handle project files in the editor.
 *
 * @author Nico
 */
public class EditorFileUtils {

	/**
	 * Creates a directory with the given name at the given path.
	 * Used to create a directory from the file system gui.
	 *
	 * @param path Path to the directory containing the one to be created or to a file in the same directory
	 * @param name Name of the directory to create
	 */
	public static void createDirectory(Path path, String name) {
		try {
			Path newPath = Files.isDirectory(path) ? Path.of(path.toString(), name) : Path.of(path.getParent().toString(), name);
			Files.createDirectories(newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renames the file at the given path to the given name.
	 *
	 * @param path Path to the file to rename
	 * @param name The file's new name
	 */
	public static void rename(Path path, String name) {
		try {
			// TODO: Update path when the currently open scene is renamed
			Files.move(path, Path.of(path.getParent().toString(), name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the file or directory at the given path.
	 * Directories are deleted recursively.
	 *
	 * @param path Path to the file or directory to be deleted
	 */
	public static void delete(Path path) {
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
