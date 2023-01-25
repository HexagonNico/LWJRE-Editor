package gamma.engine.editor.panels;

import gamma.engine.core.tree.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class SceneTreePanel extends JPanel {

	private static final SceneTreePanel SINGLETON = new SceneTreePanel();

	public static SceneTreePanel instance() {
		return SINGLETON;
	}

	private final JTree sceneTree = new JTree();

	private SceneTreePanel() {
		this.setLayout(new BorderLayout());
		this.sceneTree.setDragEnabled(true);
//		this.sceneTree.setTransferHandler();
//		this.sceneTree.addMouseListener();
		JScrollPane scrollPane = new JScrollPane(this.sceneTree);
		this.add(BorderLayout.CENTER, scrollPane);
	}

	public void loadScene(String file) {
		DefaultTreeModel treeModel = (DefaultTreeModel) this.sceneTree.getModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
//		this.createNodes(root, rootNode);
//		root.setUserObject(rootNode);
		treeModel.setRoot(root);
		treeModel.reload();
	}

	private void createNodes(DefaultMutableTreeNode treeNode, Node node) {
		node.getChildren().forEach(child -> {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
			this.createNodes(childNode, child);
			treeNode.add(childNode);
		});
	}
}
