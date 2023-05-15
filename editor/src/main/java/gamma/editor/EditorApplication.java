package gamma.editor;

import gamma.editor.controls.EditorLayout;
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
		// TODO: Move this in another class
		InspectorGui inspectorGui = new InspectorGui();
		EditorGui.add(new FileSystemGui(inspectorGui));
		EditorGui.add(new SceneTreeGui(inspectorGui));
		EditorGui.add(inspectorGui);
		EditorGui.add(new SceneViewportGui());
		EditorGui.add(new EditorMenuGui());
		DynamicLoader.reloadDependencies();
		DynamicLoader.reloadProject();
	}

	@Override
	public void process() {
		DynamicLoader.listenForChanges();
		EditorScene.rootNode().editorProcess();
		ImGui.dockSpaceOverViewport();
		EditorGui.drawGui();
	}

	@Override
	protected void postRun() {
		DynamicLoader.closeWatchService();
	}

	public static void main(String[] args) {
		Thread.currentThread().setContextClassLoader(new EditorClassLoader());
		EditorLayout.defaultLayout();
		launch(new EditorApplication());
	}
}
