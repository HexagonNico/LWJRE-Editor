package gamma.editor.gui;

import java.util.HashSet;

public abstract class EditorGui {

	private static final HashSet<EditorGui> GUI = new HashSet<>();

	public static void add(EditorGui gui) {
		GUI.add(gui);
	}

	public static void remove(EditorGui gui) {
		GUI.remove(gui);
	}

	public static void clear() {
		GUI.clear();
	}

	public static void drawGui() {
		GUI.forEach(EditorGui::onDraw);
	}

	protected abstract void onDraw();
}
