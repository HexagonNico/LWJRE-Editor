package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;
import imgui.ImGui;

import java.lang.reflect.Field;

public class CheckboxFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		boolean current = field.getBoolean(component);
		if(ImGui.checkbox("##" + component.getClass() + ":" + field.getName(), current)) {
			field.set(component, !current);
		}
	}
}
