package gamma.editor.controls;

import gamma.editor.EditorUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileClipboard {

	private Path path;
	private byte operation = 0;

	public void listenForKeyBinds(Path path) {

	}

	public void cut(Path path) {
		this.path = path;
		this.operation = 1;
	}

	public void copy(Path path) {
		this.path = path;
		this.operation = 2;
	}

	public void paste(Path path) {
		if(this.operation != 0) {
			Path directory = Files.isDirectory(path) ? path : path.getParent();
			String actualName = EditorUtils.findUnusedName(this.path.getFileName().toString(), directory);
			try {
				Path newPath = Path.of(directory.toString(), actualName);
				if(this.operation == 1) {
					Files.move(this.path, newPath);
					this.path = newPath;
					this.operation = 2;
				} else if(this.operation == 2) {
					Files.copy(this.path, newPath);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isInClipboard(Path path) {
		return path.equals(this.path) && this.operation == 1;
	}
}
