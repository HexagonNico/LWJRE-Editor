package gamma.editor.gui;

import gamma.editor.EditorClassLoader;
import gamma.editor.controls.DragDropPayload;
import gamma.editor.controls.EditorScene;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.Reflection;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class SceneTreeGui extends TreeGui<NodeResource> {

	private final InspectorGui inspector;

	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	protected String title() {
		return "Scene tree";
	}

	@Override
	protected void onDraw() {
		super.onDraw();
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {

		}
	}

	@Override
	protected void onDrawNode(NodeResource node, String label, NodeResource parent) {
		super.onDrawNode(node, label, parent);
		if(ImGui.beginPopupContextItem()) {
			this.onSelect(node);
			if(ImGui.beginMenu("Add node")) {
				EditorClassLoader.getNodeClasses().forEach(nodeClass -> {
					if(ImGui.menuItem(nodeClass.getSimpleName())) {
						NodeResource addedNodeResource = new NodeResource(nodeClass.getName());
						EditorScene.putNode(node, addedNodeResource, (Node) Reflection.instantiate(nodeClass));
					}
				});
				ImGui.endMenu();
			}
			ImGui.separator();
			if(ImGui.menuItem("Cut", "Ctrl+X")) {

			}
			if(ImGui.menuItem("Copy", "Ctrl+C")) {

			}
			if(ImGui.menuItem("Paste", "Ctrl+V")) {

			}
			ImGui.separator();
			if(ImGui.menuItem("Delete node", "Del")) {
				this.deleteNode(node, parent);
			}
			ImGui.endPopup();
		}
		if(ImGui.isWindowFocused()) {
			if(node.equals(this.getSelected())) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
					this.deleteNode(node, parent);
				}
			}
		}
	}

	private void deleteNode(NodeResource node, NodeResource parent) {
		if(!node.equals(this.getRoot())) {
			EditorScene.removeNode(node);
			parent.children.values().remove(node);
			this.inspector.nodeResource = null;
		}
	}

	@Override
	protected NodeResource getRoot() {
		return EditorScene.rootResource();
	}

	@Override
	protected String getLabel(NodeResource node, NodeResource parent) {
		return parent.children.entrySet().stream()
				.filter(entry -> entry.getValue().equals(node))
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse("Node");
	}

	@Override
	protected boolean isLeaf(NodeResource resource) {
		return resource.children.size() == 0;
	}

	@Override
	protected Iterable<NodeResource> getChildren(NodeResource resource) {
		return resource.children.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getValue)
				.toList();
	}

	@Override
	protected String dragDropType() {
		return "EntityResource";
	}

	@Override
	protected void onDragDropTarget(NodeResource target, DragDropPayload payload) {
		if(payload.object() instanceof NodeResource resource) {
			if(!isDescendant(resource, target)) {
				removeFromParent(this.getRoot(), resource);
				Node node = EditorScene.removeNode(resource);
				EditorScene.putNode(target, resource, payload.label(), node);
			}
		}
	}

	private static void removeFromParent(NodeResource possibleParent, NodeResource child) {
		if(!possibleParent.children.values().remove(child)) {
			possibleParent.children.forEach((key, nextParent) -> removeFromParent(nextParent, child));
		}
	}

	public static boolean isDescendant(NodeResource ancestor, NodeResource descendant) {
		if(ancestor.children.containsValue(descendant)) {
			return true;
		} else {
			return ancestor.children.values().stream().anyMatch(next -> isDescendant(next, descendant));
		}
	}

	@Override
	protected void onSelect(NodeResource node) {
		this.inspector.nodeResource = node;
		super.onSelect(node);
	}

	@Override
	protected void onRename(NodeResource resource, String name, NodeResource parent) {
		Node node = EditorScene.removeNode(resource);
		parent.children.values().remove(resource);
		name = EditorScene.putNode(parent, resource, name, node);
		parent.children.put(name, resource);
	}
}
