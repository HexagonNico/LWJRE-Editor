package gamma.editor.gui;

import java.util.HashSet;

public abstract class EditorGui {

	private static final HashSet<EditorGui> GUI = new HashSet<>();

	public static void add(EditorGui gui) {
		GUI.add(gui);
	}

	public static void remove(EditorGui gui) {
		GUI.remove(gui);
	}

	public static void clear() {
		GUI.clear();
	}

	public static void showProjectGui() {
		System.out.println("???");
		GUI.clear();
		InspectorGui inspectorGui = new InspectorGui();
		GUI.add(new FileSystemGui(inspectorGui));
		GUI.add(new SceneTreeGui(inspectorGui));
		GUI.add(inspectorGui);
		GUI.add(new SceneViewportGui());
		GUI.add(new EditorMenuGui());
	}

	public static void drawGui() {
		GUI.forEach(EditorGui::onDraw);
	}

	protected abstract void onDraw();
}
