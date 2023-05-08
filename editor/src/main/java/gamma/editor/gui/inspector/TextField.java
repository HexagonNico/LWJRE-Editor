package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorText;
import gamma.engine.tree.Node;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.util.HashMap;

public class TextField implements InspectorField {

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		EditorText text = field.getAnnotation(EditorText.class);
		int length = text != null ? text.maxLength() : 256;
		ImString ptr = new ImString((String) field.get(node), length);
		if(text != null && text.multiline()) {
			if(ImGui.inputTextMultiline("##" + node.getClass() + ":" + field.getName(), ptr)) {
				field.set(node, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if(text != null && !text.hint().isEmpty()) {
			if(ImGui.inputTextWithHint("##" + node.getClass() + ":" + field.getName(), text.hint(), ptr)) {
				field.set(node, ptr.get());
				values.put(field.getName(), ptr.get());
			}
		} else if (ImGui.inputText("##" + node.getClass() + ":" + field.getName(), ptr)) {
			field.set(node, ptr.get());
			values.put(field.getName(), ptr.get());
		}
	}
}
