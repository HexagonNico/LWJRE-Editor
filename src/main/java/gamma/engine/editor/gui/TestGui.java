package gamma.engine.editor.gui;

import imgui.ImGui;

public class TestGui extends EditorGui {

	private boolean showText = false;

	public TestGui() {
		super("Test Gui");
	}

	@Override
	protected void drawGui() {
		if(ImGui.button("I am a button")) {
			this.showText = !this.showText;
		}
		if(this.showText) {
			ImGui.text("You clicked a button");
			ImGui.sameLine();
			if(ImGui.button("Stop showing text")) {
				this.showText = false;
			}
		}
	}
}
