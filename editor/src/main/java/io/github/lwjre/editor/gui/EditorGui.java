package io.github.lwjre.editor.gui;

/**
 * Base interface to represent an editor gui component.
 *
 * @author Nico
 */
public interface EditorGui {

	/**
	 * Called when the gui started being shown.
	 */
	void init();

	/**
	 * Called when the gui is drawn.
	 */
	void draw();

	/**
	 * Called when the gui is being deleted.
	 */
	void cleanUp();
}
