package gamma.editor.gui;

public interface EditorGui {

	void draw();

	default void onEditorClosed() {}
}
