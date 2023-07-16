package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.models.SceneFrameBuffer;
import io.github.lwjre.editor.utils.EditorCamera;

import java.io.IOException;

/**
 * Class that represents the gui that shows the scene viewport.
 *
 * @author Nico
 */
public class SceneViewportWindow implements GuiComponent {

	/** Editor camera controller */
	private final EditorCamera editorCamera = new EditorCamera();

	/** Shown when running the project */
	private final BasicPopup runningProjectPopup;
	/** Set to true when the project is running */
	private boolean running = false;

	/** Needs to be rendered to the gui window */
	private SceneFrameBuffer sceneFrameBuffer;

	/**
	 * Constructs the scene viewport window.
	 *
	 * @param runningProjectPopup Shown when running the project
	 */
	public SceneViewportWindow(BasicPopup runningProjectPopup) {
		this.runningProjectPopup = runningProjectPopup;
	}

	@Override
	public void draw() {
		if(ImGui.begin("Scene viewport", EditorScene.hasUnsavedChanges() ? ImGuiWindowFlags.UnsavedDocument : 0)) {
			if(!this.running && ImGui.button("Run Project")) try {
				this.runningProjectPopup.setTitle("Running project");
				this.runningProjectPopup.setContent("mvn clean install -DskipTests");
				this.runningProjectPopup.open();
				this.running = true;
				Runtime.getRuntime().exec("mvn clean install -DskipTests", null, ProjectPath.current().toFile()).onExit().thenRun(() -> {
					try {
						this.runningProjectPopup.setContent("mvn exec:java");
						Runtime.getRuntime().exec("mvn exec:java", null, ProjectPath.current().toFile()).onExit().thenRun(() -> {
							this.runningProjectPopup.close();
							this.running = false;
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(this.sceneFrameBuffer != null) {
				ImVec2 viewportSize = getLargestSizeForViewport();
				ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
				ImGui.setCursorPos(viewportPos.x, viewportPos.y);
				ImGui.image(this.sceneFrameBuffer.texture, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
			}
			this.editorCamera.onUpdate(0.0f);
		}
		ImGui.end();
	}

	/**
	 * Sets the scene to be rendered on the gui window.
	 *
	 * @param frameBuffer Scene frame buffer
	 */
	public void setScene(SceneFrameBuffer frameBuffer) {
		this.sceneFrameBuffer = frameBuffer;
	}

	/**
	 * Computes the scaled viewport size.
	 *
	 * @return The size of the viewport texture
	 */
	private ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / this.sceneFrameBuffer.aspectRatio();
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * this.sceneFrameBuffer.aspectRatio();
		}
		return new ImVec2(aspectWidth, aspectHeight);
	}

	/**
	 * Computes the centered viewport position.
	 *
	 * @param aspectSize Size of the viewport
	 * @return The position of the viewport
	 */
	private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
		float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}
}
