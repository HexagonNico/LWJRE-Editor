package gamma.editor;

import gamma.editor.gui.IGui;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	public EditorWindow() {
		super("Gamma Engine - Editor", 1280, 720);
	}

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO(); // TODO: Move this into EditorProperties class
		io.setIniFilename(".gamma/editorLayout.ini"); // TODO: Check if .gamma directory exists and create it if it doesn't
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigWindowsMoveFromTitleBarOnly(true);
		this.imGuiGlfw.init(this.handle, true);
		this.imGuiGl3.init("#version 130");
	}

	public void renderGui(IGui rootGui) {
		this.imGuiGlfw.newFrame();
		ImGui.newFrame();
		rootGui.draw();
		ImGui.render();
		this.imGuiGl3.renderDrawData(ImGui.getDrawData());
	}

	@Override
	public void destroy() {
		this.imGuiGl3.dispose();
		this.imGuiGlfw.dispose();
		ImGui.destroyContext();
		super.destroy();
	}
}
