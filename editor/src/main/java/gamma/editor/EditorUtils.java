package gamma.editor;

import gamma.engine.scene.Entity;

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
}
