package io.github.lwjre.editor.gui;

import imgui.ImGui;
import io.github.lwjre.editor.EditorApplication;
import io.github.lwjre.editor.ProjectManagerState;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorScene;
import org.lwjgl.glfw.GLFW;

/**
 * The editor's main menu bar.
 *
 * @author Nico
 */
public class MainMenuBar implements GuiComponent {

	/** Shown when "New scene" is clicked */
	private final NewScenePopup newScenePopup;
	/** Shown when "Open scene" is clicked */
	private final OpenScenePopup openScenePopup;
	/** Needs to be opened when "Application settings" is clicked */
	private final ApplicationSettingsWindow applicationSettingsWindow;

	/**
	 * Constructs the main menu bar.
	 *
	 * @param newScenePopup Shown when "New scene" is clicked
	 * @param openScenePopup Shown when "Open scene" is clicked
	 * @param applicationSettingsWindow Needs to be opened when "Application settings" is clicked
	 */
	public MainMenuBar(NewScenePopup newScenePopup, OpenScenePopup openScenePopup, ApplicationSettingsWindow applicationSettingsWindow) {
		this.newScenePopup = newScenePopup;
		this.openScenePopup = openScenePopup;
		this.applicationSettingsWindow = applicationSettingsWindow;
	}

	@Override
	public void draw() {
		if(ImGui.beginMainMenuBar()) {
			if(ImGui.beginMenu("File")) {
				if(ImGui.menuItem("New scene", "Ctrl + N")) {
					this.newScenePopup.open();
				}
				if(ImGui.menuItem("Save", "Ctrl + S")) {
					EditorScene.saveScene();
				}
				if(ImGui.menuItem("Open", "Ctrl + O")) {
					this.openScenePopup.open();
				}
				ImGui.separator();
				if(ImGui.menuItem("Quit project", "Ctrl + Q")) {
					ProjectPath.setCurrent(".");
					EditorScene.changeScene(null, "");
					EditorApplication.changeState(new ProjectManagerState());
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
				if(ImGui.menuItem("Application settings")) {
					this.applicationSettingsWindow.open();
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
				this.newScenePopup.open();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_O)) {
				this.openScenePopup.open();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_S)) {
				EditorScene.saveScene();
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_E)) {

			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Q)) {
				ProjectPath.setCurrent(".");
				EditorScene.changeScene(null, "");
				EditorApplication.changeState(new ProjectManagerState());
			}
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Z)) {

				} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_Q)) {

				}
			}
		}
	}
}
