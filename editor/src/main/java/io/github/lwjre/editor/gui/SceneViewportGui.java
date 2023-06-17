package io.github.lwjre.editor.gui;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controls.EditorCamera;
import io.github.lwjre.editor.controls.EditorFrameBuffer;
import io.github.lwjre.editor.controls.EditorScene;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import io.github.lwjre.engine.debug.DebugRenderer;
import io.github.lwjre.engine.servers.RenderingServer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class SceneViewportGui extends WindowGui {

	private final EditorFrameBuffer frameBuffer = new EditorFrameBuffer(1920, 1080); // TODO: Use correct size
	private final EditorCamera camera = new EditorCamera();

	private Process runningProcess = null;

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
		if(this.runningProcess == null || !this.runningProcess.isAlive()) {
			if(ImGui.button("Run Project")) try {
				this.runningProcess = Runtime.getRuntime().exec("mvn clean install exec:java", null, ProjectPath.currentFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			ImGui.sameLine();
			if(ImGui.button("Run scene")) try {
				this.runningProcess = Runtime.getRuntime().exec("mvn clean install exec:java -Dexec.args=\"" + EditorScene.currentResource() + "\"", null, ProjectPath.currentFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.frameBuffer.bindAndDraw(() -> {
			GL11.glViewport(0, 0, 1920, 1080);
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

	@Override
	public void onEditorClosed() {
		this.frameBuffer.delete();
		if(this.runningProcess != null && this.runningProcess.isAlive()) {
			this.runningProcess.destroy();
		}
	}
}
