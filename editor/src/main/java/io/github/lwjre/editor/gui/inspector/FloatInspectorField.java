package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImFloat;
import io.github.lwjre.engine.annotations.EditorAngle;
import io.github.lwjre.engine.annotations.EditorRange;
import io.github.lwjre.engine.annotations.EditorSlider;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FloatInspectorField implements InspectorField {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		EditorAngle angle = field.getAnnotation(EditorAngle.class);
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		String label = "##" + object.getClass() + ":" + field.getName();
		if(angle != null) {
			float[] ptr = new float[] {field.getFloat(object)};
			if(ImGui.sliderAngle(label, ptr, angle.min(), angle.max())) {
				field.set(object, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(slider != null) {
			float[] ptr = new float[] {field.getFloat(object)};
			if(ImGui.sliderFloat(label, ptr, slider.min(), slider.max())) {
				field.set(object, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			float[] ptr = new float[] {field.getFloat(object)};
			if(ImGui.dragFloat(label, ptr, range.step(), range.min(), range.max())) {
				field.set(object, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImFloat ptr = new ImFloat(field.getFloat(object));
			if(ImGui.inputFloat(label, ptr)) {
				field.set(object, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
