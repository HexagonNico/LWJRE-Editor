package io.github.lwjre.editor.gui;

import imgui.ImGui;

public class ReloadingPopupGui extends PopupModalGui {

	public static void showPopup() {
		GuiManager.get(ReloadingPopupGui.class).show();
	}

	public static void hidePopup() {
		GuiManager.get(ReloadingPopupGui.class).hide();
	}

	@Override
	protected void drawPopup() {
		ImGui.text("Reloading classes");
	}

	@Override
	protected String title() {
		return "Reloading...";
	}
}
