package gamma.editor.scene;

import gamma.editor.EditorUtils;
import gamma.editor.gui.IGui;
import gamma.editor.gui.InspectorGui;
import gamma.engine.scene.Entity;
import gamma.engine.scene.Scene;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;
import vecmatlib.vector.Vec2i;

public class SceneTreeGui implements IGui {

	private final InspectorGui inspector;

	private Entity selectedEntity;
	private String selectedName;

	private Entity renaming;
	private String newName;

	private final EntityClipboard entityClipboard = new EntityClipboard();

	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(5.0f, 15.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.FirstUseEver);
		if((ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) && this.selectedEntity != null) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_A, false)) {
				this.selectedEntity.addChild(new Entity());
			}
		}
		ImGui.begin("Scene tree");
		if(this.selectedEntity != null && this.selectedEntity.getParent() != null) {
			this.entityClipboard.listenForKeyBinds(this.selectedEntity, this.selectedName);
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE, false) && ImGui.isWindowFocused()) {
				this.selectedEntity.removeFromScene();
			}
		}
		this.drawEntity("root", Scene.getRoot(), true);
		if(this.renaming != null && this.renaming.getParent() != null && this.newName != null) {
			if(!this.newName.isEmpty()) {
				String actualName = EditorUtils.findUnusedName(this.newName, this.renaming.getParent());
				this.renaming.getParent().renameChild(this.renaming, actualName);
			}
			this.renaming = null;
			this.newName = null;
		}
		ImGui.end();
	}

	private void drawEntity(String name, Entity entity, boolean isRoot) {
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;
		if(entity.getChildCount() == 0)
			flags = flags | ImGuiTreeNodeFlags.Leaf;
		if(entity == this.inspector.entity)
			flags = flags | ImGuiTreeNodeFlags.Selected;
		if(this.entityClipboard.isInClipboard(entity))
			ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
		if(ImGui.treeNodeEx(name, flags, this.renaming != entity ? name : "")) {
			if(this.entityClipboard.isInClipboard(entity))
				ImGui.popStyleColor();
			this.doRenamingInput(name, entity);
			this.doSelectEntity(name, entity);
			this.doDragAndDrop(name, entity);
			this.doClickToRename(entity, isRoot);
			this.doRightClickMenu(name, entity, isRoot);
			entity.forEachChild((key, child) -> this.drawEntity(key, child, false));
			ImGui.treePop();
		}
	}

	private void doSelectEntity(String name, Entity entity) {
		if(ImGui.isItemClicked(0) || ImGui.isItemClicked(1)) {
			this.inspector.entity = entity;
			this.selectedEntity = entity;
			this.selectedName = name;
		}
	}

	private void doDragAndDrop(String name, Entity entity) {
		if(ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload("Entity", new Object[] {entity, name});
			ImGui.text(name);
			ImGui.endDragDropSource();
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
	}

	private void doClickToRename(Entity entity, boolean isRoot) {
		if(ImGui.isWindowFocused()) {
			if(ImGui.isItemHovered() && ImGui.isMouseClicked(0) && this.renaming != null) {
				this.renaming = null;
			}
			if(this.inspector.entity == entity && ImGui.isMouseDoubleClicked(0) && !isRoot) {
				this.renaming = entity;
			}
		}
	}

	private void doRightClickMenu(String name, Entity entity, boolean isRoot) {
		if(ImGui.beginPopupContextItem()) {
			if(ImGui.menuItem("Add child entity", "Ctrl+A")) {
				entity.addChild(new Entity());
			}
			if(!isRoot) {
				ImGui.separator();
				if(ImGui.menuItem("Cut", "Ctrl+X")) {
					this.entityClipboard.cut(entity, name);
				}
				if(ImGui.menuItem("Copy", "Ctrl+C")) {
					this.entityClipboard.copy(entity, name);
				}
				if(ImGui.menuItem("Paste", "Ctrl+V")) {
					this.entityClipboard.paste(entity);
				}
				ImGui.separator();
				if(ImGui.menuItem("Delete entity", "Delete")) {
					entity.removeFromScene();
				}
			}
			ImGui.endPopup();
		}
	}

	private void doRenamingInput(String name, Entity entity) {
		if(this.renaming == entity) {
			ImGui.sameLine();
			ImGui.setKeyboardFocusHere();
			ImString ptr = new ImString(name, 256);
			if(ImGui.inputText("##" + entity, ptr, ImGuiInputTextFlags.EnterReturnsTrue)) {
				this.newName = ptr.get();
			}
		}
	}
}
