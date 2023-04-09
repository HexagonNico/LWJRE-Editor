package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorDegrees;
import gamma.engine.annotations.EditorRange;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImFloat;

import java.lang.reflect.Field;

/**
 * Gui component to render a float or double field as {@link ImGui#inputFloat(String, ImFloat)} or {@link ImGui#dragFloat(String, float[])} or {@link ImGui#sliderFloat(String, float[], float, float)}.
 *
 * @author Nico
 */
public class RealFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		if(range != null) {
			float value = (float) field.getDouble(component);
			// TODO: There is also inputAngle
			float[] ptr = {field.isAnnotationPresent(EditorDegrees.class) ? (float) Math.toDegrees(value) : value};
			if(range.slider()) {
				if(ImGui.sliderFloat("##" + component.getClass() + ":" + field.getName(), ptr, range.min(), range.max())) {
					field.set(component, ptr[0]);
				}
			} else if(ImGui.dragFloat("##" + component.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
				field.set(component, field.isAnnotationPresent(EditorDegrees.class) ? (float) Math.toRadians(ptr[0]) : ptr[0]);
			}
		} else {
			ImFloat ptr = new ImFloat((float) field.getDouble(component));
			// TODO: There is also ImGui#inputDouble
			if(ImGui.inputFloat("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
			}
		}
	}
}
