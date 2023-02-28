package gamma.engine.editor.filesystem;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileNode extends DefaultMutableTreeNode implements Comparable<FileNode> {

	private final boolean isRoot;

	public FileNode(String path, boolean isRoot) {
		this(new File(path), isRoot);
	}

	public FileNode(File file) {
		this(file, false);
	}

	public FileNode(File file, boolean isRoot) {
		this.setUserObject(file);
		this.isRoot = isRoot;
	}

	public File file() {
		return (File) this.getUserObject();
	}

	public Stream<FileNode> listFileNodes() {
		File[] files = this.file().listFiles();
		return files != null ? Arrays.stream(files).map(FileNode::new).sorted() : Stream.empty();
	}

	@Override
	public String toString() {
		return this.isRoot ? super.toString() : this.file().getName();
	}

	@Override
	public int compareTo(FileNode fileNode) {
		if(this.file().isDirectory() == fileNode.file().isDirectory()) {
			return this.file().getName().compareToIgnoreCase(fileNode.file().getName());
		}
		return this.file().isDirectory() ? -1 : 1;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FileNode node && node.file().equals(this.file());
	}
}
