package gamma.editor.gui;

import gamma.editor.controls.Clipboard;
import gamma.editor.controls.DragDropPayload;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

public abstract class TreeGui<N> extends EditorGui {

	private N selected;
	private boolean renaming = false;

	@Override
	protected void onDraw() {
		N root = this.getRoot();
		if(root != null) {
			this.drawNode(root, null);
		}
	}

	private void drawNode(N node, N parent) {
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap;
		if(this.isLeaf(node))
			flags = flags | ImGuiTreeNodeFlags.Leaf;
		if(node.equals(this.selected)) {
			flags = flags | ImGuiTreeNodeFlags.Selected;
		}
		String label = parent != null ? this.getLabel(node, parent) : "root";
		if(node.equals(Clipboard.getContent())) {
			ImGui.pushStyleColor(ImGuiCol.Text, 0.7f, 0.7f, 0.7f, 1.0f);
		}
		if(ImGui.treeNodeEx(this.renaming && this.isSelected(node) ? "##" + label : label, flags)) {
			if(node.equals(Clipboard.getContent())) {
				ImGui.popStyleColor();
			}
			this.onDrawNode(node, label, parent);
			if(!this.isLeaf(node)) {
				this.getChildren(node).forEach(child -> this.drawNode(child, node));
			}
			ImGui.treePop();
		}
	}

	protected void onDrawNode(N node, String label, N parent) {
		this.dragDrop(label, node);
		if(parent != null) {
			this.inputRenameNode(label, node, parent);
		}
		if(ImGui.isItemClicked()) {
			this.onSelect(node);
		}
		if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
			this.onDoubleClick(node);
		}
	}

	private void dragDrop(String label, N node) {
		if(!this.dragDropType().isEmpty()) {
			if(ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload(this.dragDropType(), new DragDropPayload(node, label));
				ImGui.text(label);
				ImGui.endDragDropSource();
			}
			if(ImGui.beginDragDropTarget()) {
				Object payload = ImGui.acceptDragDropPayload(this.dragDropType());
				if(payload instanceof DragDropPayload) {
					this.onDragDropTarget(node, (DragDropPayload) payload);
				}
				ImGui.endDragDropTarget();
			}
		}
	}

	private void inputRenameNode(String label, N node, N parent) {
		// TODO: Stop renaming when another node is selected
		if(!this.renaming && this.isSelected(node) && ImGui.isWindowFocused() && ImGui.isKeyPressed(257)) {
			this.renaming = true;
		}
		if(this.renaming && this.isSelected(node)) {
			ImGui.sameLine();
			ImGui.setKeyboardFocusHere();
			ImString ptr = new ImString(label, 256);
			if(ImGui.inputText("##renaming", ptr, ImGuiInputTextFlags.EnterReturnsTrue)) {
				this.onRename(node, ptr.get(), parent);
				this.renaming = false;
			}
		}
	}

	protected abstract N getRoot();

	protected abstract String getLabel(N node, N parent);

	protected abstract boolean isLeaf(N node);

	protected abstract Iterable<N> getChildren(N node);

	protected String dragDropType() {
		return "";
	}

	protected void onDragDropTarget(N target, DragDropPayload payload) {

	}

	protected void onSelect(N node) {
		this.selected = node;
	}

	protected void onDoubleClick(N node) {

	}

	protected void onRename(N node, String name, N parent) {

	}

	public final N getSelected() {
		return this.selected;
	}

	public final boolean isSelected(N node) {
		return node.equals(this.selected);
	}
}
