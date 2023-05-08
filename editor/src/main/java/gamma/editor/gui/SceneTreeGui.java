package gamma.editor.gui;

import gamma.editor.EditorClassLoader;
import gamma.editor.controls.DragDropPayload;
import gamma.editor.controls.EditorScene;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.Reflection;
import imgui.ImGui;

import java.util.Map;
import java.util.stream.Stream;

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
	protected void onDrawNode(NodeResource node, String label, NodeResource parent) {
		super.onDrawNode(node, label, parent);
		if(ImGui.beginPopupContextItem()) {
			EditorClassLoader.getNodeClasses().forEach(nodeClass -> {
				if(ImGui.menuItem(nodeClass.getSimpleName())) {
					NodeResource addedNodeResource = new NodeResource(nodeClass.getName());
					EditorScene.putNode(node, addedNodeResource, (Node) Reflection.instantiate(nodeClass));
				}
			});
			ImGui.endPopup();
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
	protected Stream<NodeResource> getChildren(NodeResource resource) {
		return resource.children.values().stream();
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
	}

	@Override
	protected void onRename(NodeResource node, String name, NodeResource parent) {
		String oldName = this.getLabel(node, parent);
		parent.children.remove(oldName);
		parent.children.put(name, node);
	}
}
