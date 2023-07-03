package io.github.lwjre.editor.gui.inspector;

import imgui.ImGui;
import imgui.type.ImString;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.annotations.DefaultResource;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.Resources;
import io.github.lwjre.engine.utils.FileUtils;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Function;

public class ResourceField implements InspectorField {

	private final Function<String, Object> getOrLoad;

	public ResourceField(Function<String, Object> getOrLoad) {
		this.getOrLoad = getOrLoad;
	}

	@Override
	public void inputGui(Field field, Node node, HashMap<String, Object> values) throws IllegalAccessException {
		String value = Resources.pathOf(field.get(node));
		DefaultResource defaultResource = field.getAnnotation(DefaultResource.class);
		if(defaultResource != null && defaultResource.path().equals(value)) {
			value = "";
		}
		ImString ptr = new ImString(value, 256);
		if(ImGui.inputText("##" + node.getClass() + ":" + field.getName(), ptr)) {
			setResource(node, field, ptr.get(), values);
		}
		if(ImGui.beginDragDropTarget()) {
			Object payload = ImGui.acceptDragDropPayload("Path");
			if(payload instanceof Path path) {
				setResource(node, field, ProjectPath.resourcesFolder().relativize(path).toString(), values);
			}
			ImGui.endDragDropTarget();
		}
		if(!value.isEmpty()) {
			ImGui.sameLine();
			if(ImGui.smallButton("X##" + node.getClass() + ":" + field.getName())) {
				setResource(node, field, "", values);
			}
		}
	}

	private void setResource(Node node, Field field, String path, HashMap<String, Object> values) throws IllegalAccessException {
		if(path.isEmpty() || (FileUtils.resourceExists(path) && Resources.hasLoader(path))) {
			Object resource = this.getOrLoad.apply(path);
			if(resource.getClass().isAssignableFrom(field.getType())) {
				field.set(node, resource);
				values.put(field.getName(), resource);
			}
		}
	}
}
