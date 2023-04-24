package gamma.editor.gui.inspector;

import gamma.editor.EditorApplication;
import gamma.engine.annotations.DefaultResource;
import gamma.engine.resources.FileUtils;
import gamma.engine.resources.Resources;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Function;

public class ResourceFieldGui implements IFieldGui {

	private final Function<String, Object> getOrLoad;

	public ResourceFieldGui(Function<String, Object> getOrLoad) {
		this.getOrLoad = getOrLoad;
	}

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		String value = Resources.pathOf(field.get(component));
		DefaultResource defaultResource = field.getAnnotation(DefaultResource.class);
		if(defaultResource != null && defaultResource.path().equals(value)) {
			value = "";
		}
		ImString ptr = new ImString(value, 256);
		if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
			setResource(component, field, ptr.get(), values);
		}
		if(ImGui.beginDragDropTarget()) {
			if(ImGui.acceptDragDropPayload("Path") instanceof Path path) {
				setResource(component, field, path.toString().substring(EditorApplication.currentPath().length() + 20), values);
			}
			ImGui.endDragDropTarget();
		}
		if(!value.isEmpty()) {
			ImGui.sameLine();
			if(ImGui.smallButton("X")) {
				setResource(component, field, "", values);
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
	private void setResource(Component component, Field field, String path, HashMap<String, Object> values) throws IllegalAccessException {
		if(path.isEmpty() || (FileUtils.resourceExists(path) && Resources.hasLoader(path)) || path.isBlank()) {
			Object resource = this.getOrLoad.apply(path);
			if(resource.getClass().isAssignableFrom(field.getType())) {
				field.set(component, resource);
				values.put(field.getName(), resource);
			}
		}
	}
}
