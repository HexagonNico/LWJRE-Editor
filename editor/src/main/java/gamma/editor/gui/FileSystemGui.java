package gamma.editor.gui;

import gamma.editor.ProjectPath;
import gamma.editor.controls.Clipboard;
import gamma.editor.controls.EditorScene;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileSystemGui extends TreeWindowGui<Path> {

	@Override
	protected String title() {
		return "File system";
	}

	@Override
	protected Path getRoot() {
		return ProjectPath.resourcesFolder();
	}

	@Override
	protected String getRootLabel() {
		return "src/main/resources";
	}

	@Override
	protected void drawNode(Path node, String label, Path parent) {
		super.drawNode(node, label, parent);
		if(this.isSelected(node) && ImGui.isWindowFocused()) {
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
				if (ImGui.isKeyPressed(GLFW.GLFW_KEY_N)) {
					newDirectory(node, parent);
				} else if (parent != null && ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
					cutPath(node);
				} else if (parent != null && ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
					copyPath(node);
				} else if (ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
					pastePath(node);
				}
			}
		}
	}

	@Override
	protected void drawChildren(Path node) {
		try(Stream<Path> files = Files.list(node)) {
			files.sorted((path1, path2) -> {
				if(Files.isDirectory(path1) == Files.isDirectory(path2))
					return path1.getFileName().toString().compareToIgnoreCase(path2.getFileName().toString());
				return Files.isDirectory(path1) ? -1 : 1;
			}).forEach(path -> this.drawNode(path, path.getFileName().toString(), node));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isLeaf(Path node) {
		return !Files.isDirectory(node);
	}

	@Override
	protected ContextMenu contextMenu(Path node, String label, Path parent) {
		ContextMenu contextMenu = super.contextMenu(node, label, parent);
		contextMenu.menuItem("New folder", "Ctrl+N", () -> newDirectory(node, parent));
		contextMenu.separator();
		contextMenu.menuItem("Cut", "Ctrl+X", () -> cutPath(node));
		contextMenu.menuItem("Copy", "Ctrl+C", () -> copyPath(node));
		contextMenu.menuItem("Paste", "Ctrl+V", () -> pastePath(node));
		contextMenu.separator();
		contextMenu.menuItem("Delete", "Del"); // TODO: Ask confirm popup
		return contextMenu;
	}

	@Override
	protected String[] acceptablePayloads() {
		return new String[] {"Path", "SceneFile"};
	}

	@Override
	protected void onDrag(Path node, String label, Path parent) {
		if(label.endsWith(".yaml") || label.endsWith(".yml")) {
			ImGui.setDragDropPayload("SceneFile", node);
		} else {
			ImGui.setDragDropPayload("Path", node);
		}
	}

	@Override
	protected void onDrop(Path target, Object payload) {
		System.out.println(payload);
		if(payload instanceof Path path) try {
			if(!isDescendant(path, target)) {
				target = Files.isDirectory(target) ? target : target.getParent();
				target = Path.of(target.toString(), path.getFileName().toString());
				Files.move(path, target);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isDescendant(Path ancestor, Path descendant) {
		if(descendant.getParent().equals(ancestor)) {
			return true;
		} else if(Files.isDirectory(ancestor)) {
			try(Stream<Path> files = Files.list(ancestor)) {
				return files.filter(Files::isDirectory).anyMatch(next -> isDescendant(next, descendant));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected void onRename(Path node, String oldName, String newName, Path parent) {
		if(!EditorScene.currentPath().equals(node)) try {
			int newDotIndex = newName.lastIndexOf('.');
			if(newDotIndex != -1) {
				newName = newName.substring(0, newDotIndex);
			}
			Path newPath = Path.of(parent.toString(), newName);
			int oldDotIndex = oldName.lastIndexOf('.');
			if(oldDotIndex != -1) {
				newPath = Path.of(newPath + oldName.substring(oldDotIndex));
			}
			Files.move(node, newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void newDirectory(Path path, Path parent) {
		try {
			Path newPath = Files.isDirectory(path) ? Path.of(path.toString(), "folder") : Path.of(parent.toString(), "folder");
			Files.createDirectories(newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	private static void pastePath(Path target) {
		if(Clipboard.getContent() instanceof Path) {
			Clipboard.paste(target);
		}
	}

	@Override
	protected void onDoubleClick(Path node) {
		String pathStr = node.toString();
		if(pathStr.endsWith(".yaml") || pathStr.endsWith(".yml")) {
			GuiManager.get(InspectorGui.class).setNode(null);
			EditorScene.changeScene(node);
		}
	}
}
