package gamma.editor.gui;

/**
 * Interface used to represent a part of the editor's gui.
 * May contain other {@code IGui}s.
 *
 * @author Nico
 */
public interface IGui {

	/**
	 * Draws this gui using {@link imgui.ImGui}.
	 */
	void draw();
}
