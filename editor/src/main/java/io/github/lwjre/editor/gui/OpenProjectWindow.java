package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import io.github.lwjre.editor.EditorApplication;
import io.github.lwjre.editor.EditorState;
import io.github.lwjre.editor.ProjectPath;

/**
 * File dialog gui shown when the editor is opened.
 * Asks the user to select a folder containing the project.
 *
 * @author Nico
 */
public class OpenProjectWindow implements GuiComponent {

	/** Makes sure that {@link ImGuiFileDialog#openDialog} is only called once */
	private boolean shouldOpen = true;

	@Override
	public void draw() {
		if(this.shouldOpen) {
			ImGuiFileDialog.openDialog("##openProject", "Open Project", null, ".", ".", 1, 0, ImGuiFileDialogFlags.DontShowHiddenFiles);
			this.shouldOpen = false;
		}
		ImGui.setNextWindowPos(0.0f, 0.0f);
		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY(), ImGuiCond.Always);
		if(ImGuiFileDialog.display("##openProject", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize, 0.0f, 0.0f, 1920.0f, 1080.0f)) {
			if(ImGuiFileDialog.isOk()) {
				// TODO: Check if the folder is valid
				ProjectPath.setCurrent(ImGuiFileDialog.getCurrentPath());
				EditorApplication.changeState(new EditorState());
			} else {
				ImGuiFileDialog.close();
			}
			this.shouldOpen = true;
		}
	}
}
