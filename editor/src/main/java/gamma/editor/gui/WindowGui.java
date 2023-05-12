package gamma.editor.gui;

import imgui.ImGui;

import java.util.HashSet;

public abstract class WindowGui extends EditorGui {

	private static final HashSet<WindowGui> GUI = new HashSet<>();

	public static void add(WindowGui gui) {
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

	@Override
	protected final void onDraw() {
		if(ImGui.begin(this.title(), this.flags())) {
			this.drawWindow();
		}
		ImGui.end();
	}

	protected abstract String title();

	protected int flags() {
		return 0;
	}

	protected abstract void drawWindow();
}
