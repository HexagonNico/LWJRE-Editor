package gamma.engine.editor.model;

import gamma.engine.core.node.Node;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Objects;

public class SceneTreeModel implements TreeModel {

	private final Node root;

	public SceneTreeModel(Node root) {
		this.root = root;
	}

	@Override
	public Object getRoot() {
		return new SceneNode(this.root);
	}

	@Override
	public Object getChild(Object parent, int index) {
		return new SceneNode(((SceneNode) parent).node().getChildren().toList().get(index));
	}

	@Override
	public int getChildCount(Object parent) {
		return ((SceneNode) parent).node().getChildCount();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((SceneNode) node).node() == null || ((SceneNode) node).node().getChildCount() == 0;
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object o) {

	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((SceneNode) parent).node().getChildren().toList().indexOf(((SceneNode) child).node());
	}

	@Override
	public void addTreeModelListener(TreeModelListener treeModelListener) {

	}

	@Override
	public void removeTreeModelListener(TreeModelListener treeModelListener) {

	}

	private static class SceneNode extends DefaultMutableTreeNode {

		private SceneNode(Node node) {
			this.setUserObject(node);
		}

		private Node node() {
			return (Node) this.getUserObject();
		}

		@Override
		public String toString() {
			return this.node() != null ? this.node().getClass().getSimpleName() : "null";
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof SceneNode node && node.getUserObject() != null && node.getUserObject().equals(this.node());
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.node());
		}
	}
}
