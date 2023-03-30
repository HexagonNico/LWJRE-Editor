package gamma.editor;

import gamma.editor.gui.*;
import gamma.engine.core.window.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw glfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 gl3 = new ImGuiImplGl3();

	private final ArrayList<IEditorGui> guis = new ArrayList<>();

	private final EditorCamera camera = new EditorCamera();

	public EditorWindow() {
		this.guis.add(new EditorMenuBar());
		this.guis.add(new FileSystemGui());
		InspectorGui inspector = new InspectorGui();
		this.guis.add(new SceneTreeGui(inspector));
		this.guis.add(inspector);
	}

	@Override
	public void setupCallbacks() {
		GLFW.glfwSetWindowSizeCallback(this.handle, (window, width, height) -> GL11.glViewport(0, 0, width, height));
	}

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		GL.createCapabilities();
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename(".gamma/editorLayout.ini"); // TODO: Check if .gamma directory exists and create it if it doesn't
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		this.glfw.init(this.handle, true);
		this.gl3.init("#version 130");
	}

	@Override
	public void update() {
		this.glfw.newFrame();
		this.camera.editorUpdate();
		ImGui.newFrame();
		// TODO: Either make this invisible or use frame buffers
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.NoDockingInCentralNode);
		this.guis.forEach(IEditorGui::draw);
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
