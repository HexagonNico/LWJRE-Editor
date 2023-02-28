package gamma.engine.editor.scene;

import gamma.engine.core.scene.Entity;
import gamma.engine.core.scene.Scene;
import gamma.engine.editor.TreeView;
import gamma.engine.editor.inspector.InspectorPanel;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SceneTree extends TreeView implements TreeSelectionListener {

	private final InspectorPanel inspectorPanel;

	public SceneTree(InspectorPanel inspectorPanel) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode()));
		this.setShowsRootHandles(true);
		this.setDragEnabled(true);
		this.addTreeSelectionListener(this);
		this.inspectorPanel = inspectorPanel;
	}

	@Override
	protected void refreshView() {
		SceneNode root = new SceneNode(Scene.getCurrent().root, "root");
		addChildren(root, Scene.getCurrent().root);
		DefaultTreeModel model = new DefaultTreeModel(root);
		super.refreshView(model);
	}

	private static void addChildren(SceneNode node, Entity entity) {
		entity.childrenMap().forEach((key, child) -> {
			SceneNode childNode = new SceneNode(child, key);
			addChildren(childNode, child);
			node.add(childNode);
		});
	}

	@Override
	public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
		SceneNode node = (SceneNode) this.getLastSelectedPathComponent();
		if(node == null)
			this.inspectorPanel.clearEntity();
		else
			this.inspectorPanel.setEntity(node.entity);
	}
}
