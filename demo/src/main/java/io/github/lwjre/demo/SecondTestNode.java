package io.github.lwjre.demo;

import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.lwjre.engine.annotations.EditorVariable;
import io.github.lwjre.engine.nodes.Node3D;

public class SecondTestNode extends Node3D {

	@EditorVariable
	public Color4f color = Color4f.Black();
}
