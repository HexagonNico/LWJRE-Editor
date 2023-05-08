package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.annotations.EditorSlider;
import gamma.engine.tree.Node;
import imgui.ImGui;
import imgui.type.ImInt;

import java.lang.reflect.Field;
import java.util.HashMap;

public class IntField implements InspectorField {

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		EditorSlider slider = field.getAnnotation(EditorSlider.class);
		EditorRange range = field.getAnnotation(EditorRange.class);
		String label = "##" + node.getClass() + ":" + field.getName();
		if(slider != null) {
			int[] ptr = new int[] {field.getInt(node)};
			if(ImGui.sliderInt(label, ptr, (int) slider.min(), (int) slider.max())) {
				field.set(node, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else if(range != null) {
			int[] ptr = new int[] {field.getInt(node)};
			if(ImGui.dragInt(label, ptr, range.step(), range.min(), range.max())) {
				field.set(node, ptr[0]);
				values.put(field.getName(), ptr[0]);
			}
		} else {
			ImInt ptr = new ImInt(field.getInt(node));
			if(ImGui.inputInt(label, ptr)) {
				field.set(label, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		}
	}
}
