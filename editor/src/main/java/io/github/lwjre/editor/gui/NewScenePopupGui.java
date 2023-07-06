package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.type.ImString;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.controllers.ProjectClassesLoader;
import io.github.lwjre.engine.resources.NodeResource;
import org.lwjgl.glfw.GLFW;

/**
 * Popup to be shown when a new scene needs to be created.
 *
 * @author Nico
 */
public class NewScenePopupGui extends PopupModalGui {

	/** Path to the future scene */
	private String sceneName = "newScene.yaml";
	/** Root of the future scene */
	private String rootType = "io.github.lwjre.engine.nodes.Node";

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title   Title of the popup
	 * @param content Content of the popup
	 */
	public NewScenePopupGui(String title, String... content) {
		super(title, content);
	}

	@Override
	protected void onDrawPopup() {
		ImString ptr = new ImString(this.sceneName, 256);
		if(ImGui.inputText("##" + this.getTitle() + "#sceneName", ptr)) {
			this.sceneName = ptr.get();
		}
		if(ImGui.beginCombo("##rootType", this.rootType.substring(this.rootType.lastIndexOf('.') + 1))) {
			ProjectClassesLoader.getNodeClasses().forEach(nodeClass -> {
				if(ImGui.selectable(nodeClass.getSimpleName(), nodeClass.getName().equals(this.rootType))) {
					this.rootType = nodeClass.getName();
				}
			});
			ImGui.endCombo();
		}
		if(ImGui.button("Ok") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			EditorScene.changeScene(new NodeResource(this.rootType), this.sceneName);
			EditorScene.saveScene();
			this.close();
		}
		ImGui.sameLine();
		if(ImGui.button("Cancel") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			this.close();
		}
	}

	@Override
	public void open() {
		this.sceneName = "newScene.yaml";
		this.rootType = "io.github.lwjre.engine.nodes.Node";
		super.open();
	}
}
