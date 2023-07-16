package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import io.github.hexagonnico.vecmatlib.color.Color3f;
import io.github.hexagonnico.vecmatlib.color.Color4f;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ColorInspectorField implements InspectorField {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		Object obj = field.get(object);
		if(obj instanceof Color3f color) {
			float[] ptr = {color.r(), color.g(), color.b()}; // TODO: Color flags
			if(ImGui.colorEdit3("##" + object.getClass() + ":" + field.getName(), ptr)) {
				color = new Color3f(ptr[0], ptr[1], ptr[2]);
				field.set(object, color);
				values.put(field.getName(), color);
			}
		} else if(obj instanceof Color4f color) {
			float[] ptr = {color.r(), color.g(), color.b(), color.a()}; // TODO: Color flags
			if(ImGui.colorEdit4("##" + object.getClass() + ":" + field.getName(), ptr)) {
				color = new Color4f(ptr[0], ptr[1], ptr[2], ptr[3]);
				field.set(object, color);
				values.put(field.getName(), color);
			}
		}
	}
}
