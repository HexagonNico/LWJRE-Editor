package gamma.editor.gui;

import gamma.editor.ProjectPath;
import gamma.editor.controls.Clipboard;
import gamma.editor.controls.DragDropPayload;
import gamma.editor.controls.EditorScene;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemGui extends TreeGui<Path> {

	private final InspectorGui inspector;

	public FileSystemGui(InspectorGui inspector) {
		this.inspector = inspector;
	}

	@Override
	protected String title() {
		return "File System";
	}

	@Override
	protected void drawWindow() {
		super.drawWindow();
		if(ImGui.isWindowFocused()) {
			if(ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
				if(ImGui.isKeyPressed(GLFW.GLFW_KEY_N)) {
					newDirectory(this.getSelected(), this.getSelected().getParent());
				} else if(!this.getSelected().equals(this.getRoot()) && ImGui.isKeyPressed(GLFW.GLFW_KEY_X)) {
					cutPath(this.getSelected());
				} else if(!this.getSelected().equals(this.getRoot()) && ImGui.isKeyPressed(GLFW.GLFW_KEY_C)) {
					copyPath(this.getSelected());
				} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V)) {
					pastePath(this.getSelected());
				}
			}
		}
	}

	@Override
	protected void onDrawNode(Path path, String label, Path parent) {
		super.onDrawNode(path, label, parent);
		if(ImGui.beginPopupContextItem()) {
			this.onSelect(path);
			if(ImGui.menuItem("New folder", "Ctrl+N")) {
				newDirectory(path, parent);
			}
			ImGui.separator();
			if(ImGui.menuItem("Cut", "Ctrl+X")) {
				cutPath(path);
			}
			if(ImGui.menuItem("Copy", "Ctrl+C")) {
				copyPath(path);
			}
			if(ImGui.menuItem("Paste", "Ctrl+V")) {
				pastePath(path);
			}
			ImGui.separator();
			if(ImGui.menuItem("Delete", "Del")) {

			}
			ImGui.endPopup();
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
	protected Path getRoot() {
		return ProjectPath.resourcesFolder();
	}

	@Override
	protected String getLabel(Path path, Path parent) {
		return path.getFileName().toString();
	}

	@Override
	protected boolean isLeaf(Path path) {
		return !Files.isDirectory(path);
	}

	@Override
	protected Iterable<Path> getChildren(Path path) {
		try(Stream<Path> files = Files.list(path)) {
			return files.sorted((path1, path2) -> {
				if(Files.isDirectory(path1) == Files.isDirectory(path2))
					return path1.getFileName().toString().compareToIgnoreCase(path2.getFileName().toString());
				return Files.isDirectory(path1) ? -1 : 1;
			}).toList();
		} catch (IOException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	@Override
	protected String dragDropType() {
		return "Path";
	}

	@Override
	protected void onDragDropTarget(Path target, DragDropPayload payload) {
		if(payload.object() instanceof Path dropPath) try {
			if(!isDescendant(dropPath, target)) {
				target = Files.isDirectory(target) ? target : target.getParent();
				target = Path.of(target.toString(), dropPath.getFileName().toString());
				Files.move(dropPath, target);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isDescendant(Path ancestor, Path descendant) {
		if(descendant.getParent().equals(ancestor)) {
			return true;
		} else if(Files.isDirectory(ancestor)) try(Stream<Path> files = Files.list(ancestor)) {
			return files.filter(Files::isDirectory).anyMatch(next -> isDescendant(next, descendant));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onDoubleClick(Path path) {
		String pathStr = path.toString();
		if(pathStr.endsWith(".yaml") || pathStr.endsWith(".yml")) {
			this.inspector.nodeResource = null;
			EditorScene.changeScene(path);
		}
	}

	@Override
	protected void onRename(Path path, String name, Path parent) {
		try {
			Path newPath = Path.of(path.getParent().toString(), name);
			Files.move(path, newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
