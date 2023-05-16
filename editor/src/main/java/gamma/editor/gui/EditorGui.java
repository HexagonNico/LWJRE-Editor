package gamma.editor.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public abstract class EditorGui {

	private static final HashSet<EditorGui> ADD_QUEUE = new HashSet<>();
	private static final HashSet<Class<?>> REMOVE_QUEUE = new HashSet<>();
	private static final HashMap<Class<?>, EditorGui> GUI = new HashMap<>();

	public static void add(EditorGui gui) {
		ADD_QUEUE.add(gui);
	}

	public static void remove(Class<? extends EditorGui> gui) {
		REMOVE_QUEUE.add(gui);
	}

	public static <T extends EditorGui> Optional<T> get(Class<T> gui) {
		return Optional.ofNullable(gui.cast(GUI.get(gui)));
	}

	public static void clear() {
		GUI.clear();
	}

	public static void showProjectGui() {
		clear();
		add(new FileSystemGui());
		add(new SceneTreeGui());
		add(new InspectorGui());
		add(new SceneViewportGui());
		add(new EditorMenuGui());
	}

	public static void drawGui() {
		ADD_QUEUE.removeIf(gui -> GUI.put(gui.getClass(), gui) == null);
		GUI.forEach((key, gui) -> gui.onDraw());
		REMOVE_QUEUE.removeIf(gui -> GUI.remove(gui) != null);
	}

	protected abstract void onDraw();
}
