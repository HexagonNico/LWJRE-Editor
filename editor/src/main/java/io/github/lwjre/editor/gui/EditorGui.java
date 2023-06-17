package io.github.lwjre.editor.gui;

public interface EditorGui {

	void draw();

	default void onEditorClosed() {}
}
