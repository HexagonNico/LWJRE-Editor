package gamma.editor;

import gamma.engine.scene.Scene;
import gamma.engine.utils.YamlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EditorScene {

	private static String currentFile;

	public static void changeScene(String filePath) {
		Scene scene = YamlUtils.parseFile(filePath, Scene.class);
		Scene.changeScene(scene);
		currentFile = filePath;
	}

	public static void saveCurrent() {
		String yaml = YamlUtils.serialize(Scene.getCurrent());
		try {
			Files.write(Path.of(currentFile), yaml.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
