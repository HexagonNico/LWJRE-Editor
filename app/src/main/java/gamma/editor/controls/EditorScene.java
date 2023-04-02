package gamma.editor.controls;

import gamma.engine.scene.Entity;
import gamma.engine.scene.Scene;
import gamma.engine.utils.YamlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EditorScene {

	private static String currentFile;
	private static Entity current;

	public static void changeScene(String filePath) {
		current = YamlUtils.parseFile(filePath, Entity.class);
		Scene.changeSceneTo(current);
		currentFile = filePath;
	}

	public static void saveCurrent() {
		String yaml = YamlUtils.serialize(current);
		try {
			Files.write(Path.of(currentFile), yaml.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
