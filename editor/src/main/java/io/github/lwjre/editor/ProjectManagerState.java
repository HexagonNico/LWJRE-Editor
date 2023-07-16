package io.github.lwjre.editor;

import io.github.lwjre.editor.gui.OpenProjectWindow;

/**
 * State used when the editor is opened and a project needs to be opened.
 *
 * @author Nico
 */
public class ProjectManagerState implements ApplicationState {

	private final OpenProjectWindow openProjectWindow = new OpenProjectWindow();

	@Override
	public void init() {

	}

	@Override
	public void process() {
		this.openProjectWindow.draw();
	}

	@Override
	public void terminate() {

	}
}
