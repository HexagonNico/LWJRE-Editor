package gamma.editor.gui;

import gamma.editor.DynamicLoader;
import gamma.editor.ProjectPath;
import gamma.editor.controls.EditorScene;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.YamlSerializer;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

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
			if(ImGui.button("Create new scene")) {
				NodeResource newScene = new NodeResource(this.currentRoot.getName());
				if(!this.currentPath.endsWith(".yaml") && !this.currentPath.endsWith(".yml")) {
					int index = this.currentPath.lastIndexOf('.');
					if(index != -1) {
						this.currentPath = this.currentPath.substring(0, index) + ".yaml";
					} else {
						this.currentPath = this.currentPath + ".yaml";
					}
				}
				Path path = ProjectPath.resourcesFolder(this.currentPath);
				YamlSerializer.writeToFile(newScene, path.toString());
				EditorScene.changeScene(path);
				this.hide();
			}
			ImGui.tableNextColumn();
			if(ImGui.button("Back")) {
				this.hide();
			}
			ImGui.endTable();
		}
	}

	@Override
	protected String title() {
		return "New scene";
	}
}
