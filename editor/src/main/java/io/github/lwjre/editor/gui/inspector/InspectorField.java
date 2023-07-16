package io.github.lwjre.editor.gui.inspector;

import java.lang.reflect.Field;
import java.util.HashMap;

public interface InspectorField {

	void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException;
}
