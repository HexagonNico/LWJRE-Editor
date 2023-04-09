package gamma.editor.gui;

import gamma.editor.EditorUtils;
import gamma.editor.controls.EntityClipboard;
import gamma.engine.input.Keyboard;
import gamma.engine.scene.Entity;
import gamma.engine.scene.Scene;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
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

	/** Currently selected entity needed to listen for key bindings */
	private Entity selectedEntity;
	/** Name of the selected entity needed to listen for key bindings */
	private String selectedName;

	/** The entity that is being renamed */
	private Entity renaming;
	/** New name to give to the entity that is being renamed */
	private String newName;

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
		if((ImGui.isKeyDown(Keyboard.KEY_LEFT_CONTROL) || ImGui.isKeyDown(Keyboard.KEY_RIGHT_CONTROL)) && this.selectedEntity != null) {
			if(ImGui.isKeyPressed(Keyboard.KEY_A, false)) {
				this.selectedEntity.addChild(new Entity());
			}
		}
		// Show scene tree gui window
		if(ImGui.begin("Scene tree")) {
			// Listen for cut/copy/paste/delete hotkeys
			if(this.selectedEntity != null && this.selectedEntity.getParent() != null) {
				this.entityClipboard.listenForKeyBinds(this.selectedEntity, this.selectedName);
				if(ImGui.isKeyPressed(Keyboard.KEY_DELETE, false) && ImGui.isWindowFocused()) {
					this.selectedEntity.removeFromScene();
				}
			}
			// Recursive function to render entities as tree nodes
			this.drawEntity("root", Scene.getRoot(), true);
			// Rename an entity if it was renamed
			if(this.renaming != null && this.renaming.getParent() != null && this.newName != null) {
				// TODO: Get the name for selectedEntity might be easier
				if(!this.newName.isEmpty()) {
					String actualName = EditorUtils.findUnusedName(this.newName, this.renaming.getParent());
					this.renaming.getParent().renameChild(this.renaming, actualName);
				}
				this.renaming = null;
				this.newName = null;
			}
		}
		ImGui.end();
	}

	/**
	 * Recursive function to draw entities as tree nodes
	 *
	 * @param name Name of the entity
	 * @param entity The current entity
	 * @param isRoot Root entities cannot be moved
	 */
	private void drawEntity(String name, Entity entity, boolean isRoot) {
		// Default node flags
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;
		// Show as leaf if the entity as no children
		if(entity.getChildCount() == 0) flags = flags | ImGuiTreeNodeFlags.Leaf;
		// Highlight when selected
		if(entity == this.inspector.entity) flags = flags | ImGuiTreeNodeFlags.Selected;
		// Show as gray if the entity was cut
		if(this.entityClipboard.isInClipboard(entity))
			ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
		// Show entity as tree node
		if(ImGui.treeNodeEx(name, flags, this.renaming != entity ? name : "")) {
			// Show other entities as white if this one was gray
			if(this.entityClipboard.isInClipboard(entity))
				ImGui.popStyleColor();
			this.doRenamingInput(name, entity);
			this.doSelectEntity(name, entity);
			this.doDragAndDrop(name, entity);
			this.doClickToRename(entity, isRoot);
			this.doRightClickMenu(name, entity, isRoot);
			// Show children recursively
			entity.forEachChild((key, child) -> this.drawEntity(key, child, false));
			ImGui.treePop();
		}
	}

	/**
	 * Handles the selection of an entity when clicked.
	 *
	 * @param name Entity's name
	 * @param entity Current entity
	 */
	private void doSelectEntity(String name, Entity entity) {
		if(ImGui.isItemClicked(0) || ImGui.isItemClicked(1)) {
			this.inspector.entity = entity;
			this.selectedEntity = entity;
			this.selectedName = name;
		}
	}

	/**
	 * Handles drag and drop of entities.
	 *
	 * @param name Entity's name
	 * @param entity Current entity
	 */
	private void doDragAndDrop(String name, Entity entity) {
		// Start dragging entity
		if(ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload("Entity", new Object[] {entity, name});
			ImGui.text(name);
			ImGui.endDragDropSource();
		}
		// Detect entity drop
		if(ImGui.beginDragDropTarget()) {
			Object[] payload = ImGui.acceptDragDropPayload("Entity");
			if(payload != null && payload[0] instanceof Entity entityToMove && payload[1] instanceof String key) {
				// Get the parent to make sure it is not the same
				Entity parent = entity.getParent();
				while(parent != null && parent != entityToMove)
					parent = parent.getParent();
				// Move the entity
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

	/**
	 * Detect when an entity is clicked to start renaming it.
	 *
	 * @param entity The entity to start renaming
	 * @param isRoot The root cannot be renamed // TODO: Might change
	 */
	private void doClickToRename(Entity entity, boolean isRoot) {
		if(ImGui.isWindowFocused()) {
			// Stop renaming when another entity is pressed
			if(((ImGui.isItemHovered() && ImGui.isMouseClicked(0)) || ImGui.isKeyPressed(Keyboard.KEY_ESCAPE, false)) && this.renaming != null) {
				this.renaming = null;
			}
			// Start renaming when the entity is double-clicked
			if(this.inspector.entity == entity && (ImGui.isKeyPressed(Keyboard.KEY_F2, false) || ImGui.isKeyPressed(Keyboard.KEY_ENTER, false) || ImGui.isMouseDoubleClicked(0)) && !isRoot) {
				this.renaming = entity;
			}
		} else {
			this.renaming = null;
		}
	}

	/**
	 * Show the context menu when an entity is right-clicked.
	 *
	 * @param name Name of the entity
	 * @param entity The entity that was clicked
	 * @param isRoot Root cannot be cut/copied/deleted
	 */
	private void doRightClickMenu(String name, Entity entity, boolean isRoot) {
		if(ImGui.beginPopupContextItem()) {
			if(ImGui.menuItem("Add child entity", "Ctrl+A")) {
				entity.addChild(new Entity());
			}
			ImGui.separator();
			if(!isRoot && ImGui.menuItem("Cut", "Ctrl+X")) {
				this.entityClipboard.cut(entity, name);
			}
			if(!isRoot && ImGui.menuItem("Copy", "Ctrl+C")) {
				this.entityClipboard.copy(entity, name);
			}
			if(ImGui.menuItem("Paste", "Ctrl+V")) {
				this.entityClipboard.paste(entity);
			}
			ImGui.separator();
			if(!isRoot && ImGui.menuItem("Delete entity", "Delete")) {
				entity.removeFromScene();
			}
			ImGui.endPopup();
		}
	}

	/**
	 * Show a text input instead of the entity if it is being renamed.
	 *
	 * @param name Name of the entity
	 * @param entity The current entity
	 */
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
