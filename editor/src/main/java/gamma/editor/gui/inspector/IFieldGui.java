package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;

import java.lang.reflect.Field;

public interface IFieldGui {

	void drawGui(Component component, Field field) throws IllegalAccessException;
}
