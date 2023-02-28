package gamma.engine.editor;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public abstract class TreeView extends JTree {

	private static final ArrayList<TreeView> TREES = new ArrayList<>();

	public static void refreshAll() {
		TREES.forEach(TreeView::refreshView);
	}

	public TreeView(TreeModel treeModel) {
		super(treeModel);
		TREES.add(this);
	}

	protected abstract void refreshView();

	protected void refreshView(TreeModel treeModel) {
		ArrayList<TreePath> expanded = new ArrayList<>();
		for(int i = 0; i < this.getRowCount() - 1; i++) {
			if(this.isExpanded(i)) {
				expanded.add(this.getPathForRow(i));
			}
		}
		this.setModel(treeModel);
		for(TreePath path : expanded) {
			this.expandPath(path);
		}
	}
}
