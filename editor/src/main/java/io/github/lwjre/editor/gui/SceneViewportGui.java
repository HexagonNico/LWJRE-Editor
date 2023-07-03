package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import io.github.lwjre.editor.controllers.EditorCamera;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.controllers.RuntimeHelper;
import io.github.lwjre.editor.models.FrameBuffer;
import io.github.lwjre.engine.ApplicationProperties;
import io.github.lwjre.engine.debug.DebugRenderer;
import io.github.lwjre.engine.servers.RenderingServer;

/**
 * Class that represents the gui that shows the scene viewport.
 *
 * @author Nico
 */
public class SceneViewportGui extends WindowGui {

	// TODO: Update the size if the value is changed
	private final FrameBuffer frameBuffer = new FrameBuffer(
			ApplicationProperties.get("window.viewport.width", 1920),
			ApplicationProperties.get("window.viewport.height", 1080)
	);
	/** The editor camera */
	private final EditorCamera camera = new EditorCamera();

	/** Popup to show when the project is running */
	private final PopupModalGui runningPopup = new PopupModalGui("Running project");
	/** Popup to show if the project could not be compiled */
	private final AlertPopupGui errorPopup = new AlertPopupGui("Cannot run project", "There was an error compiling the project");

	/** Keeps track of whether the project is running */
	private boolean isRunning = false;

	@Override
	public void init() {

	}

	@Override
	protected String title() {
		return "Scene viewport";
	}

	@Override
	protected int flags() {
		return EditorScene.hasUnsavedChanges() ? ImGuiWindowFlags.UnsavedDocument : 0;
	}

	@Override
	public void draw() {
		this.runningPopup.draw();
		this.errorPopup.draw();
		super.draw();
	}

	@Override
	protected void drawWindow() {
		if(!this.isRunning) {
			if(ImGui.button("Run Project")) {
				this.isRunning = true;
				this.runningPopup.open();
				this.runningPopup.setContent("mvn install -DskipTests");
				RuntimeHelper.execute("mvn install -DskipTests").onExit(() -> {
					RuntimeHelper.execute("mvn exec:java").onExit(() -> this.isRunning = false);
					this.runningPopup.close();
				}).onError(this.errorPopup::open);
			}
		}
		this.frameBuffer.bindAndDraw(() -> {
			RenderingServer.setViewport(ApplicationProperties.get("window.viewport.width", 1920), ApplicationProperties.get("window.viewport.height", 1080));
			this.camera.update();
			RenderingServer.deptTest(true);
			RenderingServer.backFaceCulling(true);
			RenderingServer.clearScreen();
			RenderingServer.render();
			RenderingServer.lineWidth(3.0f);
			RenderingServer.fillPolygons(false);
			RenderingServer.backFaceCulling(false);
			DebugRenderer.render();
			RenderingServer.lineWidth(1.0f);
			RenderingServer.fillPolygons(true);
		});
		ImVec2 viewportSize = getLargestSizeForViewport();
		ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
		ImGui.setCursorPos(viewportPos.x, viewportPos.y);
		ImGui.image(this.frameBuffer.texture, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
	}

	/**
	 * Computes the viewport size.
	 *
	 * @return Size of the viewport
	 */
	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float aspectWidth = windowSize.x;
		float aspectRatio = ApplicationProperties.get("window.viewport.width", 1920.0f) / ApplicationProperties.get("window.viewport.height", 1080.0f);
		float aspectHeight = aspectWidth / aspectRatio;
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * aspectRatio;
		}
		return new ImVec2(aspectWidth, aspectHeight);
	}

	/**
	 * Computes the viewport position.
	 *
	 * @param aspectSize Viewport size
	 * @return Viewport position
	 */
	private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
		float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}

	@Override
	public void cleanUp() {
		this.frameBuffer.delete();
	}
}
