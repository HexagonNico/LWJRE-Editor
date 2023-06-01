package gamma.editor.gui;

import gamma.editor.DynamicLoader;
import gamma.editor.ProjectPath;
import gamma.editor.controls.Clipboard;
import gamma.editor.controls.DragDropPayload;
import gamma.editor.controls.EditorScene;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class SceneTreeGui extends TreeGui<NodeResource> {

	@Override
	protected String title() {
		return "Scene tree";
	}

	@Override
	protected void onDrawNode(NodeResource node, String label, NodeResource parent) {
		super.onDrawNode(node, label, parent);
		if(ImGui.beginPopupContextItem()) {
			this.onSelect(node);
			if(ImGui.beginMenu("Add node")) {
				DynamicLoader.getNodeClasses().forEach(nodeClass -> {
					if(ImGui.menuItem(nodeClass.getSimpleName())) {
						NodeResource addedNodeResource = new NodeResource(nodeClass.getName());
						EditorScene.putNode(node, addedNodeResource, addedNodeResource.instantiate());
					}
				});
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Add child scene")) {
				try(Stream<Path> files = Files.walk(ProjectPath.resourcesFolder())) {
					files.forEach(path -> {
						String fileName = path.getFileName().toString();
						String menuItem = ProjectPath.resourcesFolder().relativize(path).toString();
						if((fileName.endsWith(".yaml") || fileName.endsWith(".yml")) && ImGui.menuItem(menuItem)) {
							instantiateChildScene(path, node);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Change type")) {
				DynamicLoader.getNodeClasses().forEach(nodeClass -> {
					if(ImGui.menuItem(nodeClass.getSimpleName())) {
						node.type = nodeClass.getName();
						EditorScene.putNode(parent, node, label, EditorScene.getNode(node));
					}
				});
				ImGui.endMenu();
			}
			ImGui.separator();
			if(!node.equals(this.getRoot()) && ImGui.menuItem("Cut", "Ctrl+X")) {
				cutNode(node, parent, label);
			}
			if(!node.equals(this.getRoot()) && ImGui.menuItem("Copy", "Ctrl+C")) {
				copyNode(node, label);
			}
			if(ImGui.menuItem("Paste", "Ctrl+V")) {
				pasteNode(node);
			}
			ImGui.separator();
			if(!node.equals(this.getRoot()) && ImGui.menuItem("Delete node", "Del")) {
				this.deleteNode(node, parent);
			}
			ImGui.endPopup();
		}
		if(ImGui.isWindowFocused() && this.isSelected(node)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
				this.deleteNode(node, parent);
			}
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
				if(!node.equals(this.getRoot()) && ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
					cutNode(node, parent, label);
				} else if(!node.equals(this.getRoot()) && ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
					copyNode(node, label);
				} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
					pasteNode(node);
				}
			}
		}
	}

	private static void cutNode(NodeResource node, NodeResource parent, String label) {
		Clipboard.setContent(node, target -> {
			Node actualNode = EditorScene.removeNode(node);
			parent.children.values().remove(node);
			EditorScene.putNode((NodeResource) target, node, label, actualNode);
			copyNode(node, label);
		});
	}

	private static void copyNode(NodeResource node, String label) {
		NodeResource copy = new NodeResource(node);
		Clipboard.setContent(copy, target -> {
			EditorScene.putNode((NodeResource) target, copy, label, copy.instantiate());
			copyNode(node, label);
		});
	}

	private static void pasteNode(NodeResource target) {
		if(Clipboard.getContent() instanceof NodeResource) {
			Clipboard.paste(target);
		}
	}

	private void deleteNode(NodeResource node, NodeResource parent) {
		if(!node.equals(this.getRoot())) {
			EditorScene.removeNode(node);
			parent.children.values().remove(node);
			GuiManager.get(InspectorGui.class).setNode(null);
		}
	}

	private static void instantiateChildScene(Path filePath, NodeResource target) {
		if(!filePath.equals(EditorScene.currentPath())) {
			String path = ProjectPath.resourcesFolder().relativize(filePath).toString();
			if(path.endsWith(".yaml") || path.endsWith(".yml")) {
				NodeResource resource = new NodeResource();
				resource.override = path;
				EditorScene.putNode(target, resource, path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.')), resource.instantiate());
			}
		}
	}

	@Override
	protected NodeResource getRoot() {
		return EditorScene.rootResource();
	}

	@Override
	protected String getLabel(NodeResource node, NodeResource parent) {
		if(parent == null) {
			String fileName = EditorScene.currentFileName();
			return fileName.substring(0, fileName.lastIndexOf('.'));
		} else {
			return parent.children.entrySet().stream()
					.filter(entry -> entry.getValue().equals(node))
					.findFirst()
					.map(Map.Entry::getKey)
					.orElse("Node");
		}
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
		} else if(payload.object() instanceof Path filePath) {
			instantiateChildScene(filePath, target);
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
		GuiManager.get(InspectorGui.class).setNode(node);
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
