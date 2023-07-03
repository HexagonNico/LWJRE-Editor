package io.github.lwjre.editor.gui;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorClassLoader;

import java.util.HashSet;

/**
 * Class that represents the root gui that contains all other guis.
 *
 * @author Nico
 */
public final class RootGui implements EditorGui {

	/** Set of all the guis to show */
	private final HashSet<EditorGui> guis = new HashSet<>();
	/** Set to true when the gui needs to be reloaded */
	private boolean reload = false;

	@Override
	public void init() {
		if(ProjectPath.isInsideProject()) {
			EditorClassLoader.changeCurrentThreadClassLoader();
			this.guis.add(new FileSystemGui());
			InspectorGui inspector = new InspectorGui();
			this.guis.add(inspector);
			this.guis.add(new SceneTreeGui(inspector));
			this.guis.add(new EditorMenuGui());
			this.guis.add(new SceneViewportGui());
		} else {
			this.guis.add(new OpenProjectGui(this));
		}
		this.guis.forEach(EditorGui::init);
	}

	/**
	 * Requests to reload the gui.
	 * Called when changing between views.
	 */
	public void reloadGui() {
		this.reload = true;
	}

	@Override
	public void draw() {
		this.guis.forEach(EditorGui::draw);
		if(this.reload) {
			this.cleanUp();
			this.guis.clear();
			this.init();
			this.reload = false;
		}
	}

	@Override
	public void cleanUp() {
		this.guis.forEach(EditorGui::cleanUp);
	}
}
