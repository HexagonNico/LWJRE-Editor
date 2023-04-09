package gamma.editor.gui;

import gamma.editor.controls.EditorScene;
import imgui.ImGui;

/**
 * Gui component that represents the editor's main menu bar.
 *
 * @author Nico
 */
public class EditorMenuBar implements IGui {

	@Override
	public void draw() {
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				// TODO: Key combinations
				if(ImGui.menuItem("New scene", "Ctrl+N")) {
					System.out.println("New scene pressed!");
				}
				if(ImGui.menuItem("Save scene", "Ctrl+S")) {
					EditorScene.saveCurrent();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Edit")) {
				// TODO: Undo and redo
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Settings")) {

				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
	}
}
