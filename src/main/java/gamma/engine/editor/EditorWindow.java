package gamma.engine.editor;

import gamma.engine.core.Window;
import gamma.engine.editor.gui.EditorGui;
import gamma.engine.editor.gui.FileSystemGui;
import gamma.engine.editor.gui.TestGui;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw glfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 gl3 = new ImGuiImplGl3();

	private final List<EditorGui> guis = List.of(
			new TestGui(),
			new FileSystemGui()
	);

	public EditorWindow() {
		super("Gamma Engine - Editor", 800, 450);
	}

	@Override
	public void makeContextCurrent() {
		super.makeContextCurrent();
		GL.createCapabilities();
		ImGui.createContext();
		ImGui.getIO().setIniFilename(null);
		ImGui.getIO().setConfigFlags(ImGuiConfigFlags.DockingEnable);
		this.glfw.init(this.handle, true);
		this.gl3.init("#version 130");
	}

	@Override
	public void update() {
		GL11.glClearColor(0.1f, 0.09f, 0.1f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		this.glfw.newFrame();
		ImGui.newFrame();
		this.guis.forEach(EditorGui::draw);
		ImGui.render();
		this.gl3.renderDrawData(ImGui.getDrawData());
		super.update();
	}

	@Override
	public void destroy() {
		this.gl3.dispose();
		this.glfw.dispose();
		ImGui.destroyContext();
		super.destroy();
	}
}
