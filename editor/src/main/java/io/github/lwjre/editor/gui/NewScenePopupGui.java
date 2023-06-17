package io.github.lwjre.editor.gui;

import io.github.lwjre.editor.DynamicLoader;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controls.EditorScene;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.FileUtils;
import io.github.lwjre.engine.utils.YamlSerializer;

import java.nio.file.Path;

public class NewScenePopupGui extends PopupModalGui {

	public static void showPopup() {
		GuiManager.get(NewScenePopupGui.class).show();
	}

	private String currentPath = "scenes/scene.yaml";
	private Class<?> currentRoot = Node.class;

	@Override
	protected void drawPopup() {
		ImGui.text("Create new scene");
		if(ImGui.beginTable("##createScene", 2, ImGuiTableFlags.SizingStretchProp)) {
			ImGui.tableSetupColumn("0", ImGuiTableColumnFlags.WidthStretch);
			ImGui.tableSetupColumn("1", ImGuiTableColumnFlags.WidthStretch);
			ImGui.tableNextColumn();
			ImGui.text("Path");
			ImGui.tableNextColumn();
			ImString ptr = new ImString(this.currentPath, 256);
			if(ImGui.inputText("##scenePath", ptr)) {
				this.currentPath = ptr.get();
			}
			ImGui.tableNextColumn();
			ImGui.text("Root node");
			ImGui.tableNextColumn();
			if(ImGui.beginCombo("##rootNode", this.currentRoot.getSimpleName())) {
				DynamicLoader.getNodeClasses().forEach(nodeClass -> {
					if(ImGui.selectable(nodeClass.getSimpleName())) {
						this.currentRoot = nodeClass;
					}
				});
				ImGui.endCombo();
			}
			ImGui.tableNextColumn();
			if(!FileUtils.resourceExists(this.actualPath()) && ImGui.button("Create new scene")) {
				NodeResource newScene = new NodeResource(this.currentRoot.getName());
				Path path = ProjectPath.resourcesFolder(this.actualPath());
				YamlSerializer.writeToFile(newScene, path.toString());
				EditorScene.changeScene(path);
				this.hide();
			}
			ImGui.tableNextColumn();
			if(ImGui.button("Back")) {
				this.hide();
			}
			ImGui.endTable();
			if(FileUtils.resourceExists(this.actualPath())) {
				ImGui.text("A resource already exists at the given path");
			}
		}
	}

	private String actualPath() {
		if(!this.currentPath.endsWith(".yaml") && !this.currentPath.endsWith(".yml")) {
			int index = this.currentPath.lastIndexOf('.');
			if(index != -1) {
				return this.currentPath.substring(0, index) + ".yaml";
			} else {
				return this.currentPath + ".yaml";
			}
		}
		return this.currentPath;
	}

	@Override
	protected String title() {
		return "New scene";
	}
}
