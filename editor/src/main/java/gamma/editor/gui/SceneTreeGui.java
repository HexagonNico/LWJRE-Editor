package gamma.editor.gui;

import gamma.editor.controls.EditorScene;
import gamma.editor.controls.EntityClipboard;
import gamma.editor.controls.EntityPayload;
import gamma.editor.controls.EntityRenamer;
import gamma.engine.input.Keyboard;
import gamma.engine.scene.Entity;
import gamma.engine.scene.EntityResource;
import gamma.engine.scene.Scene;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import vecmatlib.vector.Vec2i;

/**
 * Gui component that represents the scene tree showing the entities in the scene.
 *
 * @author Nico
 */
public class SceneTreeGui implements IGui {

	/** Needed to set the current entity when one is selected */
	private final InspectorGui inspector;

	private final EntityRenamer renamer = new EntityRenamer();
	private EntityPayload payload = null;

	/** Used to handle copy/cut/paste of entities in the scene tree */
	private final EntityClipboard entityClipboard = new EntityClipboard();

	/**
	 * Creates the scene tree gui.
	 *
	 * @param inspector Needed to set the current entity when one is selected
	 */
	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	public void draw() {
		// Initial window size
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(5.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.FirstUseEver);
		// "Add new entity" hotkey
//		if((ImGui.isKeyDown(Keyboard.KEY_LEFT_CONTROL) || ImGui.isKeyDown(Keyboard.KEY_RIGHT_CONTROL)) && this.selectedEntity != null) {
//			if(ImGui.isKeyPressed(Keyboard.KEY_A, false)) {
//				this.selectedEntity.addChild(new Entity());
//			}
//		}
		// Show scene tree gui window
		if(ImGui.begin("Scene tree")) {
			// Listen for cut/copy/paste/delete hotkeys
//			if(this.selectedEntity != null && this.selectedEntity.getParent() != null) {
//				this.entityClipboard.listenForKeyBinds(this.selectedEntity, this.selectedName);
//				if(ImGui.isKeyPressed(Keyboard.KEY_DELETE, false) && ImGui.isWindowFocused()) {
//					this.selectedEntity.removeFromScene();
//				}
//			}
			// Recursive function to render entities as tree nodes
			if(EditorScene.current() != null) {
				this.drawEntity("root", EditorScene.current(), null, Scene.getRoot());
			}
			this.renamer.performRename();
			if(this.payload != null) {
				this.payload.performDrop();
				this.payload = null;
			}
		}
		ImGui.end();
	}

	private void drawEntity(String name, EntityResource resource, EntityResource parent, Entity entity) {
		// Default node flags
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;
		// Show as leaf if the entity as no children
		if(resource.children.size() == 0) flags = flags | ImGuiTreeNodeFlags.Leaf;
		// Highlight when selected
		if(this.inspector.isSelected(resource)) flags = flags | ImGuiTreeNodeFlags.Selected;
		// Show as gray if the entity was cut
//		if(this.entityClipboard.isInClipboard(entity))
//			ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
		// Show entity as tree node
		if(ImGui.treeNodeEx(name, flags, this.renamer.isRenaming(resource) ? "" : name)) {
			// Show other entities as white if this one was gray
//			if(this.entityClipboard.isInClipboard(entity))
//				ImGui.popStyleColor();
			this.doRenamingInput(name, resource);
			this.doSelectEntity(resource, entity);
			this.doDragAndDrop(name, resource, parent);
			this.doClickToRename(resource, parent);
//			this.doRightClickMenu(name, entity, isRoot);
			// Show children recursively
			resource.children.forEach((key, child) -> this.drawEntity(key, child, resource, entity.requireChild(key)));
			ImGui.treePop();
		}
	}

	private void doSelectEntity(EntityResource resource, Entity entity) {
		if(ImGui.isItemClicked(0) || ImGui.isItemClicked(1)) {
			this.inspector.setEntity(resource, entity);
		}
	}

	private void doDragAndDrop(String name, EntityResource entity, EntityResource parent) {
		if(parent != null) {
			// Start dragging entity
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("Entity", new EntityPayload(entity, parent, name));
				ImGui.text(name);
				ImGui.endDragDropSource();
			}
		}
		// Detect entity drop
		if(ImGui.beginDragDropTarget()) {
			Object object = ImGui.acceptDragDropPayload("Entity");
			if(object instanceof EntityPayload dropPayload) {
				if(!entity.children.containsKey(dropPayload.name)) {
					dropPayload.setNewParent(entity);
					this.payload = dropPayload;
				}
			}
			ImGui.endDragDropTarget();
		}
	}

	private void doClickToRename(EntityResource entity, EntityResource parent) {
		if(ImGui.isItemFocused()) {
			// Stop renaming when another entity is pressed
			if((ImGui.isItemHovered() && ImGui.isMouseClicked(0)) || ImGui.isKeyPressed(Keyboard.KEY_ESCAPE, false)) {
				this.renamer.stopRenaming();
			}
			// Start renaming when the entity is double-clicked
			if((ImGui.isKeyPressed(Keyboard.KEY_F2, false) || ImGui.isKeyPressed(Keyboard.KEY_ENTER, false) || ImGui.isMouseDoubleClicked(0)) && parent != null) {
				this.renamer.setEntity(entity, parent);
			}
		}
	}

//	/**
//	 * Show the context menu when an entity is right-clicked.
//	 *
//	 * @param name Name of the entity
//	 * @param entity The entity that was clicked
//	 * @param isRoot Root cannot be cut/copied/deleted
//	 */
//	private void doRightClickMenu(String name, EntityResource entity, boolean isRoot) {
//		if(ImGui.beginPopupContextItem()) {
//			if(ImGui.menuItem("Add child entity", "Ctrl+A")) {
//				entity.addChild(new Entity());
//			}
//			ImGui.separator();
//			if(!isRoot && ImGui.menuItem("Cut", "Ctrl+X")) {
//				this.entityClipboard.cut(entity, name);
//			}
//			if(!isRoot && ImGui.menuItem("Copy", "Ctrl+C")) {
//				this.entityClipboard.copy(entity, name);
//			}
//			if(ImGui.menuItem("Paste", "Ctrl+V")) {
//				this.entityClipboard.paste(entity);
//			}
//			if(!isRoot) {
//				ImGui.separator();
//				if(ImGui.menuItem("Rename", "F2")) {
//					this.renaming = entity;
//				}
//				ImGui.separator();
//				if(ImGui.menuItem("Delete entity", "Delete")) {
//					entity.removeFromScene();
//				}
//			}
//			ImGui.endPopup();
//		}
//	}

	/**
	 * Show a text input instead of the entity if it is being renamed.
	 *
	 * @param name Name of the entity
	 * @param entity The current entity
	 */
	private void doRenamingInput(String name, EntityResource entity) {
		if(this.renamer.isRenaming(entity)) {
			ImGui.sameLine();
			ImGui.setKeyboardFocusHere();
			ImString ptr = new ImString(name, 256);
			if(ImGui.inputText("##" + entity, ptr, ImGuiInputTextFlags.EnterReturnsTrue)) {
				this.renamer.setName(ptr.get(), name);
			}
		}
	}
}
