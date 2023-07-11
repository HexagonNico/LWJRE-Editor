package io.github.lwjre.editor.gui.fields;

import imgui.ImGui;
import imgui.type.ImDouble;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DoubleFieldGui implements FieldGui {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		ImDouble ptr = new ImDouble(field.getDouble(object));
		if(ImGui.inputDouble("##" + object.getClass() + ":" + field.getName(), ptr)) {
			field.set(object, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
