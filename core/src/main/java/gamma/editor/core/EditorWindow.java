package gamma.editor.core;

import gamma.engine.core.window.Window;
import gamma.editor.core.gui.FileSystemGui;
import gamma.editor.core.gui.TestGui;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.GL;

import java.util.List;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw glfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 gl3 = new ImGuiImplGl3();

	private static final List<Runnable> GUIS = List.of(
			TestGui::drawGui,
			FileSystemGui::drawGui
	);

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		GL.createCapabilities();
		ImGui.createContext();
		ImGui.getIO().setIniFilename(null);
		this.glfw.init(this.handle, true);
		this.gl3.init("#version 130");
	}

	@Override
	public void update() {
		this.glfw.newFrame();
		ImGui.newFrame();
		GUIS.forEach(Runnable::run);
		ImGui.render();
		this.gl3.renderDrawData(ImGui.getDrawData());
		super.update();
	}

	@Override
	public void destroy() {
		super.destroy();
		this.gl3.dispose();
		this.glfw.dispose();
		ImGui.destroyContext();
	}
}
