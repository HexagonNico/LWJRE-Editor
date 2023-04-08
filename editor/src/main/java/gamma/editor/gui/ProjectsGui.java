package gamma.editor.gui;

import gamma.editor.EditorApplication;
import imgui.ImGui;
import imgui.ImGuiViewport;

public class ProjectsGui implements IGui {

	@Override
	public void draw() {
		ImGuiViewport viewport = ImGui.getMainViewport();
		ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
		ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
		if(ImGui.begin("Projects")) {
			if(ImGui.button("New project")) {
				EditorApplication.setGui(new EditorGui());
			}
		}
		ImGui.end();
	}
}
