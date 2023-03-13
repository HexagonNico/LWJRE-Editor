package gamma.engine.editor.gui;

import imgui.ImGui;

public class TestGui {

	private static boolean showText = false;

	public static void drawGui() {
		ImGui.begin("Test Gui");
		if(ImGui.button("I am a button")) {
			showText = !showText;
		}
		if(showText) {
			ImGui.text("You clicked a button");
			ImGui.sameLine();
			if(ImGui.button("Stop showing text")) {
				showText = false;
			}
		}
		ImGui.end();
	}
}
