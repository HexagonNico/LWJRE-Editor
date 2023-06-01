package gamma.editor.gui;

import imgui.ImGui;

public class NewScenePopupGui extends PopupModalGui {

	public static void showPopup() {
		GuiManager.get(NewScenePopupGui.class).show();
	}

	@Override
	protected void drawPopup() {
		ImGui.text("This is a test");
	}

	@Override
	protected String title() {
		return "New scene";
	}
}
