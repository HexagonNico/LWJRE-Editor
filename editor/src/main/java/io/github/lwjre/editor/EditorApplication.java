package io.github.lwjre.editor;

import io.github.lwjre.editor.controls.EditorLayout;
import io.github.lwjre.editor.controls.EditorScene;
import io.github.lwjre.editor.gui.GuiManager;
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
		DynamicLoader.reloadDependencies();
		DynamicLoader.reloadProject();
	}

	@Override
	public void process() {
		DynamicLoader.listenForChanges();
		EditorScene.rootNode().editorProcess();
		ImGui.dockSpaceOverViewport();
		GuiManager.drawGui();
	}

	@Override
	protected void postRun() {
		GuiManager.onEditorClose();
		DynamicLoader.closeWatchService();
	}

	public static void main(String[] args) {
		Thread.currentThread().setContextClassLoader(new EditorClassLoader());
		EditorLayout.defaultLayout();
		launch(new EditorApplication());
	}
}
