package gamma.editor.gui;

import gamma.editor.controls.EditorScene;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class EditorMenuGui extends EditorGui {

	@Override
	protected void onDraw() {
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				if(ImGui.menuItem("Save", "Ctrl+S")) {
					EditorScene.saveScene();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Edit")) {
				if(ImGui.menuItem("Undo", "Ctrl+Z")) {

				}
				if(ImGui.menuItem("Redo", "Ctrl+Shift+Z")) {

				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Settings")) {
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_S)) {
				EditorScene.saveScene();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

			}
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

				}
			}
		}
	}
}
