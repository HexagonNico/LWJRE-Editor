package gamma.engine.editor.filesystem;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

public class FileTreeModel implements TreeModel {

	@Override
	public Object getRoot() {
		return new FileNode("src/main/resources", true);
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((FileNode) parent).listFileNodes().toList().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		File[] files = ((FileNode) parent).file().listFiles();
		return files != null ? files.length : 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((FileNode) node).file().isFile();
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object o) {

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((FileNode) parent).listFileNodes().toList().indexOf((FileNode) child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener treeModelListener) {

	}

	@Override
	public void removeTreeModelListener(TreeModelListener treeModelListener) {

	}
}
