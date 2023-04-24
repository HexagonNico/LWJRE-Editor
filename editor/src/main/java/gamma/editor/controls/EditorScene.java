package gamma.editor.controls;

import gamma.editor.EditorApplication;
import gamma.engine.scene.EntityResource;
import gamma.engine.scene.Scene;
import gamma.engine.utils.YamlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Static class to hold a reference to the scene that is currently in the editor.
 * Used to have a static method to save the scene accessible everywhere.
 *
 * @author Nico
 */
public final class EditorScene {

	// TODO: Handle what happens when the file is moved/deleted while the scene is open

	/** Path to the file representing the scene */
	private static String currentFile;
	/** The resource representing the current scene */
	private static EntityResource current;

	/**
	 * Changes the scene in {@link Scene} and saves its file's path.
	 *
	 * @param filePath Path to the scene's file
	 */
	public static void changeScene(String filePath) {
		current = EntityResource.getOrLoad(filePath);
		Scene.changeSceneTo(current);
		currentFile = filePath;
	}

	/**
	 * Gets the current scene or null if there is no scene.
	 *
	 * @return The current scene or null if there is no scene.
	 */
	public static EntityResource current() {
		return current;
	}

	/**
	 * Saves the current scene to its file.
	 */
	public static void saveCurrent() {
		String yaml = YamlSerializer.serialize(current);
		try {
			Files.write(Path.of(EditorApplication.currentPath() + currentFile), yaml.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
