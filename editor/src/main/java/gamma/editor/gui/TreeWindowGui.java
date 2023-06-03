package gamma.editor.gui;

import gamma.editor.controls.Clipboard;
import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

public abstract class TreeWindowGui<N> extends WindowGui {

	private N selected = null;
	private N renaming = null;

	@Override
	protected void drawWindow() {
		N root = this.getRoot();
		if(root != null) {
			this.drawNode(root, this.getRootLabel(), null);
		}
	}

	protected abstract N getRoot();

	protected abstract String getRootLabel();

	protected void drawNode(N node, String label, N parent) {
		if(node != null && label != null) {
			if(node.equals(Clipboard.getContent())) {
				ImGui.pushStyleColor(ImGuiCol.Text, 0.7f, 0.7f, 0.7f, 1.0f);
			}
			if(ImGui.treeNodeEx(node.equals(this.renaming) ? "" : label, this.nodeFlags(node))) {
				if(node.equals(Clipboard.getContent())) {
					ImGui.popStyleColor();
				}
				this.dragDrop(node, label, parent);
				if(ImGui.isItemClicked()) {
					this.onSelect(node, label);
					this.renaming = null;
				}
				if(parent != null) {
					if(node.equals(this.selected)) {
						if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
							if(ImGui.isKeyPressed(GLFW.GLFW_KEY_F6)) {
								this.renaming = node;
							}
						}
					}
					if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
						this.renaming = node;
					}
					if(node.equals(this.renaming)) {
						this.renamingInput(node, label, parent);
					}
				}
				this.contextMenu(node, label, parent).draw(() -> this.onSelect(node, label));
				if(!this.isLeaf(node)) {
					this.drawChildren(node);
				}
				ImGui.treePop();
			}
		}
	}

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

	protected abstract boolean isLeaf(N node);

	protected ContextMenu contextMenu(N node, String label, N parent) {
		return new ContextMenu();
	}

	protected abstract void drawChildren(N node);

	protected abstract Class<?>[] acceptablePayloads();

	protected Object onDrag(N node, String label, N parent) {
		return null;
	}

	private void dragDrop(N node, String label, N parent) {
		if(parent != null) {
			Object dragPayload = this.onDrag(node, label, parent);
			if(dragPayload != null && ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload(dragPayload);
				ImGui.text(label);
				ImGui.endDragDropSource();
			}
		}
		if(ImGui.beginDragDropTarget()) {
			for(Class<?> type : this.acceptablePayloads()) {
				Object dropPayload = ImGui.acceptDragDropPayload(type);
				if(dropPayload != null) {
					this.onDrop(node, dropPayload);
				}
			}
			ImGui.endDragDropTarget();
		}
	}

	protected void onSelect(N node, String label) {
		this.selected = node;
	}

	protected void onDrop(N target, Object payload) {}

	private void renamingInput(N node, String label, N parent) {
		ImGui.sameLine();
		ImGui.setKeyboardFocusHere();
		ImString ptr = new ImString(label, 256);
		if(ImGui.inputText("##renaming", ptr, ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.AutoSelectAll)) {
			this.onRename(node, label, ptr.get(), parent);
			this.renaming = null;
		}
	}

	protected void onRename(N node, String oldName, String newName, N parent) {}

	protected final boolean isSelected(N node) {
		return node != null && node.equals(this.selected);
	}
}
