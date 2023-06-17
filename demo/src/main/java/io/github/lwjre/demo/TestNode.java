package io.github.lwjre.demo;

import io.github.lwjre.engine.annotations.EditorVariable;
import io.github.lwjre.engine.nodes.Node3D;

public class TestNode extends Node3D {

	@EditorVariable
	public String string = "Hello!";

	@EditorVariable
	public int test = 42;
}
