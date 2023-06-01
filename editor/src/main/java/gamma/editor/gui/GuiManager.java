package gamma.editor.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;

public final class GuiManager {

	private static final HashSet<EditorGui> ADD_QUEUE = new HashSet<>();
	private static final HashSet<Class<?>> REMOVE_QUEUE = new HashSet<>();
	private static final HashMap<Class<?>, EditorGui> GUI = new HashMap<>();

	static {
		ServiceLoader.load(EditorGui.class).forEach(gui -> GUI.put(gui.getClass(), gui));
	}

	public static <T extends EditorGui> T get(Class<T> gui) {
		return gui.cast(GUI.get(gui));
	}

	public static void drawGui() {
		ADD_QUEUE.removeIf(gui -> GUI.put(gui.getClass(), gui) == null);
		GUI.forEach((key, gui) -> gui.draw());
		REMOVE_QUEUE.removeIf(gui -> GUI.remove(gui) != null);
	}
}
