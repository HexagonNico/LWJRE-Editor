package gamma.editor;

import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw glfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 gl3 = new ImGuiImplGl3();

	private final EditorGui editorGui = new EditorGui();

	public EditorWindow() {
		super("Gamma Engine - Editor", 1280, 720);
	}

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		this.glfw.init(this.handle, true);
		this.gl3.init("#version 130");
	}

	@Override
	public void update() {
		this.glfw.newFrame();
		this.editorGui.render();
		this.gl3.renderDrawData(ImGui.getDrawData());
		super.update();
	}

	@Override
	public void destroy() {
		this.gl3.dispose();
		this.glfw.dispose();
		this.editorGui.destroy();
		super.destroy();
	}
}
