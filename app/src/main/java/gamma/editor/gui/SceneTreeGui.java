package gamma.editor.gui;

import gamma.engine.scene.Entity;
import gamma.engine.scene.Scene;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import vecmatlib.vector.Vec2i;

public class SceneTreeGui implements IEditorGui {

	private final InspectorGui inspector;

	private Entity renaming;
	private String newName;

	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(5.0f, 15.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.FirstUseEver);
		ImGui.begin("Scene tree");
		this.drawEntity("root", Scene.getCurrent().root, true);
		if(this.renaming != null && this.renaming.getParent() != null && this.newName != null) {
			this.renaming.getParent().renameChild(this.renaming, this.newName);
			this.renaming = null;
			this.newName = null;
		}
		ImGui.end();
	}

	private void drawEntity(String name, Entity entity, boolean isRoot) {
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;
		if(entity.getChildCount() == 0)
			flags = flags | ImGuiTreeNodeFlags.Leaf;
		if(ImGui.treeNodeEx(name, flags, this.renaming != entity ? name : "")) {
			if(this.renaming == entity) {
				ImGui.sameLine();
				ImString ptr = new ImString(name, 256);
				if(ImGui.inputText("##" + entity, ptr, ImGuiInputTextFlags.EnterReturnsTrue)) {
					this.newName = ptr.get();
				}
				name = ptr.get();
			}
			if(ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("Entity", new Object[] {entity, name});
				ImGui.text(name);
				ImGui.endDragDropSource();
			}
			if(ImGui.isItemClicked()) {
				this.inspector.setEntity(entity);
			}
			if(ImGui.beginDragDropTarget()) {
				Object[] payload = ImGui.acceptDragDropPayload("Entity");
				if(payload != null && payload[0] instanceof Entity entityToMove && payload[1] instanceof String key) {
					Entity parent = entity.getParent();
					while(parent != null && parent != entityToMove)
						parent = parent.getParent();
					if(parent != entityToMove && !entityToMove.hasParent(entity)) {
						String newKey = key;
						int i = 1;
						while(entity.hasChild(newKey))
							newKey = key + i;
						entityToMove.setParent(newKey, entity);
					}
				}
				ImGui.endDragDropTarget();
			}
			if(ImGui.isItemHovered() && ImGui.isMouseClicked(0) && !isRoot) {
				this.renaming = entity;
			}
			entity.forEachChild((key, child) -> this.drawEntity(key, child, false));
			ImGui.treePop();
		}
	}
}
