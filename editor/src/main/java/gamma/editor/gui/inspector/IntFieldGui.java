package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImInt;

import java.lang.reflect.Field;
import java.util.HashMap;

public class IntFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		if(slider != null) {
			int[] ptr = new int[] {field.getInt(component)};
			if(ImGui.sliderInt("##" + component.getClass() + ":" + field.getName(), ptr, (int) slider.min(), (int) slider.max())) {
				field.set(component, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			int[] ptr = new int[] {field.getInt(component)};
			if(ImGui.dragInt("##" + component.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
				field.set(component, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImInt ptr = new ImInt(field.getInt(component));
			if(ImGui.inputInt("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
