package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorText;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Gui component to represent a String field as a {@link ImGui#inputText(String, ImString)}.
 *
 * @author Nico
 */
public class TextFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		EditorText text = field.getAnnotation(EditorText.class);
		int length = text != null ? text.maxLength() : 256;
		ImString ptr = new ImString((String) field.get(component), length);
		if(text != null && text.multiline()) {
			if(ImGui.inputTextMultiline("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if(text != null && !text.hint().isEmpty()) {
			if(ImGui.inputTextWithHint("##" + component.getClass() + ":" + field.getName(), text.hint(), ptr)) {
				field.set(component, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if (ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
			field.set(component, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
