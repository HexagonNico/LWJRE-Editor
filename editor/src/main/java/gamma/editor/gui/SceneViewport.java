package gamma.editor.gui;

import gamma.editor.EditorCamera;
import gamma.engine.ApplicationProperties;
import gamma.engine.rendering.DebugRenderer;
import gamma.engine.rendering.RenderingSystem;
import gamma.engine.resources.FrameBuffer;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;
import vecmatlib.vector.Vec2i;

public class SceneViewport implements IGui {

	// TODO: Get proper size
	private final FrameBuffer frameBuffer = new FrameBuffer(1920, 1080);

	private final EditorCamera editorCamera = new EditorCamera();

	@Override
	public void draw() {
		this.renderScene();
		// TODO: Proper viewport scaling
		GL11.glViewport(0, 0, 1920, 1080);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		this.renderViewport();
	}

	private void renderScene() {
		FrameBuffer.bind(this.frameBuffer);
		RenderingSystem.render();
		DebugRenderer.render();
		FrameBuffer.unbind();
	}

	private void renderViewport() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(10.0f + windowSize.x() / 8.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() - windowSize.x() / 4.0f - 20.0f, windowSize.y() - 30.0f, ImGuiCond.FirstUseEver);
		if(ImGui.begin("Scene Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse)) {
			this.editorCamera.update();
			ImVec2 viewportSize = getLargestSizeForViewport();
			ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
			ImGui.setCursorPos(viewportPos.x, viewportPos.y);
			ImGui.image(this.frameBuffer.texture, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
		}
		ImGui.end();
	}

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
