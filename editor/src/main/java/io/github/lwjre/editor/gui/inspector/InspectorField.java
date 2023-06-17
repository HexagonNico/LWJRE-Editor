package io.github.lwjre.editor.gui.inspector;

import io.github.lwjre.engine.nodes.Node;

import java.lang.reflect.Field;
import java.util.HashMap;

public interface InspectorField {

	void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException;
}
