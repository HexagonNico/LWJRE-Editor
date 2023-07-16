package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CheckboxInspectorField implements InspectorField {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		boolean current = field.getBoolean(object);
		if(ImGui.checkbox("##" + object.getClass() + ":" + field.getName(), current)) {
			field.set(object, !current);
			values.put(field.getName(), !current);
		}
	}
}
