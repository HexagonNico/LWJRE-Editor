package gamma.editor;

import gamma.editor.gui.IGui;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

/**
 * Represent the editor's window. Uses {@link Window} to use GLFW.
 *
 * @author Nico
 */
public class EditorWindow extends Window {

	/** ImGui GLFW handle */
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	/** ImGui OpenGL handle */
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	/**
	 * Creates the editor window.
	 *
	 * @see Window#Window()
	 */
	public EditorWindow() {
		super("Gamma Engine - Editor", 1280, 720);
	}

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename("editorLayout.ini"); // TODO: This might go in another class
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigWindowsMoveFromTitleBarOnly(true);
		this.imGuiGlfw.init(this.handle, true);
		this.imGuiGl3.init("#version 130");
	}

	/**
	 * Renders the given root gui.
	 *
	 * @param rootGui {@link IGui} that contains all others
	 */
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
