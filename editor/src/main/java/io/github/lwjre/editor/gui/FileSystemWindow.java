package io.github.lwjre.editor.gui;

import imgui.ImGui;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.Clipboard;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.editor.utils.EditorFileUtils;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.YamlParser;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Represents the file system gui.
 *
 * @author Nico
 */
public class FileSystemWindow extends TreeWindow<Path> {

	/** Used for creating new folders and renaming files */
	private final TextInputPopup textInputPopup;
	/** Used to delete files */
	private final AskConfirmationPopup askConfirmationPopup;

	/**
	 * Constructs the file system window.
	 *
	 * @param textInputPopup Used for creating new folders and renaming files
	 * @param askConfirmationPopup Used to delete files
	 */
	public FileSystemWindow(TextInputPopup textInputPopup, AskConfirmationPopup askConfirmationPopup) {
		this.textInputPopup = textInputPopup;
		this.askConfirmationPopup = askConfirmationPopup;
	}

	@Override
	public void draw() {
		if(ImGui.begin("File system")) {
			this.drawTree();
		}
		ImGui.end();
	}

	@Override
	protected Path getRoot() {
		return ProjectPath.resourcesFolder();
	}

	@Override
	protected String getLabel(Path node) {
		if(node.equals(this.getRoot())) {
			return "src/main/resources";
		}
		return node.getFileName().toString();
	}

	@Override
	protected boolean isLeaf(Path node) {
		return !Files.isDirectory(node);
	}

	@Override
	protected void drawChildren(Path node) {
		try(Stream<Path> files = Files.list(node)) {
			files.sorted((path1, path2) -> {
				if(Files.isDirectory(path1) == Files.isDirectory(path2))
					return path1.getFileName().toString().compareToIgnoreCase(path2.getFileName().toString());
				return Files.isDirectory(path1) ? -1 : 1;
			}).forEach(this::drawNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSelect(Path node) {

	}

	@Override
	protected void onDoubleClick(Path node) {
		String path = this.getRoot().relativize(node).toString();
		if(path.endsWith(".yaml") || path.endsWith(".yml")) {
			// TODO: Remove selected node from the fields
			Object resource = YamlParser.parseResource(path);
			if(resource instanceof NodeResource nodeResource) {
				EditorScene.changeScene(nodeResource, path);
			}
		}
	}

	@Override
	protected ContextMenu contextMenu(Path node) {
		ContextMenu contextMenu = super.contextMenu(node);
		contextMenu.menuItem("New folder", "Ctrl + N", () -> this.newFolder(node));
		contextMenu.separator();
		boolean isRoot = node.equals(this.getRoot());
		if(!isRoot) {
			contextMenu.menuItem("Cut", "Ctrl + X", () -> cutPath(node));
			contextMenu.menuItem("Copy", "Ctrl + C", () -> copyPath(node));
		}
		contextMenu.menuItem("Paste", "Ctrl + V", () -> pastePath(node));
		if(!isRoot) {
			contextMenu.separator();
			contextMenu.menuItem("Rename", "Shift + F6", () -> this.rename(node));
			contextMenu.menuItem("Delete", "Del", () -> this.delete(node));
		}
		return contextMenu;
	}

	@Override
	protected void hotkeys(Path node) {
		if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_N)) {
				this.newFolder(node);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
				cutPath(node);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
				copyPath(node);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
				pastePath(node);
			}
		} else if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_F6)) {
				this.rename(node);
			}
		} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
			this.delete(node);
		}
	}

	/**
	 * Starts showing the popup for creating a new folder.
	 *
	 * @param path The path at which the folder should be created
	 */
	private void newFolder(Path path) {
		this.textInputPopup.setTitle("New folder");
		this.textInputPopup.setContent("Create new folder");
		this.textInputPopup.setInput("folder");
		this.textInputPopup.open(name -> EditorFileUtils.createDirectory(path, name));
	}

	/**
	 * Copies a {@link Path} to the {@link Clipboard}.
	 *
	 * @param path The path to copy
	 */
	private static void copyPath(Path path) {
		// TODO: Contents of directories are not copied
		Clipboard.setContent(path.toAbsolutePath(), target -> {
			try {
				if(!Files.isDirectory((Path) target))
					target = ((Path) target).getParent();
				Files.copy(path, Path.of(target.toString(), path.getFileName().toString()));
				copyPath(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Cuts a {@link Path} to the {@link Clipboard}.
	 *
	 * @param path The path to cut
	 */
	private static void cutPath(Path path) {
		Clipboard.setContent(path, target -> {
			try {
				if(!Files.isDirectory((Path) target))
					target = ((Path) target).getParent();
				Path result = Files.move(path, Path.of(target.toString(), path.getFileName().toString()));
				copyPath(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Pastes the content of the {@link Clipboard} if it is a {@link Path} object.
	 *
	 * @param target The target on which the path was pasted
	 */
	private static void pastePath(Path target) {
		if(Clipboard.getContent() instanceof Path) {
			Clipboard.paste(target);
		}
	}

	/**
	 * Starts showing the popup for renaming a file.
	 *
	 * @param path Path to the file to rename
	 */
	private void rename(Path path) {
		this.textInputPopup.setTitle("Rename file");
		this.textInputPopup.setContent("Rename file \"" + path + "\" to:");
		this.textInputPopup.setInput(path.getFileName().toString());
		// TODO: Update path when the currently open scene is renamed
		this.textInputPopup.open(name -> EditorFileUtils.rename(path, name));
	}

	/**
	 * Starts showing the popup for deleting a file.
	 *
	 * @param path The file that should be deleted
	 */
	private void delete(Path path) {
		this.askConfirmationPopup.setTitle("Delete file");
		if(Files.isDirectory(path)) {
			this.askConfirmationPopup.setContent("Delete directory \"" + path + "\" and all of its content?");
		} else {
			this.askConfirmationPopup.setContent("Delete file \"" + path + "\"?");
		}
		this.askConfirmationPopup.open(() -> EditorFileUtils.delete(path));
	}

	@Override
	protected String dragType() {
		return "Path";
	}

	@Override
	protected String[] acceptablePayloads() {
		return new String[] {"Path"};
	}

	@Override
	protected void onDrop(Path target, Object payload) {
		if(payload instanceof Path path) try {
			target = Files.isDirectory(target) ? target : target.getParent();
			target = Path.of(target.toString(), path.getFileName().toString());
			Files.move(path, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
