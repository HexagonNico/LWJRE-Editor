package gamma.editor.gui;

import gamma.editor.controls.DragDropPayload;
import gamma.editor.controls.EditorScene;

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
	protected Path getRoot() {
		return Path.of("demo/src/main/resources");
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
			if(!isDescendant(target, dropPath)) {
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
		} else try(Stream<Path> files = Files.list(ancestor)) {
			return files.anyMatch(next -> isDescendant(next, descendant));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
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
