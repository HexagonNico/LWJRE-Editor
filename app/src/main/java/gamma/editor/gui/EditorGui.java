package gamma.editor.gui;

import gamma.editor.inspector.InspectorGui;
import gamma.editor.scene.SceneTreeGui;

import java.util.ArrayList;

public final class EditorGui {

	private final ArrayList<IGui> guis = new ArrayList<>();

	public EditorGui() {
		this.guis.add(new EditorDockSpace());
		this.guis.add(new GameViewport());
		InspectorGui inspectorGui = new InspectorGui();
		this.guis.add(inspectorGui);
		this.guis.add(new SceneTreeGui(inspectorGui));
		this.guis.add(new FileSystemGui());
		this.guis.add(new EditorMenuBar());
	}

	public void renderAll() {
		this.guis.forEach(IGui::draw);
	}
}
