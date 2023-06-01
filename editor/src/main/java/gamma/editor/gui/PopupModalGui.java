package gamma.editor.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public abstract class PopupModalGui implements EditorGui {

	private Visibility visibility = Visibility.HIDDEN;

	@Override
	public void draw() {
		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f, io.getDisplaySizeY() * 0.5f, ImGuiCond.Always, 0.5f, 0.5f);
		ImGui.setNextWindowSize(io.getDisplaySizeX() / 3.0f, io.getDisplaySizeY() / 3.0f, ImGuiCond.Always);
		if(ImGui.beginPopupModal(this.title(), ImGuiWindowFlags.NoResize)) {
			this.drawPopup();
			if(this.visibility == Visibility.HIDE) {
				ImGui.closeCurrentPopup();
				this.visibility = Visibility.HIDDEN;
			}
			ImGui.endPopup();
		}
		if(this.visibility == Visibility.SHOW) {
			ImGui.openPopup(this.title());
			this.visibility = Visibility.VISIBLE;
		}
	}

	public final void show() {
		this.visibility = Visibility.SHOW;
	}

	public final void hide() {
		this.visibility = Visibility.HIDE;
	}

	protected abstract void drawPopup();

	protected abstract String title();

	private enum Visibility {
		VISIBLE,
		SHOW,
		HIDE,
		HIDDEN
	}
}
