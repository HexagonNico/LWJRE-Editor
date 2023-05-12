package gamma.editor;

import gamma.editor.controls.EditorScene;
import gamma.editor.gui.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;

public final class EditorApplication extends Application {

	@Override
	protected void configure(Configuration config) {
		config.setTitle("Gamma Engine - Editor");
	}

	@Override
	protected void preRun() {
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename("editorLayout.ini");
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigWindowsMoveFromTitleBarOnly(true);
		InspectorGui inspectorGui = new InspectorGui();
		EditorGui.add(new FileSystemGui(inspectorGui));
		EditorGui.add(new SceneTreeGui(inspectorGui));
		EditorGui.add(inspectorGui);
		EditorGui.add(new SceneViewportGui());
		EditorGui.add(new EditorMenuGui());
	}

	@Override
	public void process() {
		EditorScene.rootNode().editorProcess();
		ImGui.dockSpaceOverViewport();
		EditorGui.drawGui();
	}

	public static void main(String[] args) {
		Thread.currentThread().setContextClassLoader(new EditorClassLoader());
		launch(new EditorApplication());
	}
}
