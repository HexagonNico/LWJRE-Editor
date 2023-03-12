package gamma.engine.editor.gui;

import imgui.ImGui;

public class FileSystemGui extends EditorGui {

	public FileSystemGui() {
		super("File system");
	}

	@Override
	protected void drawGui() {
		ImGui.text("This is where the file system will be");
	}
}
