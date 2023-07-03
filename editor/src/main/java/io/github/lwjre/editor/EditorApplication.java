package io.github.lwjre.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.controllers.RuntimeHelper;
import io.github.lwjre.editor.gui.RootGui;

public final class EditorApplication extends Application {

	private final RootGui rootGui = new RootGui();

	@Override
	protected void configure(Configuration config) {
		config.setTitle("LWJRE - Editor");
	}

	@Override
	protected void preRun() {
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename("editorLayout.ini");
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigWindowsMoveFromTitleBarOnly(true);
		this.rootGui.init();
	}

	@Override
	public void process() {
		RuntimeHelper.process();
		EditorScene.rootNode().editorProcess();
		ImGui.dockSpaceOverViewport();
		this.rootGui.draw();
	}

	@Override
	protected void postRun() {
		RuntimeHelper.terminate();
		this.rootGui.cleanUp();
	}

	public static void main(String[] args) {
		launch(new EditorApplication());
	}
}
