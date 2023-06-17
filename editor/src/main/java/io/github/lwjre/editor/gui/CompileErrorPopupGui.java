package io.github.lwjre.editor.gui;

import imgui.ImGui;

public class CompileErrorPopupGui extends PopupModalGui {

	public static void showPopup() {
		GuiManager.get(CompileErrorPopupGui.class).show();
	}

	@Override
	protected void drawPopup() {
		ImGui.text("Could not compile sources");
		if(ImGui.button("Close", 120.0f, 20.0f)) {
			this.hide();
		}
	}

	@Override
	protected String title() {
		return "Error";
	}
}
