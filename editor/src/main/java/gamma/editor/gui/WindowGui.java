package gamma.editor.gui;

import imgui.ImGui;

public abstract class WindowGui implements EditorGui {

	@Override
	public void draw() {
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
