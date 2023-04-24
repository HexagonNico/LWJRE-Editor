package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Interface used to represent a class' field as an ImGui component.
 *
 * @author Nico
 */
public interface IFieldGui {

	void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException;
}
