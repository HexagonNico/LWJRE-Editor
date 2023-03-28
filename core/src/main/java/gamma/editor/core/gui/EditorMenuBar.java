package gamma.editor.core.gui;

import gamma.editor.core.EditorScene;
import imgui.ImGui;

public class EditorMenuBar implements IEditorGui {

	@Override
	public void draw() {
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				if(ImGui.menuItem("New scene", "Ctrl+N")) {
					System.out.println("New scene pressed!");
				}
				if(ImGui.menuItem("Save scene", "Ctrl+S")) {
					EditorScene.saveCurrent();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Edit")) {

				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Settings")) {

				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
	}
}