package io.github.lwjre.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;

/**
 * Main editor application class.
 *
 * @author Nico
 */
public final class EditorApplication extends Application {

	/** Current application state */
	private static ApplicationState currentState;
	/** Next application state */
	private static ApplicationState nextState = new ProjectManagerState();

	/**
	 * Changes the state of the application to the given one.
	 * The change is not immediate, but will happen on the next frame.
	 *
	 * @param state The state to change to
	 */
	public static void changeState(ApplicationState state) {
		nextState = state;
	}

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
	}

	@Override
	public void process() {
		if(nextState != null) {
			if(currentState != null) {
				currentState.terminate();
			}
			currentState = nextState;
			currentState.init();
			nextState = null;
		}
		currentState.process();
	}

	@Override
	protected void postRun() {
		if(currentState != null) {
			currentState.terminate();
		}
	}

	public static void main(String[] args) {
		launch(new EditorApplication());
	}
}
