package gamma.editor.gui.inspector;

import gamma.engine.tree.Node;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CheckboxField implements InspectorField {

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		boolean current = field.getBoolean(node);
		if(ImGui.checkbox("##" + node.getClass() + ":" + field.getName(), current)) {
			field.set(node, !current);
			values.put(field.getName(), !current);
		}
	}
}
