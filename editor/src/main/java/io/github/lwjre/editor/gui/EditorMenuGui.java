package io.github.lwjre.editor.gui;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorScene;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class EditorMenuGui implements EditorGui {

	private final NewScenePopupGui newScenePopupGui = new NewScenePopupGui("New scene...");
	private final OpenScenePopupGui openScenePopupGui = new OpenScenePopupGui("Open scene...");
	private final ProjectSettingsPopupGui projectSettingsPopupGui = new ProjectSettingsPopupGui("Project settings");

	private final RootGui rootGui;

	public EditorMenuGui(RootGui rootGui) {
		this.rootGui = rootGui;
	}

	@Override
	public void init() {

	}

	@Override
	public void draw() {
		this.newScenePopupGui.draw();
		this.openScenePopupGui.draw();
		this.projectSettingsPopupGui.draw();
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				if(ImGui.menuItem("New scene", "Ctrl + N")) {
					this.newScenePopupGui.open();
				}
				if(ImGui.menuItem("Save", "Ctrl + S")) {
					EditorScene.saveScene();
				}
				if(ImGui.menuItem("Open", "Ctrl + O")) {
					this.openScenePopupGui.open();
				}
				ImGui.separator();
				if(ImGui.menuItem("Quit project", "Ctrl + Q")) {
					ProjectPath.setCurrent(".");
					this.rootGui.reloadGui();
				}
				if(ImGui.menuItem("Quit", "Ctrl + Shift + Q")) {

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
			if(ImGui.beginMenu("Project")) {
				if(ImGui.menuItem("Project settings")) {
					this.projectSettingsPopupGui.open();
				}
				if(ImGui.menuItem("Export", "Ctrl + E")) {

				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Settings")) {
				if(ImGui.menuItem("Editor settings")) {

				}
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_N)) {
				this.newScenePopupGui.open();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_O)) {
				this.openScenePopupGui.open();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_S)) {
				EditorScene.saveScene();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_E)) {

			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Q)) {
				ProjectPath.setCurrent(".");
				this.rootGui.reloadGui();
			}
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

				} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Q)) {

				}
			}
		}
	}

	@Override
	public void cleanUp() {

	}
}
