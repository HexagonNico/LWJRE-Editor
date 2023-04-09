package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImInt;

import java.lang.reflect.Field;

/**
 * Gui component to render a byte, short, int, or long field as a {@link ImGui#inputInt(String, ImInt)} or {@link ImGui#dragInt(String, int[])} or {@link ImGui#sliderInt(String, int[], int, int)}.
 *
 * @author Nico
 */
public class IntegerFieldGui implements IFieldGui {

	// TODO: Make sure this works with byte/short/long

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		if(range != null) {
			int[] ptr = {(int) field.getLong(component)};
			if(range.slider()) {
				if(ImGui.sliderInt("##" + component.getClass() + ":" + field.getName(), ptr, (int) range.min(), (int) range.max())) {
					field.set(component, ptr[0]);
				}
			} else if (ImGui.dragInt("##" + component.getClass() + ":" + field.getName(), ptr, range.step(), range.min(), range.max())) {
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
