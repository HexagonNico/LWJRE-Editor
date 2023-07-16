package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImInt;
import io.github.lwjre.engine.annotations.EditorRange;
import io.github.lwjre.engine.annotations.EditorSlider;

import java.lang.reflect.Field;
import java.util.HashMap;

public class IntInspectorField implements InspectorField {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		String label = "##" + object.getClass() + ":" + field.getName();
		if(slider != null) {
			int[] ptr = new int[] {field.getInt(object)};
			if(ImGui.sliderInt(label, ptr, (int) slider.min(), (int) slider.max())) {
				field.set(object, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			int[] ptr = new int[] {field.getInt(object)};
			if(ImGui.dragInt(label, ptr, range.step(), range.min(), range.max())) {
				field.set(object, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImInt ptr = new ImInt(field.getInt(object));
			if(ImGui.inputInt(label, ptr)) {
				field.set(object, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
