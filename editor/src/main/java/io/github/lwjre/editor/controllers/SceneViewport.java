package io.github.lwjre.editor.controllers;

import io.github.hexagonnico.vecmatlib.vector.Vec2i;
import io.github.lwjre.editor.gui.SceneViewportWindow;
import io.github.lwjre.editor.models.ApplicationSettingsEditor;
import io.github.lwjre.editor.models.SceneFrameBuffer;

/**
 * Renders the currently active scene to a frame buffer and listens for changes in the viewport size.
 *
 * @author Nico
 */
public class SceneViewport {

	/** Needed to detect changes in the viewport size */
	private final ApplicationSettingsEditor applicationSettingsEditor;
	/** Needs to be updated when the viewport size changes */
	private final SceneViewportWindow sceneViewportWindow;

	/** Current viewport size */
	private Vec2i currentSize;
	/** Scene frame buffer */
	private SceneFrameBuffer frameBuffer;

	/**
	 * Constructs a scene viewport.
	 *
	 * @param applicationSettingsEditor Needed to detect changes in the viewport size
	 * @param sceneViewportWindow Needs to be updated when the viewport size changes
	 */
	public SceneViewport(ApplicationSettingsEditor applicationSettingsEditor, SceneViewportWindow sceneViewportWindow) {
		this.applicationSettingsEditor = applicationSettingsEditor;
		this.sceneViewportWindow = sceneViewportWindow;
	}

	/**
	 * Creates a {@link SceneFrameBuffer} and adds it to the {@link SceneViewportWindow}.
	 */
	public void init() {
		this.currentSize = (Vec2i) this.applicationSettingsEditor.get("window", "viewport");
		this.frameBuffer = new SceneFrameBuffer(this.currentSize);
		this.sceneViewportWindow.setScene(this.frameBuffer);
	}

	/**
	 * Renders the current scene to the {@link SceneFrameBuffer} and updates its size if a change in {@link ApplicationSettingsEditor} is detected.
	 */
	public void update() {
		if(!this.applicationSettingsEditor.get("window", "viewport").equals(this.currentSize)) {
			this.currentSize = (Vec2i) this.applicationSettingsEditor.get("window", "viewport");
			this.frameBuffer.cleanUp();
			this.frameBuffer = new SceneFrameBuffer(this.currentSize);
			this.sceneViewportWindow.setScene(this.frameBuffer);
		}
		this.frameBuffer.drawScene();
	}

	/**
	 * Deletes the current {@link SceneFrameBuffer}.
	 *
	 * @see SceneFrameBuffer#cleanUp()
	 */
	public void cleanUp() {
		this.frameBuffer.cleanUp();
	}
}
