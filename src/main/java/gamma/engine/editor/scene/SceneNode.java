package gamma.engine.editor.scene;

import gamma.engine.core.scene.Entity;

import javax.swing.tree.DefaultMutableTreeNode;

public class SceneNode extends DefaultMutableTreeNode {

	public final Entity entity;

	public SceneNode(Entity entity, String name) {
		super(name);
		this.entity = entity;
	}
}
