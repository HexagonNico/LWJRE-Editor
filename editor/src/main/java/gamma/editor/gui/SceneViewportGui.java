package gamma.editor.gui;

import gamma.editor.controls.EditorCamera;
import gamma.engine.ApplicationProperties;
import gamma.engine.rendering.DebugRenderer;
import gamma.engine.rendering.FrameBuffer;
import gamma.engine.rendering.RenderingSystem;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;
import vecmatlib.vector.Vec2i;

/**
 * Gui component that represents the scene viewport rendered inside an ImGui window.
 *
 * @author Nico
 */
public class SceneViewportGui implements IGui {

	/** Used to render the scene to a texture before rendering it to the window */
	private final FrameBuffer frameBuffer;

	/** The editor camera is updated when rendering the viewport */
	private final EditorCamera editorCamera;

	/**
	 * Creates the scene viewport gui.
	 * Must be called after {@link Window#makeContextCurrent()} because of the {@link FrameBuffer}.
	 */
	public SceneViewportGui() {
		// TODO: Get proper size
		this.frameBuffer = new FrameBuffer(1920, 1080);
		this.editorCamera = new EditorCamera();
	}

	@Override
	public void draw() {
		this.renderScene();
		// TODO: Proper viewport scaling
		GL11.glViewport(0, 0, 1920, 1080);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		this.renderViewport();
	}

	/**
	 * Renders the whole scene to the viewport's frame butter.
	 */
	private void renderScene() {
		FrameBuffer.bind(this.frameBuffer);
		RenderingSystem.render();
		DebugRenderer.render();
		FrameBuffer.unbind();
	}

	/**
	 * Renders the viewport in an ImGui window.
	 */
	private void renderViewport() {
		// Initial window size
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(10.0f + windowSize.x() / 8.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() - windowSize.x() / 4.0f - 20.0f, windowSize.y() - 30.0f, ImGuiCond.FirstUseEver);
		// Show viewport window
		if(ImGui.begin("Scene Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse)) {
			// Update the camera
			this.editorCamera.update();
			// Center the viewport inside the ImGui window
			ImVec2 viewportSize = getLargestSizeForViewport();
			ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
			ImGui.setCursorPos(viewportPos.x, viewportPos.y);
			// Render the frame buffer
			ImGui.image(this.frameBuffer.texture, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
		}
		ImGui.end();
	}

	/**
	 * Gets the largest possible size of the viewport for correct scaling.
	 *
	 * @return Viewport size
	 */
	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / (ApplicationProperties.get("window/viewport/width", 0.0f) / ApplicationProperties.get("window/viewport/height", 1.0f));
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * (ApplicationProperties.get("window/viewport/width", 0.0f) / ApplicationProperties.get("window/viewport/height", 1.0f));
		}
		return new ImVec2(aspectWidth, aspectHeight);
	}

	/**
	 * Gets a centered position for the viewport in the ImGui window.
	 *
	 * @param aspectSize Size of the viewport
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
}
