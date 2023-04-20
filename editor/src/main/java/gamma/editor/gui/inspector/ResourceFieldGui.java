package gamma.editor.gui.inspector;

import gamma.editor.EditorApplication;
import gamma.engine.annotations.EditorResource;
import gamma.engine.resources.Resources;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class ResourceFieldGui implements IFieldGui {

	@Override
	public boolean drawGui(Component component, Field field) throws IllegalAccessException {
		EditorResource resourceAnnotation = field.getAnnotation(EditorResource.class);
		String value = Resources.pathOf(field.get(component));
		ImString ptr = new ImString(value.equals(resourceAnnotation.defaultValue()) ? "" : value, 256);
		if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
			setResource(component, field, ptr.get());
			return true;
		}
		if(ImGui.beginDragDropTarget()) {
			if(ImGui.acceptDragDropPayload("Path") instanceof Path path) {
				setResource(component, field, path.toString().substring(EditorApplication.currentPath().length() + 20));
				return true;
			}
			ImGui.endDragDropTarget();
		}
		return false;
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
				setResourceValue(component, field, path);
			} else {
				setResourceValue(component, field, resourceAnnotation.defaultValue());
			}
		} else if(Resources.hasLoader(path) && Thread.currentThread().getContextClassLoader().getResource(path) != null) {
			setResourceValue(component, field, path);
		}
	}

	private static void setResourceValue(Component component, Field field, String path) throws IllegalAccessException {
		Object resource = Resources.getOrLoad(path);
		if(resource != null && resource.getClass().isAssignableFrom(field.getType())) {
			field.set(component, resource);
		}
		// TODO: Resource == null ?
	}
}
