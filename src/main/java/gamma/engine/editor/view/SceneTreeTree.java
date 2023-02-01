package gamma.engine.editor.view;

import gamma.engine.core.node.Node;
import gamma.engine.editor.model.SceneTreeModel;

import javax.swing.*;

public class SceneTreeTree extends JTree {

	public SceneTreeTree() {
		super(new SceneTreeModel(null));
		this.setShowsRootHandles(true);
		this.setDragEnabled(true);
	}

	public void setScene(Node root) {
		this.setModel(new SceneTreeModel(root));
	}
}
