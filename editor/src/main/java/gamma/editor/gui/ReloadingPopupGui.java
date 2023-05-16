package gamma.editor.gui;

public class ReloadingPopupGui extends PopupModalGui {

	public static void showPopup() {
		EditorGui.get(ReloadingPopupGui.class).ifPresent(PopupModalGui::show);
	}

	public static void hidePopup() {
		EditorGui.get(ReloadingPopupGui.class).ifPresent(PopupModalGui::hide);
	}

	@Override
	protected String text() {
		return "Reloading...";
	}
}
