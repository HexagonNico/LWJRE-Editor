package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import io.github.lwjre.editor.controllers.Clipboard;

/**
 * Class that represents a gui window that can show a tree.
 *
 * @param <N> Type of node of this tree
 *
 * @author Nico
 */
public abstract class TreeWindow<N> implements GuiComponent {

	/** Currently selected node */
	private N selected = null;

	/**
	 * Draws the tree.
	 */
	protected final void drawTree() {
		N root = this.getRoot();
		if(root != null) {
			this.drawNode(root);
		}
	}

	/**
	 * Gets the tree's root.
	 * Might be null.
	 *
	 * @return The tree's root or null if no tree is shown
	 */
	protected abstract N getRoot();

	/**
	 * Gets the label of the given node.
	 *
	 * @param node The node
	 * @return The given node's label
	 */
	protected abstract String getLabel(N node);

	/**
	 * Returns true if the given node is a leaf, otherwise false.
	 *
	 * @param node The node
	 * @return True if the given node is a leaf, otherwise false
	 */
	protected abstract boolean isLeaf(N node);

	/**
	 * Draws the given node.
	 * This function calls {@link TreeWindow#drawChildren(Object)} to draw the node's children.
	 *
	 * @param node The node to draw
	 */
	protected final void drawNode(N node) {
		if(node.equals(Clipboard.getContent())) {
			ImGui.pushStyleColor(ImGuiCol.Text, 0.7f, 0.7f, 0.7f, 1.0f);
		}
		String label = this.getLabel(node);
		if(ImGui.treeNodeEx(label, this.nodeFlags(node))) {
			if(node.equals(Clipboard.getContent())) {
				ImGui.popStyleColor();
			}
			this.dragDrop(node, label);
			if(ImGui.isItemClicked() || this.contextMenu(node).draw()) {
				this.selected = node;
				this.onSelect(node);
			}
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
				this.onDoubleClick(node);
			}
			if(node.equals(this.selected) && ImGui.isWindowFocused()) {
				this.hotkeys(node);
			}
			if(!this.isLeaf(node)) {
				this.drawChildren(node);
			}
			ImGui.treePop();
		}
	}

	/**
	 * Gets flags for the given node.
	 *
	 * @see ImGuiTreeNodeFlags
	 *
	 * @param node The node
	 * @return The given node's flags
	 */
	protected int nodeFlags(N node) {
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.OpenOnDoubleClick;
		if(this.isLeaf(node)) {
			flags = flags | ImGuiTreeNodeFlags.Leaf;
		}
		if(node.equals(this.selected)) {
			flags = flags | ImGuiTreeNodeFlags.Selected;
		}
		return flags;
	}

	/**
	 * Gets the context menu for the given node.
	 *
	 * @param node The node
	 * @return The context menu for the given node
	 */
	protected ContextMenu contextMenu(N node) {
		return new ContextMenu();
	}

	/**
	 * Draws the given node's children.
	 * This function should call {@link TreeWindow#drawNode(Object)} for every child of the given node.
	 *
	 * @param node The parent node
	 */
	protected abstract void drawChildren(N node);

	/**
	 * Called when the given node is selected.
	 *
	 * @param node The node that was selected
	 */
	protected abstract void onSelect(N node);

	/**
	 * Called when the given node is double-clicked.
	 *
	 * @param node The node that was double-clicked
	 */
	protected abstract void onDoubleClick(N node);

	/**
	 * Listens for hotkeys for the given node.
	 *
	 * @param node The current node
	 */
	protected abstract void hotkeys(N node);

	/**
	 * Handles drag and drop.
	 *
	 * @param node The current node
	 * @param label The text to show when the node is being dragged
	 */
	private void dragDrop(N node, String label) {
		String type = this.dragType();
		if(type != null && !type.isEmpty()) {
			if(ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload(type, node);
				ImGui.text(label);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()) {
			for(String payloadType : this.acceptablePayloads()) {
				Object payload = ImGui.acceptDragDropPayload(payloadType);
				if(payload != null) {
					this.onDrop(node, payload);
				}
			}
			ImGui.endDragDropTarget();
		}
	}

	/**
	 * Gets the type to use when dragging nodes or an empty string if dragging should be disabled.
	 *
	 * @return The type to use when dragging nodes or an empty string if dragging should be disabled
	 */
	protected String dragType() {
		return "";
	}

	/**
	 * Gets the types that can be accepted when a payload is dropped onto a node of this tree.
	 *
	 * @return An array of the types that can be accepted when a payload is dropped onto a node of this tree
	 */
	protected String[] acceptablePayloads() {
		return new String[0];
	}

	/**
	 * Called when a payload is dropped onto a node.
	 *
	 * @param target The target node
	 * @param payload The drop payload
	 */
	protected abstract void onDrop(N target, Object payload);
}
