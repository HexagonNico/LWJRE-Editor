package gamma.editor.gui;

import gamma.engine.core.scene.Entity;
import gamma.engine.core.scene.Scene;
import gamma.engine.core.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import vecmatlib.vector.Vec2i;

public class SceneTreeGui implements IEditorGui {

	private final InspectorGui inspector;

	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(5.0f, 15.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.FirstUseEver);
		ImGui.begin("Scene tree");
		drawEntity("root", Scene.getCurrent().root);
		ImGui.end();
	}

	private void drawEntity(String name, Entity entity) {
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen;
		if(entity.getChildCount() == 0)
			flags = flags | ImGuiTreeNodeFlags.Leaf;
		if(ImGui.treeNodeEx(name, flags, name)) {
			if(ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("Entity", entity);
				ImGui.text(name);
				ImGui.endDragDropSource();
			}
			if(ImGui.isItemClicked()) {
				this.inspector.setEntity(entity);
			}
			if(ImGui.beginDragDropTarget()) {
				Object payload = ImGui.acceptDragDropPayload("Entity");
				if(payload instanceof Entity entityToMove) {
					System.out.println("You dragged " + entityToMove + " on " + entity);
				}
				ImGui.endDragDropTarget();
			}
			entity.forEachChild(this::drawEntity);
			ImGui.treePop();
		}
	}
}
