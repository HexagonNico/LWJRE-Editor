package gamma.editor.gui.inspector;

import gamma.engine.tree.Node;

import java.lang.reflect.Field;
import java.util.HashMap;

public interface InspectorField {

	void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException;
}
