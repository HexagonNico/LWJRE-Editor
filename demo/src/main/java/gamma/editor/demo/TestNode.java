package gamma.editor.demo;

import gamma.engine.annotations.EditorVariable;
import gamma.engine.tree.Node3D;

public class TestNode extends Node3D {

	@EditorVariable
	public String string = "Hello!";

	@EditorVariable
	public int test = 0;
}
