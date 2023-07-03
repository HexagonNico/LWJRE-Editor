package io.github.lwjre.editor.gui;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

/**
 * Class that represents a popup used to show an alert.
 * This kind of popup can be closed with the "Ok" button or by pressing escape or enter.
 *
 * @author Nico
 */
public class AlertPopupGui extends PopupModalGui {

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title Title of the popup
	 * @param content Content of the popup
	 */
	public AlertPopupGui(String title, String... content) {
		super(title, content);
	}

	@Override
	protected void onDrawPopup() {
		if(ImGui.button("Ok") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE) || ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			this.close();
		}
	}
}
