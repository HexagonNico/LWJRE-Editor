package gamma.editor.gui;

import gamma.editor.controls.EditorCamera;
import gamma.editor.controls.EditorFrameBuffer;
import gamma.editor.controls.EditorScene;
import gamma.engine.rendering.RenderingSystem;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;

public class SceneViewportGui extends WindowGui {

	private final EditorFrameBuffer frameBuffer = new EditorFrameBuffer(1920, 1080); // TODO: Use correct size
	private final EditorCamera camera = new EditorCamera();

	@Override
	protected String title() {
		return "Scene viewport";
	}

	@Override
	protected int flags() {
		return EditorScene.hasUnsavedChanges() ? ImGuiWindowFlags.UnsavedDocument : 0;
	}

	@Override
	protected void drawWindow() {
		this.frameBuffer.bindAndDraw(() -> {
			GL11.glViewport(0, 0, 1920, 1080);
			this.camera.update();
			RenderingSystem.render();
			RenderingSystem.clearRenderer();
		});
		// TODO: Delete the frame buffer when the application is closed
		ImVec2 viewportSize = getLargestSizeForViewport();
		ImVec2 viewportPos = getCenteredPositionForViewport(viewportSize);
		ImGui.setCursorPos(viewportPos.x, viewportPos.y);
		ImGui.image(this.frameBuffer.texture, viewportSize.x, viewportSize.y, 0, 1, 1, 0);
	}

	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / (1920.0f / 1080.0f); // TODO: Use correct size
		if(aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * (1920.0f / 1080.0f);
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
