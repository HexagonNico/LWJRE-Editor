package io.github.lwjre.editor.gui;

import imgui.ImGui;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.Clipboard;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.controllers.ProjectClassesLoader;
import io.github.lwjre.editor.models.EditorNode;
import io.github.lwjre.engine.resources.NodeResource;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Class that represents the scene tree gui.
 *
 * @author Nico
 */
public class SceneTreeGui extends TreeWindowGui<EditorNode> {

	/** Responsible for loading classes when the project is updated */
	private final ProjectClassesLoader classesLoader = new ProjectClassesLoader();
	/** Needs to be updated when a node is selected */
	private final InspectorGui inspector;

	/** Popup to show when a node is being renamed */
	private final TextInputPopup renameNodePopup = new TextInputPopup("Rename node", "Renaming node...");
	/** Popup to show when a node is being deleted */
	private final ConfirmPopupGui confirmDeletePopup = new ConfirmPopupGui("Delete node");

	/**
	 * Constructs the scene tree gui.
	 *
	 * @param inspector Reference to the fields gui
	 */
	public SceneTreeGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	public void init() {
		this.classesLoader.init();
	}

	@Override
	protected String title() {
		return "Scene tree";
	}

	@Override
	public void draw() {
		this.renameNodePopup.draw();
		this.confirmDeletePopup.draw();
		super.draw();
		if(this.classesLoader.listenForChanges()) {
			this.inspector.setNode(null, null);
		}
	}

	@Override
	protected EditorNode getRoot() {
		if(EditorScene.rootNode() != null && EditorScene.rootResource() != null) {
			return new EditorNode(EditorScene.rootNode(), EditorScene.rootResource(), EditorScene.rootName(), null);
		}
		return null;
	}

	@Override
	protected String getLabel(EditorNode node) {
		return node.label();
	}

	@Override
	protected boolean isLeaf(EditorNode node) {
		return node.resource().children.isEmpty();
	}

	@Override
	protected void drawChildren(EditorNode node) {
		node.resource().children.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.filter(entry -> node.node().getChild(entry.getKey()) != null)
				.forEach(entry -> this.drawNode(new EditorNode(node.node().getChild(entry.getKey()), entry.getValue(), entry.getKey(), node.resource())));
	}

	@Override
	protected void onSelect(EditorNode node) {
		this.inspector.setNode(node.node(), node.resource());
	}

	@Override
	protected void onDoubleClick(EditorNode node) {

	}

	@Override
	protected ContextMenu contextMenu(EditorNode node) {
		ContextMenu contextMenu = super.contextMenu(node);
		ContextMenu addNodeSubmenu = new ContextMenu();
		ProjectClassesLoader.getNodeClasses().forEach(nodeClass -> addNodeSubmenu.menuItem(nodeClass.getSimpleName(), () -> node.addChild(nodeClass)));
		contextMenu.submenu("Add node", addNodeSubmenu);
		ContextMenu addChildSceneSubmenu = new ContextMenu();
		try(Stream<Path> files = Files.walk(ProjectPath.resourcesFolder())) {
			files.forEach(path -> {
				String fileName = path.getFileName().toString();
				if(fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
					Path resourcePath = ProjectPath.resourcesFolder().relativize(path);
					addChildSceneSubmenu.menuItem(resourcePath.toString(), () -> node.addChild(resourcePath));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		contextMenu.submenu("Add child scene", addChildSceneSubmenu);
		contextMenu.separator();
		boolean isRoot = node.equals(this.getRoot());
		if(!isRoot) {
			contextMenu.menuItem("Cut", "Ctrl + X", () -> cutNode(node));
			contextMenu.menuItem("Copy", "Ctrl + C", () -> copyNode(node));
		}
		contextMenu.menuItem("Paste", "Ctrl + V", () -> pasteNode(node));
		if(!isRoot) {
			contextMenu.separator();
			contextMenu.menuItem("Rename", "Shift + F6", () -> this.rename(node));
			contextMenu.menuItem("Delete node", "Del", () -> this.delete(node));
		}
		return contextMenu;
	}

	@Override
	protected void hotkeys(EditorNode node) {
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
				cutNode(node);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
				copyNode(node);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
				pasteNode(node);
			}
		} else if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_F6)) {
				this.rename(node);
			}
		} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
			this.delete(node);
		}
	}

	@Override
	protected String dragType() {
		return "EditorNode";
	}

	@Override
	protected String[] acceptablePayloads() {
		return new String[] {"EditorNode", "Path"};
	}

	@Override
	protected void onDrop(EditorNode target, Object payload) {
		if(payload instanceof EditorNode editorNode) {
			editorNode.reparent(target);
		} else if(payload instanceof Path filePath) {
			target.addChild(ProjectPath.resourcesFolder().relativize(filePath));
		}
	}

	/**
	 * Copies the given node to the clipboard.
	 *
	 * @param node The node to copy
	 */
	private static void copyNode(EditorNode node) {
		EditorNode copy = new EditorNode(node.resource().instantiate(), new NodeResource(node.resource()), node.label(), null);
		Clipboard.setContent(copy, target -> {
			if(target instanceof EditorNode targetNode) {
				copy.reparent(targetNode);
				copyNode(node);
			}
		});
	}

	/**
	 * Cuts the given node to the clipboard.
	 *
	 * @param node The node to cut
	 */
	private static void cutNode(EditorNode node) {
		Clipboard.setContent(node, target -> {
			if(target instanceof EditorNode targetNode) {
				node.reparent(targetNode);
				copyNode(node);
			}
		});
	}

	/**
	 * Pastes the node in the clipboard as a child the given target.
	 *
	 * @param target The target node
	 */
	private static void pasteNode(EditorNode target) {
		if(Clipboard.getContent() instanceof EditorNode) {
			Clipboard.paste(target);
		}
	}

	/**
	 * Starts showing the popup to rename a node.
	 *
	 * @param node The node to rename
	 */
	private void rename(EditorNode node) {
		this.renameNodePopup.setContent("Rename node '" + node.label() + "' to:");
		this.renameNodePopup.open(node.label(), node::rename);
	}

	/**
	 * Starts showing the popup to delete a node.
	 *
	 * @param node The node to delete
	 */
	private void delete(EditorNode node) {
		this.confirmDeletePopup.setContent("Delete node '" + node.label() + "'?");
		this.confirmDeletePopup.open(node::delete);
	}

	@Override
	public void cleanUp() {
		try {
			this.classesLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
