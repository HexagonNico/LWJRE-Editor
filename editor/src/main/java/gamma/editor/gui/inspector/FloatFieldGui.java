package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorAngle;
import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImFloat;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FloatFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		EditorAngle angle = field.getAnnotation(EditorAngle.class);
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		if(angle != null) {
			float[] ptr = new float[] {field.getFloat(component)};
			if(ImGui.sliderAngle("##" + component.getClass() + ":" + field.getName(), ptr, angle.min(), angle.max())) {
				field.set(component, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(slider != null) {
			float[] ptr = new float[] {field.getFloat(component)};
			if(ImGui.sliderFloat("##" + component.getClass() + ":" + field.getName(), ptr, slider.min(), slider.max())) {
				field.set(component, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			float[] ptr = new float[] {field.getFloat(component)};
			if(ImGui.dragFloat("##" + component.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
				field.set(component, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImFloat ptr = new ImFloat(field.getFloat(component));
			if(ImGui.inputFloat("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
