package gamma.editor.gui;

import gamma.editor.DynamicLoader;
import gamma.editor.ProjectPath;
import gamma.editor.controls.Clipboard;
import gamma.editor.controls.EditorScene;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.Reflection;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class SceneTreeGui extends TreeWindowGui<NodeResource> {

	private Runnable reparentAction = null;
	private Runnable renameAction = null;

	@Override
	protected String title() {
		return "Scene tree";
	}

	@Override
	protected void drawWindow() {
		super.drawWindow();
		if(this.reparentAction != null) {
			this.reparentAction.run();
			this.reparentAction = null;
		}
		if(this.renameAction != null) {
			this.renameAction.run();
			this.renameAction = null;
		}
	}

	@Override
	protected NodeResource getRoot() {
		return EditorScene.rootResource();
	}

	@Override
	protected String getRootLabel() {
		return EditorScene.currentFileName().replaceAll("(.yaml|.yml)", "");
	}

	@Override
	protected void drawNode(NodeResource node, String label, NodeResource parent) {
		super.drawNode(node, label, parent);
		if(this.isSelected(node) && ImGui.isWindowFocused()) {
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
				if(parent != null && ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
					cutNode(node, parent, label);
				} else if(parent != null && ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
					copyNode(node, label);
				} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
					pasteNode(node);
				}
			}
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
				deleteNode(node, parent);
			}
		}
	}

	@Override
	protected void drawChildren(NodeResource node) {
		node.children.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEach(entry -> this.drawNode(entry.getValue(), entry.getKey(), node));
	}

	@Override
	protected boolean isLeaf(NodeResource node) {
		return node.children.size() == 0;
	}

	@Override
	protected ContextMenu contextMenu(NodeResource node, String label, NodeResource parent) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.submenu("Add node", () -> {
			ContextMenu submenu = new ContextMenu();
			DynamicLoader.getNodeClasses().forEach(nodeClass -> {
				String className = nodeClass.getSimpleName();
				submenu.menuItem(className, () -> {
					NodeResource nodeResource = new NodeResource(nodeClass.getName());
					node.children.put(findUnusedKey(className, node), nodeResource);
					EditorScene.reload();
				});
			});
			return submenu;
		});
		contextMenu.submenu("Add child scene", () -> {
			ContextMenu submenu = new ContextMenu();
			try(Stream<Path> files = Files.walk(ProjectPath.resourcesFolder())) {
				files.forEach(path -> {
					String fileName = path.getFileName().toString();
					if(fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
						submenu.menuItem(ProjectPath.resourcesFolder().relativize(path).toString(), () -> instantiateChildScene(path, node));
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			return submenu;
		});
		contextMenu.submenu("Change type", () -> {
			ContextMenu submenu = new ContextMenu();
			DynamicLoader.getNodeClasses().forEach(nodeClass -> {
				submenu.menuItem(nodeClass.getSimpleName(), () -> {
					node.type = nodeClass.getName();
					node.properties.entrySet().removeIf(entry -> !Reflection.hasField(entry.getKey(), nodeClass));
					EditorScene.reload();
				});
			});
			return submenu;
		});
		contextMenu.separator();
		if(parent != null) {
			contextMenu.menuItem("Cut", "Ctrl+X", () -> cutNode(node, parent, label));
			contextMenu.menuItem("Copy", "Ctrl+C", () -> copyNode(node, label));
		}
		contextMenu.menuItem("Paste", "Ctrl+V", () -> pasteNode(node));
		if(parent != null) {
			contextMenu.separator();
			contextMenu.menuItem("Delete node", "Del", () -> deleteNode(node, parent));
		}
		return contextMenu;
	}

	@Override
	protected void onSelect(NodeResource node, String label) {
		GuiManager.get(InspectorGui.class).setNode(node);
		super.onSelect(node, label);
	}

	@Override
	protected String[] acceptablePayloads() {
		return new String[] {"NodeResource", "SceneFile"};
	}

	@Override
	protected void onDrag(NodeResource node, String label, NodeResource parent) {
		ImGui.setDragDropPayload("NodeResource", new Payload(node, label, parent));
	}

	private record Payload(NodeResource node, String label, NodeResource oldParent) {}

	@Override
	protected void onDrop(NodeResource target, Object payload) {
		if(payload instanceof Payload nodePayload) {
			if(!isDescendant(nodePayload.node(), target)) {
				this.reparentAction = () -> {
					nodePayload.oldParent().children.remove(nodePayload.label());
					target.children.put(nodePayload.label(), nodePayload.node());
					EditorScene.reload();
				};
			}
		} else if(payload instanceof Path filePath) {
			instantiateChildScene(filePath, target);
		}
	}

	private static boolean isDescendant(NodeResource ancestor, NodeResource descendant) {
		if(ancestor.children.containsValue(descendant)) {
			return true;
		} else {
			return ancestor.children.values().stream().anyMatch(next -> isDescendant(next, descendant));
		}
	}

	private static void instantiateChildScene(Path filePath, NodeResource target) {
		if(!filePath.equals(EditorScene.currentPath())) {
			String path = ProjectPath.resourcesFolder().relativize(filePath).toString();
			if(path.endsWith(".yaml") || path.endsWith(".yml")) {
				NodeResource resource = new NodeResource();
				resource.override = path;
				target.children.put(findUnusedKey(filePath.getFileName().toString().replaceAll("(.yaml|.yml)", ""), target), resource);
				EditorScene.reload();
			}
		}
	}

	@Override
	protected void onRename(NodeResource node, String oldName, String newName, NodeResource parent) {
		this.renameAction = () -> {
			parent.children.remove(oldName);
			parent.children.put(findUnusedKey(newName, parent), node);
			EditorScene.reload();
		};
	}

	private static void cutNode(NodeResource node, NodeResource parent, String label) {
		Clipboard.setContent(node, target -> {
			if(target instanceof NodeResource targetNode) {
				parent.children.remove(label);
				targetNode.children.put(findUnusedKey(label, targetNode), node);
				copyNode(node, label);
				EditorScene.reload();
			}
		});
	}

	private static void copyNode(NodeResource node, String label) {
		NodeResource copy = new NodeResource(node);
		Clipboard.setContent(copy, target -> {
			if(target instanceof NodeResource targetNode) {
				targetNode.children.put(findUnusedKey(label, targetNode), copy);
				copyNode(node, label);
				EditorScene.reload();
			}
		});
	}

	private static void pasteNode(NodeResource target) {
		if(Clipboard.getContent() instanceof NodeResource) {
			Clipboard.paste(target);
		}
	}

	private void deleteNode(NodeResource node, NodeResource parent) {
		if(!node.equals(this.getRoot())) {
			parent.children.values().remove(node);
			GuiManager.get(InspectorGui.class).setNode(null);
			EditorScene.reload();
		}
	}

	private static String findUnusedKey(String key, NodeResource parent) {
		int index = 2;
		while(parent.children.containsKey(key)) {
			// TODO: _ -> 2 -> 33 (?) -> 44 (???)
			key = key.replaceAll("\\d*$", String.valueOf(index));
			index++;
		}
		return key;
	}
}
