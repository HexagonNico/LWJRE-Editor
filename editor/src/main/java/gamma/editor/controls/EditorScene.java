package gamma.editor.controls;

import gamma.engine.scene.Entity;
import gamma.engine.scene.Scene;
import gamma.engine.utils.YamlUtils;

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
	/** Root of the current scene */
	private static Entity current;

	/**
	 * Changes the scene in {@link Scene} and saves its file's path.
	 *
	 * @param filePath Path to the scene's file
	 */
	public static void changeScene(String filePath) {
		current = YamlUtils.parseFile(filePath, Entity.class);
		Scene.changeSceneTo(current);
		currentFile = filePath;
	}

	/**
	 * Saves the current scene to its file.
	 */
	public static void saveCurrent() {
		String yaml = YamlUtils.serialize(current);
		try {
			Files.write(Path.of(currentFile), yaml.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
