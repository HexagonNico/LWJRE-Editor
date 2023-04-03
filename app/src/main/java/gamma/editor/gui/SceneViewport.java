package gamma.editor.gui;

import gamma.editor.EditorCamera;
import gamma.engine.rendering.DebugRenderer;
import gamma.engine.rendering.RenderingSystem;
import gamma.engine.resources.FrameBuffer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;

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
		ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
		this.editorCamera.update();
		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		ImGui.image(this.frameBuffer.texture, windowSize.x, windowSize.y, 0, 1, 1, 0);
		ImGui.end();
	}

	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / (16.0f / 9.0f);
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * (16.0f / 9.0f);
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
