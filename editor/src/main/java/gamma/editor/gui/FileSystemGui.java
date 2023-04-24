package gamma.editor.gui;

import gamma.editor.EditorApplication;
import gamma.editor.controls.EditorScene;
import gamma.editor.controls.FileClipboard;
import gamma.engine.resources.Resources;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import vecmatlib.vector.Vec2i;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Gui component that represents the file system view in the editor.
 *
 * @author Nico
 */
public class FileSystemGui implements IGui {

	// TODO: Implement Ctrl+X, Ctrl+C, Ctrl+V
	// TODO: Implement right-click menu

	private Path renaming;

	private final FileClipboard clipboard = new FileClipboard();

	@Override
	public void draw() {
		// Window size and position
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(5.0f, windowSize.y() / 2.0f + 20.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 25.0f, ImGuiCond.FirstUseEver);
		// Show window
		if(ImGui.begin("File system")) {
			// Show tree recursively
			this.showTreeNode(Path.of(EditorApplication.currentPath() + "/src/main/resources"));
		}
		ImGui.end();
	}

	/**
	 * Recursive function used to show a tree node that represents a file or a directory in the file system.
	 *
	 * @param path Path of the file or directory that represents this node
	 */
	private void showTreeNode(Path path) {
		// Default node flags
		int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen;
		// Hide the arrow if this is a file and not a directory
		if(!Files.isDirectory(path)) flags = flags | ImGuiTreeNodeFlags.Leaf;
		String fileName = path.getFileName().toString();
		if(this.clipboard.isInClipboard(path))
			ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
		if(ImGui.treeNodeEx(fileName, flags, path.equals(this.renaming) ? "" : fileName)) {
			if(this.clipboard.isInClipboard(path))
				ImGui.popStyleColor();
			doDragAndDrop(fileName, path);
			doOpenNode(fileName, path);
			this.doRightClickMenu(path);
			this.doRenamingInput(path);
			// Recursively show all contained files if this is a directory
			if(Files.isDirectory(path)) {
				try(Stream<Path> files = Files.list(path)) {
					files.sorted((path1, path2) -> {
						if(Files.isDirectory(path1) == Files.isDirectory(path2))
							return path1.getFileName().toString().compareToIgnoreCase(path2.getFileName().toString());
						return Files.isDirectory(path1) ? -1 : 1;
					}).forEach(this::showTreeNode);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ImGui.treePop();
		}
	}

	/**
	 * Handles the drag and drop feature for a tree node.
	 *
	 * @param fileName Name of the file, shown when dragging
	 * @param path Path representing the current node
	 */
	private static void doDragAndDrop(String fileName, Path path) {
		// Set payload for drag
		if(ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload("Path", path);
			ImGui.text(fileName);
			ImGui.endDragDropSource();
		}
		// Get payload for drop
		if(ImGui.beginDragDropTarget()) {
			// Accept payload when the drag operation ends
			if(ImGui.acceptDragDropPayload("Path") instanceof Path pathToMove) {
				// The directory itself if dragged on a directory, the parent directory if dragged on a file
				Path destination = Path.of((Files.isDirectory(path) ? path : path.getParent()).toString(), pathToMove.getFileName().toString());
				try {
					// Move file
					Files.move(pathToMove, destination);
					// Update resource path in resources class
					pathToMove = Path.of(EditorApplication.currentPath() + "/src/main/resources").relativize(pathToMove);
					destination = Path.of(EditorApplication.currentPath() + "/src/main/resources").relativize(destination);
					// TODO: The editor cashes if a file is moved while it is used somewhere else
					Resources.updatePath(pathToMove.toString(), destination.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ImGui.endDragDropTarget();
		}
	}

	/**
	 * Handles the double click function for nodes.
	 *
	 * @param fileName Name of the file, needed to get the type of file
	 * @param path Path representing the current node
	 */
	private static void doOpenNode(String fileName, Path path) {
		// Detect when an item is double-clicked
		if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
			// TODO: Open different types of file
			if(fileName.endsWith(".yaml")) {
				EditorScene.changeScene(Path.of(EditorApplication.currentPath(), "src/main/resources").relativize(path).toString());
			}
		}
	}

	private void doRightClickMenu(Path path) {
		if(ImGui.beginPopupContextItem()) {
			if(ImGui.menuItem("New folder", "Ctrl+A")) {
				Path directoryPath = Path.of(path.toString(), "newFolder");
				if(!Files.exists(directoryPath)) try {
					Files.createDirectories(directoryPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ImGui.separator();
			if(ImGui.menuItem("Cut", "Ctrl+X")) {
				this.clipboard.cut(path);
			}
			if(ImGui.menuItem("Copy", "Ctrl+C")) {
				this.clipboard.copy(path);
			}
			if(ImGui.menuItem("Paste", "Ctrl+V")) {
				this.clipboard.paste(path);
			}
			ImGui.separator();
			if(ImGui.menuItem("Delete file", "Delete")) try {
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(ImGui.menuItem("Rename file", "F2")) {
				this.renaming = path;
			}
			ImGui.endPopup();
		}
	}

	private void doRenamingInput(Path path) {
		if(path.equals(this.renaming)) {
			ImGui.sameLine();
			ImGui.setKeyboardFocusHere();
			ImString ptr = new ImString(path.getFileName().toString(), 256);
			if(ImGui.inputText("##" + path, ptr, ImGuiInputTextFlags.EnterReturnsTrue)) try {
				Files.move(path, Path.of(path.getParent().toString(), ptr.get()));
				this.renaming = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
