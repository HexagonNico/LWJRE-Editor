package gamma.engine.editor.model;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class FileTreeModel implements TreeModel {

	@Override
	public Object getRoot() {
		return new FileNode("src/main/resources");
	}

	@Override
	public Object getChild(Object parent, int index) {
		try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
			return files.map(FileNode::new).sorted().toList().get(index);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getChildCount(Object parent) {
		try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
			return files.map(FileNode::new).sorted().toList().size();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isLeaf(Object node) {
		return !Files.isDirectory(((FileNode) node).path());
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object o) {
		System.out.println(treePath + " -> " + o);
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
			return files.map(FileNode::new).sorted().toList().indexOf((FileNode) child);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener treeModelListener) {

	}

	@Override
	public void removeTreeModelListener(TreeModelListener treeModelListener) {

	}

	private static class FileNode extends DefaultMutableTreeNode implements Comparable<FileNode> {

		private FileNode(Path path) {
			this.setUserObject(path);
		}

		private FileNode(String path) {
			this(Path.of(path));
		}

		private Path path() {
			return (Path) this.getUserObject();
		}

		@Override
		public String toString() {
			return this.path().toString().equals("src/main/resources") ?
					"src/main/resources" :
					this.path().getFileName().toString();
		}

		@Override
		public int compareTo(FileNode fileNode) {
			return Files.isDirectory(this.path()) == Files.isDirectory(fileNode.path()) ?
					this.path().getFileName().toString().compareToIgnoreCase(fileNode.path().getFileName().toString()) :
					Files.isDirectory(this.path()) ? -1 : 1;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof FileNode node && node.getUserObject().equals(this.path());
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.getUserObject());
		}
	}
}
