package gamma.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;

public class EditorGui {

	public EditorGui() {
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename(".gamma/editorLayout.ini"); // TODO: Check if .gamma directory exists and create it if it doesn't
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
	}

	public void render() {
		ImGui.newFrame();
		// TODO: Either make this invisible or use frame buffers
		ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.NoDockingInCentralNode);
		ImGui.begin("Test");
		ImGui.end();
//		this.guis.forEach(IEditorGui::draw);
		ImGui.render();
	}

	public void destroy() {
		ImGui.destroyContext();
	}
}
