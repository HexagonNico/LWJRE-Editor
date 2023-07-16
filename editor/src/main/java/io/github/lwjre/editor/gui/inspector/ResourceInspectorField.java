package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImString;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.annotations.DefaultResource;
import io.github.lwjre.engine.resources.Resources;
import io.github.lwjre.engine.utils.FileUtils;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Function;

public class ResourceInspectorField implements InspectorField {

	private final Function<String, Object> getOrLoad;

	public ResourceInspectorField(Function<String, Object> getOrLoad) {
		this.getOrLoad = getOrLoad;
	}

	@Override
	public void inputGui(Field field, Object object, HashMap<String, Object> values) throws IllegalAccessException {
		String value = Resources.pathOf(field.get(object));
		DefaultResource defaultResource = field.getAnnotation(DefaultResource.class);
		if(defaultResource != null && defaultResource.path().equals(value)) {
			value = "";
		}
		ImString ptr = new ImString(value, 256);
		if(ImGui.inputText("##" + object.getClass() + ":" + field.getName(), ptr)) {
			setResource(object, field, ptr.get(), values);
		}
		if(ImGui.beginDragDropTarget()) {
			Object payload = ImGui.acceptDragDropPayload("Path");
			if(payload instanceof Path path) {
				setResource(object, field, ProjectPath.resourcesFolder().relativize(path).toString(), values);
			}
			ImGui.endDragDropTarget();
		}
		if(!value.isEmpty()) {
			ImGui.sameLine();
			if(ImGui.smallButton("X##" + object.getClass() + ":" + field.getName())) {
				setResource(object, field, "", values);
			}
		}
	}

	private void setResource(Object object, Field field, String path, HashMap<String, Object> values) throws IllegalAccessException {
		if(path.isEmpty() || (FileUtils.resourceExists(path) && Resources.hasLoader(path))) {
			Object resource = this.getOrLoad.apply(path);
			if(resource.getClass().isAssignableFrom(field.getType())) {
				field.set(object, resource);
				values.put(field.getName(), resource);
			}
		}
	}
}
