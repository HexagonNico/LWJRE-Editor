package gamma.editor.gui;

import imgui.ImGui;

import java.util.HashSet;

public abstract class EditorGui {

	private static final HashSet<EditorGui> GUI = new HashSet<>();

	public static void add(EditorGui gui) {
		GUI.add(gui);
	}

	public static void clear() {
		GUI.clear();
	}

	public static void drawGui() {
		GUI.forEach(gui -> {
			if(ImGui.begin(gui.title(), gui.flags())) {
				gui.onDraw();
			}
			ImGui.end();
		});
	}

	protected abstract String title();

	protected int flags() {
		return 0;
	}

	protected abstract void onDraw();
}
