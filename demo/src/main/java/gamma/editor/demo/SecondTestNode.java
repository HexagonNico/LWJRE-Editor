package gamma.editor.demo;

import gamma.engine.annotations.EditorVariable;
import gamma.engine.tree.Node3D;
import vecmatlib.color.Color4f;

public class SecondTestNode extends Node3D {

	@EditorVariable
	public Color4f color = Color4f.Black();
}
