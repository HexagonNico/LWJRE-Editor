package gamma.editor.gui;

import gamma.editor.EditorApplication;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiWindowFlags;

/**
 * Gui component that represents the recent projects list.
 *
 * @author Nico
 */
public class ProjectsGui implements IGui {

	@Override
	public void draw() {
		// Create an ImGui window that covers the whole GLFW window
		ImGuiViewport viewport = ImGui.getMainViewport();
		ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
		ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
		// Show the open project file dialog OR the projects window
		if(ImGuiFileDialog.display("Open project", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking, 400.0f, 300.0f, 1280.0f, 720.0f)) {
			// Open project when pressing ok
			if(ImGuiFileDialog.isOk()) {
				EditorApplication.setCurrentPath(ImGuiFileDialog.getCurrentPath());
				EditorApplication.setGui(new EditorGui());
			}
			// Close dialog
			ImGuiFileDialog.close();
		} else if(ImGui.begin("Projects", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking)) {
			// Open project button
			if(ImGui.button("Open project")) {
				ImGuiFileDialog.openDialog("Open project", "Chose directory", null, ".", ".", 1, 0, ImGuiFileDialogFlags.DontShowHiddenFiles);
			}
			// New project button
			ImGui.sameLine();
			if(ImGui.button("New project")) {
				System.out.println("!!!");
			}
			ImGui.end();
		}
	}
}
