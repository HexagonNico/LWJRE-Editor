package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;

public class TextFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		// TODO: Text options with annotations
		ImString ptr = new ImString((String) field.get(component), 256);
		if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
			field.set(component, ptr.get());
		}
	}
}
