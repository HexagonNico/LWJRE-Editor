package io.github.lwjre.editor.gui;

import imgui.ImGui;

import java.util.Objects;

/**
 * A basic popup that contains a single label.
 *
 * @author Nico
 */
public class BasicPopup implements GuiComponent {

	/** The popup's title */
	private String title = "Popup";
	/** Text to write in the popup's body */
	private String content = "Content";

	/** Set to true to open the popup */
	private boolean shouldOpen = false;
	/** Set to true to close the popup */
	private boolean shouldClose = false;

	@Override
	public void draw() {
		if(this.shouldOpen) {
			ImGui.openPopup(this.title);
			this.shouldOpen = false;
		}
		if(ImGui.beginPopupModal(this.title)) {
			ImGui.text(this.content);
			if(this.shouldClose) {
				ImGui.closeCurrentPopup();
				this.shouldClose = false;
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
	 */
	public void open() {
		this.shouldOpen = true;
		this.shouldClose = false;
	}

	/**
	 * Closes the popup.
	 */
	public void close() {
		this.shouldOpen = false;
		this.shouldClose = true;
	}
}
