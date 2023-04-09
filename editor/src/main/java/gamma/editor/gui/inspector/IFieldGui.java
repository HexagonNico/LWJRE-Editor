package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;

import java.lang.reflect.Field;

/**
 * Interface used to represent a class' field as an ImGui component.
 *
 * @author Nico
 */
public interface IFieldGui {

	/**
	 * Draws the given field as an ImGui gui.
	 *
	 * @param component The component that holds the field
	 * @param field The field to represent
	 * @throws IllegalAccessException If the field could not be accessed
	 */
	void drawGui(Component component, Field field) throws IllegalAccessException;
}
