package gamma.editor.demo;

import gamma.engine.annotations.EditorVariable;
import gamma.engine.tree.Node3D;

public class TestNode extends Node3D {

	@EditorVariable
	public int test = 0;

	@EditorVariable
	public float test2 = 44.0f;
}
