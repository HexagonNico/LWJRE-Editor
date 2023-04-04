package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImInt;

import java.lang.reflect.Field;

public class IntegerFieldGui implements IFieldGui {

	private final float defaultMin;
	private final float defaultMax;

	public IntegerFieldGui(float defaultMin, float defaultMax) {
		this.defaultMin = defaultMin;
		this.defaultMax = defaultMax;
	}

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		if(field.isAnnotationPresent(EditorRange.class)) {
			EditorRange range = field.getAnnotation(EditorRange.class);
			int[] ptr = {(int) field.getLong(component)};
			float step = range != null ? range.step() : 0.001f;
			float min = range != null ? range.min() : defaultMin;
			float max = range != null ? range.max() : defaultMax;
			if(ImGui.dragInt("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, ptr[0]);
			}
		} else if(field.isAnnotationPresent(EditorSlider.class)) {
			EditorSlider slider = field.getAnnotation(EditorSlider.class);
			int[] ptr = {(int) field.getLong(component)};
			int min = (int) (slider != null ? slider.min() : defaultMin);
			int max = (int) (slider != null ? slider.max() : defaultMax);
			if(ImGui.sliderInt("##" + component.getClass() + ":" + field.getName(), ptr, min, max)) {
				field.set(component, ptr[0]);
			}
		} else {
			ImInt ptr = new ImInt((int) field.getLong(component));
			if(ImGui.inputInt("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
			}
		}
	}
}
