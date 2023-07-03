package io.github.lwjre.editor.gui;

import imgui.ImGui;

/**
 * Class that represents a generic gui window.
 *
 * @author Nico
 */
public abstract class WindowGui implements EditorGui {

	@Override
	public void draw() {
		if(ImGui.begin(this.title(), this.flags())) {
			this.drawWindow();
		}
		ImGui.end();
	}

	/**
	 * Gets the window's title.
	 *
	 * @return The window's title
	 */
	protected abstract String title();

	/**
	 * Gets the window's flags.
	 *
	 * @see imgui.flag.ImGuiWindowFlags
	 *
	 * @return The window's flags
	 */
	protected int flags() {
		return 0;
	}

	/**
	 * Draws the window.
	 * Called from {@link WindowGui#draw()} if the window is open.
	 */
	protected abstract void drawWindow();
}
