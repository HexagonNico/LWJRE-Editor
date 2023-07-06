package io.github.lwjre.editor.gui;

import io.github.lwjre.editor.controllers.EditorScene;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class EditorMenuGui implements EditorGui {

	private final NewScenePopupGui newScenePopupGui = new NewScenePopupGui("New scene...");

	@Override
	public void init() {

	}

	@Override
	public void draw() {
		this.newScenePopupGui.draw();
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				if(ImGui.menuItem("New scene", "Ctrl + N")) {
					this.newScenePopupGui.open();
				}
				if(ImGui.menuItem("Save", "Ctrl + S")) {
					EditorScene.saveScene();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Edit")) {
				if(ImGui.menuItem("Undo", "Ctrl + Z")) {

				}
				if(ImGui.menuItem("Redo", "Ctrl + Shift + Z")) {

				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Settings")) {
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_N)) {
				this.newScenePopupGui.open();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_S)) {
				EditorScene.saveScene();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

			}
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

				}
			}
		}
	}

	@Override
	public void cleanUp() {

	}
}
