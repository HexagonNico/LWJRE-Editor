package io.github.lwjre.editor.gui;

import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

/**
 * A popup modal that asks confirmation about something.
 *
 * @author Nico
 */
public class AskConfirmationPopup implements GuiComponent {

	/** The popup's title */
	private String title = "Confirm";
	/** Text to write in the popup's body */
	private String content = "";

	/** Action to run when the "Confirm" button is pressed */
	private Runnable onConfirm = () -> {};
	/** Set to true to open the popup */
	private boolean shouldOpen = false;

	@Override
	public void draw() {
		if(this.shouldOpen) {
			ImGui.openPopup(this.title);
			this.shouldOpen = false;
		}
		if(ImGui.beginPopupModal(this.title)) {
			ImGui.text(this.content);
			if(ImGui.button("Confirm") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
				this.onConfirm.run();
				ImGui.closeCurrentPopup();
			}
			ImGui.sameLine();
			if(ImGui.button("Cancel") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				ImGui.closeCurrentPopup();
			}
			ImGui.endPopup();
		}
	}

	/**
	 * Sets the title of the popup.
	 *
	 * @param title The title of the popup (must not be null)
	 */
	public void setTitle(String title) {
		this.title = Objects.requireNonNull(title);
	}

	/**
	 * Sets the content of the popup.
	 *
	 * @param content The content of the popup (must not be null)
	 */
	public void setContent(String content) {
		this.content = Objects.requireNonNull(content);
	}

	/**
	 * Opens the popup.
	 *
	 * @param onConfirm Action to run when the "Confirm" button is pressed
	 */
	public void open(Runnable onConfirm) {
		this.onConfirm = Objects.requireNonNull(onConfirm);
		this.shouldOpen = true;
	}
}
