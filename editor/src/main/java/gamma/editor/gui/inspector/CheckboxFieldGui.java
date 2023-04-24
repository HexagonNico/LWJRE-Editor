package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;
import imgui.ImGui;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Gui component to render a boolean field as a checkbox.
 *
 * @author Nico
 */
public class CheckboxFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		boolean current = field.getBoolean(component);
		if(ImGui.checkbox("##" + component.getClass() + ":" + field.getName(), current)) {
			field.set(component, !current);
			values.put(field.getName(), !current);
		}
	}
}
