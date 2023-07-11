package io.github.lwjre.editor.gui.fields;

import imgui.ImGui;
import imgui.type.ImString;
import io.github.lwjre.engine.annotations.EditorText;

import java.lang.reflect.Field;
import java.util.HashMap;

public class TextFieldGui implements FieldGui {

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		EditorText text = field.getAnnotation(EditorText.class);
		int length = text != null ? text.maxLength() : 256;
		ImString ptr = new ImString((String) field.get(object), length);
		if(text != null && text.multiline()) {
			if(ImGui.inputTextMultiline("##" + object.getClass() + ":" + field.getName(), ptr)) {
				field.set(object, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if(text != null && !text.hint().isEmpty()) {
			if(ImGui.inputTextWithHint("##" + object.getClass() + ":" + field.getName(), text.hint(), ptr)) {
				field.set(object, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if (ImGui.inputText("##" + object.getClass() + ":" + field.getName(), ptr)) {
			field.set(object, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
