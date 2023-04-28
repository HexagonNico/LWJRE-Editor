package gamma.editor.gui;

import java.util.ArrayList;

/**
 * Groups all the gui components to show in the editor.
 *
 * @see EditorDockSpace
 * @see SceneViewportGui
 * @see InspectorGui
 * @see SceneTreeGui
 * @see FileSystemGui
 * @see EditorMenuBar
 *
 * @author Nico
 */
public final class EditorGui implements IGui {

	/** List of {@code IGui}s */
	private final ArrayList<IGui> guis = new ArrayList<>();

	public EditorGui() {
		this.guis.add(new EditorDockSpace());
		this.guis.add(new SceneViewportGui());
		InspectorGui inspectorGui = new InspectorGui();
		this.guis.add(inspectorGui);
		this.guis.add(new SceneTreeGui(inspectorGui));
		this.guis.add(new FileSystemGui());
		this.guis.add(new EditorMenuBar());
	}

	@Override
	public void draw() {
		this.guis.forEach(IGui::draw);
	}
}
