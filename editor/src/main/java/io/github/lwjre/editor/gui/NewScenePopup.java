package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.type.ImString;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.engine.resources.NodeResource;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

/**
 * Popup to be shown when a new scene needs to be created.
 *
 * @author Nico
 */
public class NewScenePopup implements GuiComponent {

	/** Path to the future scene */
	private String sceneName = "newScene.yaml";
	/** Root of the future scene */
	private String rootType = "io.github.lwjre.engine.nodes.Node";

	/** List of class names */
	private Collection<String> nodeClasses;

	/** Set to true to open the popup */
	private boolean shouldOpen = false;

	@Override
	public void draw() {
		if(this.shouldOpen) {
			ImGui.openPopup("New scene");
			this.shouldOpen = false;
		}
		if(ImGui.beginPopupModal("New scene")) {
			ImString ptr = new ImString(this.sceneName, 256);
			if(ImGui.inputText("##sceneName", ptr)) {
				this.sceneName = ptr.get();
			}
			if(ImGui.beginCombo("##rootType", this.rootType.substring(this.rootType.lastIndexOf('.') + 1))) {
				this.nodeClasses.forEach(nodeClass -> {
					if(ImGui.selectable(nodeClass.substring(nodeClass.lastIndexOf('.') + 1), nodeClass.equals(this.rootType))) {
						this.rootType = nodeClass;
					}
				});
				ImGui.endCombo();
			}
			if(ImGui.button("Ok") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
				EditorScene.changeScene(new NodeResource(this.rootType), this.sceneName);
				EditorScene.saveScene();
				ImGui.closeCurrentPopup();
			}
			ImGui.sameLine();
			if(ImGui.button("Cancel") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				ImGui.closeCurrentPopup();
			}
			ImGui.endPopup();
		}
	}

	/**
	 * Sets the node classes.
	 *
	 * @param nodeClasses List of class names
	 */
	public void setNodeClasses(Collection<String> nodeClasses) {
		this.nodeClasses = nodeClasses;
	}

	/**
	 * Opens the popup.
	 */
	public void open() {
		this.sceneName = "newScene.yaml";
		this.rootType = "io.github.lwjre.engine.nodes.Node";
		this.shouldOpen = true;
	}
}
