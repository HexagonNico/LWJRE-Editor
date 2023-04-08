package gamma.editor.gui;

import gamma.editor.EditorApplication;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.extension.imguifiledialog.ImGuiFileDialog;

public class ProjectsGui implements IGui {

	@Override
	public void draw() {
		ImGuiViewport viewport = ImGui.getMainViewport();
		ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
		ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
		if(ImGui.begin("Projects")) {
			if(ImGui.button("Open project")) {
				ImGuiFileDialog.openDialog("Open project", "Chose directory", null, ".", ".", 1, 0, 0);
			}
			ImGui.sameLine();
			if(ImGui.button("New project")) {
				System.out.println("!!!");
			}
		}
		ImGui.end();
		if(ImGuiFileDialog.display("Open project", 0, 400.0f, 300.0f, 1280.0f, 720.0f)) {
			if(ImGuiFileDialog.isOk()) {
				EditorApplication.setCurrentPath(ImGuiFileDialog.getCurrentPath());
				EditorApplication.setGui(new EditorGui());
				ImGuiFileDialog.close();
			}
		}
	}
}
