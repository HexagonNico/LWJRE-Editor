package gamma.editor.core;

import gamma.editor.core.gui.FileSystemGui;
import gamma.editor.core.gui.IEditorGui;
import gamma.editor.core.gui.InspectorGui;
import gamma.editor.core.gui.SceneTreeGui;
import gamma.engine.core.window.Window;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

public class EditorWindow extends Window {

	private final ImGuiImplGlfw glfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 gl3 = new ImGuiImplGl3();

	private final ArrayList<IEditorGui> guis = new ArrayList<>();

	public EditorWindow() {
		this.guis.add(new FileSystemGui());
		InspectorGui inspector = new InspectorGui();
		this.guis.add(new SceneTreeGui(inspector));
		this.guis.add(inspector);
	}

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
