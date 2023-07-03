package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

/**
 * Class that represents a popup modal.
 * Guis that can cause a popup to show up should contain an instance of a {@code PopupModalGui}
 * and call {@link PopupModalGui#draw()} in their {@link EditorGui#draw()} method.
 *
 * @author Nico
 */
public class PopupModalGui implements EditorGui {

	/** Title of the popup */
	private String title;
	/** Content of the popup */
	private String[] content;

	/** Set to true when the popup should be opened */
	private boolean openRequested = false;
	/** Set to true when the popup should be closed */
	private boolean closeRequested = false;

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title Title of the popup
	 * @param content Content of the popup
	 */
	public PopupModalGui(String title, String... content) {
		this.title = title;
		this.content = content;
	}

	@Override
	public void init() {

	}

	@Override
	public void draw() {
		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f, io.getDisplaySizeY() * 0.5f, ImGuiCond.Always, 0.5f, 0.5f);
		ImGui.setNextWindowSize(io.getDisplaySizeX() / 3.0f, io.getDisplaySizeY() / 3.0f, ImGuiCond.Always);
		if(ImGui.beginPopupModal(this.title, ImGuiWindowFlags.NoResize)) {
			for(String text : this.content) {
				ImGui.text(text);
			}
			this.onDrawPopup();
			if(this.closeRequested) {
				ImGui.closeCurrentPopup();
				this.closeRequested = false;
			}
			ImGui.endPopup();
		}
		if(this.openRequested) {
			ImGui.openPopup(this.title);
			this.openRequested = false;
		}
	}

	/**
	 * Used by classes that override {@link PopupModalGui} to draw to the popup.
	 */
	protected void onDrawPopup() {

	}

	/**
	 * Gets the popup's title.
	 *
	 * @return The popup's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the popup.
	 *
	 * @param title Title of the popup
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the content of the popup.
	 *
	 * @param content Content of the popup
	 */
	public void setContent(String... content) {
		this.content = content;
	}

	/**
	 * Requests to open the popup.
	 */
	public void open() {
		this.openRequested = true;
	}

	/**
	 * Requests to close the popup.
	 */
	public void close() {
		this.closeRequested = true;
	}

	@Override
	public void cleanUp() {

	}
}
