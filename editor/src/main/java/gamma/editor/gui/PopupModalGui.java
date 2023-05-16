package gamma.editor.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public abstract class PopupModalGui extends EditorGui {

	private boolean hide = false;

	@Override
	protected final void onDraw() {
		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f, io.getDisplaySizeY() * 0.5f, ImGuiCond.Always, 0.5f, 0.5f);
		ImGui.setNextWindowSize(io.getDisplaySizeX() / 3.0f, io.getDisplaySizeY() / 3.0f, ImGuiCond.Always);
		if(ImGui.beginPopupModal(this.text(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar)) {
			ImGui.setCursorPosX((ImGui.getWindowSizeX() - ImGui.calcTextSize(this.text()).x) * 0.5f);
			ImGui.text(this.text());
			ImGui.setCursorPosX((ImGui.getWindowSizeX() - 120.0f) * 0.5f);
			if(this.closeable() && ImGui.button("Close", 120.0f, 20.0f)) {
				this.hide();
			}
			if(this.hide) {
				ImGui.closeCurrentPopup();
				this.hide = false;
			}
			ImGui.endPopup();
		}
	}

	protected abstract String text();

	protected boolean closeable() {
		return false;
	}

	public final void show() {
		ImGui.openPopup(this.text());
	}

	public final void hide() {
		this.hide = true;
	}
}
