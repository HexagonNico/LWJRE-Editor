package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImDouble;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DoubleFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		ImDouble ptr = new ImDouble(field.getDouble(component));
		if(ImGui.inputDouble("##" + component.getClass() + ":" + field.getName(), ptr)) {
			field.set(component, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
