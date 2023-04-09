package gamma.editor.gui;

import imgui.ImGui;

/**
 * Gui component that represents the editor's main dock space.
 *
 * @author Nico
 */
public class EditorDockSpace implements IGui {

	@Override
	public void draw() {
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
	}
}
