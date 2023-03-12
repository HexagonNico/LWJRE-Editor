package gamma.engine.editor.gui;

import imgui.ImGui;

public abstract class EditorGui {

	private final String title;

	public EditorGui(String title) {
		this.title = title;
	}

	public final void draw() {
		ImGui.begin(this.title);
		this.drawGui();
		ImGui.end();
	}

	protected abstract void drawGui();
}
