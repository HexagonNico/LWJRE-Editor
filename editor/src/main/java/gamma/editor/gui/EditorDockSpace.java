package gamma.editor.gui;

import imgui.ImGui;

public class EditorDockSpace implements IGui {

	@Override
	public void draw() {
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
	}
}
