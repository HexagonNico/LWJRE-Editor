package gamma.editor.gui.inspector;

import gamma.editor.EditorApplication;
import gamma.engine.annotations.EditorResource;
import gamma.engine.resources.Resources;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.nio.file.Path;

/**
 * Gui component to represent a String field as a {@link ImGui#inputText(String, ImString)}.
 *
 * @author Nico
 */
public class TextFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		if(field.isAnnotationPresent(EditorResource.class)) {
			EditorResource resourceAnnotation = field.getAnnotation(EditorResource.class);
			String value = (String) field.get(component);
			ImString ptr = new ImString(value.equals(resourceAnnotation.defaultValue()) ? "" : value, 256);
			if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
				setResource(component, field, ptr.get());
			}
			if(ImGui.beginDragDropTarget()) {
				if(ImGui.acceptDragDropPayload("Path") instanceof Path path) {
					setResource(component, field, path.toString().substring(EditorApplication.currentPath().length() + 20));
				}
				ImGui.endDragDropTarget();
			}
		} else {
			// TODO: Text options with annotations
			ImString ptr = new ImString((String) field.get(component), 256);
			if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, ptr.get());
			}
		}
	}

	/**
	 * Loads a resource to check if it is correct, then sets the field to its path.
	 *
	 * @param component The component that holds the field
	 * @param field The string field
	 * @param path Path to the resource
	 * @throws IllegalAccessException If the field could not be accessed
	 */
	private static void setResource(Component component, Field field, String path) throws IllegalAccessException {
		EditorResource resourceAnnotation = field.getAnnotation(EditorResource.class);
		if(path.isEmpty() || path.isBlank()) {
			if(resourceAnnotation.defaultValue().isEmpty() || resourceAnnotation.defaultValue().isBlank()) {
				field.set(component, path);
			} else {
				field.set(component, resourceAnnotation.defaultValue());
			}
		} else if(Resources.hasLoader(path) && Thread.currentThread().getContextClassLoader().getResource(path) != null) {
			if(Resources.getOrLoad(path).getClass().isAssignableFrom(resourceAnnotation.type())) {
				field.set(component, path);
			}
		}
	}
}
