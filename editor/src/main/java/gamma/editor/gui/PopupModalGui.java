package gamma.editor.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class PopupModalGui extends EditorGui {

	public static void show(String title, String text, boolean closeable) {
		EditorGui.add(new PopupModalGui(title, text, closeable));
	}

	public static void show(String title, String text) {
		EditorGui.add(new PopupModalGui(title, text));
	}

	public static void hide() {
		EditorGui.remove(PopupModalGui.class);
	}

	private final String title;
	private final String text;
	private final boolean closeable;

	public PopupModalGui(String title, String text, boolean closeable) {
		this.title = title;
		this.text = text;
		this.closeable = closeable;
	}

	public PopupModalGui(String title, String text) {
		this(title, text, false);
	}

	@Override
	protected void onDraw() {
		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowPos(io.getDisplaySizeX() * 0.5f, io.getDisplaySizeY() * 0.5f, ImGuiCond.Always, 0.5f, 0.5f);
		ImGui.setNextWindowSize(io.getDisplaySizeX() / 3.0f, io.getDisplaySizeY() / 3.0f, ImGuiCond.Always);
		if(ImGui.beginPopupModal(this.title, ImGuiWindowFlags.NoResize)) {
			ImGui.setCursorPosX((ImGui.getWindowSizeX() - ImGui.calcTextSize(this.text).x) * 0.5f);
			ImGui.text(this.text);
			ImGui.setCursorPosX((ImGui.getWindowSizeX() - 120.0f) * 0.5f);
			if(this.closeable && ImGui.button("Close", 120.0f, 20.0f)) {
				hide();
			}
			ImGui.endPopup();
		}
		ImGui.openPopup(this.title);
	}
}
