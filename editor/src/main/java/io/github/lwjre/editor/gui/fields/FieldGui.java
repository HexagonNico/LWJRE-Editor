package io.github.lwjre.editor.gui.fields;

import java.lang.reflect.Field;
import java.util.HashMap;

public interface FieldGui {

	void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException;
}
