package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImFloat;
import io.github.lwjre.engine.annotations.EditorAngle;
import io.github.lwjre.engine.annotations.EditorRange;
import io.github.lwjre.engine.annotations.EditorSlider;
import io.github.lwjre.engine.nodes.Node;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FloatField implements InspectorField {

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		EditorAngle angle = field.getAnnotation(EditorAngle.class);
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		String label = "##" + node.getClass() + ":" + field.getName();
		if(angle != null) {
			float[] ptr = new float[] {field.getFloat(node)};
			if(ImGui.sliderAngle(label, ptr, angle.min(), angle.max())) {
				field.set(node, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(slider != null) {
			float[] ptr = new float[] {field.getFloat(node)};
			if(ImGui.sliderFloat(label, ptr, slider.min(), slider.max())) {
				field.set(node, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			float[] ptr = new float[] {field.getFloat(node)};
			if(ImGui.dragFloat(label, ptr, range.step(), range.min(), range.max())) {
				field.set(node, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImFloat ptr = new ImFloat(field.getFloat(node));
			if(ImGui.inputFloat(label, ptr)) {
				field.set(node, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
