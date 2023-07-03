package io.github.lwjre.editor.gui;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

/**
 * Class that represents a popup that asks to confirm an action.
 *
 * @author Nico
 */
public class ConfirmPopupGui extends PopupModalGui {

	/** Action to run when pressing "Ok" */
	private Runnable onOk = () -> {};
	/** Action to run when pressing "Cancel" */
	private Runnable onCancel = () -> {};

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title Title of the popup
	 * @param content Content of the popup
	 */
	public ConfirmPopupGui(String title, String... content) {
		super(title, content);
	}

	@Override
	protected void onDrawPopup() {
		if(ImGui.button("Ok") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			this.onOk.run();
			this.close();
		}
		ImGui.sameLine();
		if(ImGui.button("Cancel") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			this.onCancel.run();
			this.close();
		}
	}

	/**
	 * Requests to open the popup.
	 *
	 * @param onOk Action to run when pressing "Ok"
	 */
	public void open(Runnable onOk) {
		this.open(onOk, () -> {});
	}

	/**
	 * Requests to open the popup.
	 *
	 * @param onOk Action to run when pressing "Ok"
	 * @param onCancel Action to run when pressing "Cancel"
	 */
	public void open(Runnable onOk, Runnable onCancel) {
		this.onOk = onOk != null ? onOk : () -> {};
		this.onCancel = onCancel != null ? onCancel : () -> {};
		this.open();
	}
}
