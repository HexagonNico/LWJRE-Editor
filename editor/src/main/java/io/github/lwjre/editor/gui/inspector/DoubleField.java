package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImDouble;
import io.github.lwjre.engine.nodes.Node;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DoubleField implements InspectorField {

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		ImDouble ptr = new ImDouble(field.getDouble(node));
		if(ImGui.inputDouble("##" + node.getClass() + ":" + field.getName(), ptr)) {
			field.set(node, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
