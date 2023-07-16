package io.github.lwjre.editor;

import imgui.ImGui;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.controllers.ProjectCompiler;
import io.github.lwjre.editor.controllers.SceneViewport;
import io.github.lwjre.editor.gui.*;
import io.github.lwjre.editor.models.ApplicationSettingsEditor;
import io.github.lwjre.editor.models.EditorClassLoader;

/**
 * Editor state when a project is open.
 *
 * @author Nico
 */
public class EditorState implements ApplicationState {

	private final ApplicationSettingsEditor applicationSettingsEditor = new ApplicationSettingsEditor();
	private final BasicPopup basicPopup = new BasicPopup();
	private final OpenScenePopup openScenePopup = new OpenScenePopup();
	private final NewScenePopup newScenePopup = new NewScenePopup();
	private final TextInputPopup textInputPopup = new TextInputPopup();
	private final AskConfirmationPopup askConfirmationPopup = new AskConfirmationPopup();
	private final InspectorWindow inspectorWindow = new InspectorWindow();
	private final SceneTreeWindow sceneTreeWindow = new SceneTreeWindow(this.inspectorWindow, this.textInputPopup, this.askConfirmationPopup);
	private final FileSystemWindow fileSystemWindow = new FileSystemWindow(this.textInputPopup, this.askConfirmationPopup);
	private final ApplicationSettingsWindow applicationSettingsWindow = new ApplicationSettingsWindow(this.applicationSettingsEditor);
	private final SceneViewportWindow sceneViewportWindow = new SceneViewportWindow(this.basicPopup);
	private final MainMenuBar mainMenuBar = new MainMenuBar(this.newScenePopup, this.openScenePopup, this.applicationSettingsWindow);
	private final ProjectCompiler projectCompiler = new ProjectCompiler(this.sceneTreeWindow, this.inspectorWindow, this.newScenePopup, this.basicPopup);
	private final SceneViewport sceneViewport = new SceneViewport(this.applicationSettingsEditor, this.sceneViewportWindow);

	@Override
	public void init() {
		Thread.currentThread().setContextClassLoader(new EditorClassLoader());
		this.applicationSettingsEditor.read();
		this.projectCompiler.init();
		this.sceneViewport.init();
	}

	@Override
	public void process() {
		EditorScene.rootNode().editorProcess();
		ImGui.dockSpaceOverViewport();
		this.sceneViewport.update();
		this.inspectorWindow.draw();
		this.sceneTreeWindow.draw();
		this.fileSystemWindow.draw();
		this.applicationSettingsWindow.draw();
		this.sceneViewportWindow.draw();
		this.mainMenuBar.draw();
		this.basicPopup.draw();
		this.openScenePopup.draw();
		this.newScenePopup.draw();
		this.textInputPopup.draw();
		this.askConfirmationPopup.draw();
	}

	@Override
	public void terminate() {
		this.projectCompiler.terminate();
		this.sceneViewport.cleanUp();
	}
}
