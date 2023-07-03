package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Class that represents a popup that can take a string input.
 *
 * @author Nico
 */
public class TextInputPopup extends PopupModalGui {

	/** Current value */
	private String value = "";
	/** Action to run when the popup is closed */
	private Consumer<String> action = str -> {};

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title Title of the popup
	 * @param content Content of the popup
	 */
	public TextInputPopup(String title, String... content) {
		super(title, content);
	}

	@Override
	protected void onDrawPopup() {
		ImString ptr = new ImString(this.value, 256);
		ImGui.setKeyboardFocusHere();
		if(ImGui.inputText("##" + this.getTitle() + "#input", ptr, ImGuiInputTextFlags.EnterReturnsTrue)) {
			this.value = ptr.get();
			this.action.accept(this.value);
			this.close();
		}
		if(ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			this.close();
		}
	}

	/**
	 * Requests to open the popup.
	 *
	 * @param value The input's initial value
	 * @param action The action to run when the popup is closed
	 */
	public void open(String value, Consumer<String> action) {
		this.action = action != null ? action : str -> {};
		this.value = value != null ? value : "";
		this.open();
	}
}
