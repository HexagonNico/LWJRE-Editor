package gamma.editor.gui;

public class CompileErrorPopupGui extends PopupModalGui {

	public static void showPopup() {
		EditorGui.get(CompileErrorPopupGui.class).ifPresent(PopupModalGui::show);
	}

	@Override
	protected String text() {
		return "Could not compile sources";
	}

	@Override
	protected boolean closeable() {
		return true;
	}
}
