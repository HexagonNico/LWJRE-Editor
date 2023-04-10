package gamma.editor;

import gamma.engine.scene.Entity;

import java.nio.file.Files;
import java.nio.file.Path;

public final class EditorUtils {

	public static String findUnusedName(String fromName, Entity entity) {
		String name = fromName;
		int i = 1;
		while(entity.hasChild(name)) {
			name = fromName + i;
			i++;
		}
		return name;
	}

	public static String findUnusedName(String fromName, Path path) {
		String name = fromName;
		int i = 1;
		while(Files.exists(Path.of(path.toString(), name))) {
			name = fromName + i;
			i++;
		}
		return name;
	}
}
