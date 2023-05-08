package gamma.editor.gui.inspector;

import gamma.editor.controls.DragDropPayload;
import gamma.engine.annotations.DefaultResource;
import gamma.engine.resources.FileUtils;
import gamma.engine.resources.Resources;
import gamma.engine.tree.Node;
import imgui.ImGui;
import imgui.type.ImString;

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
			if(ImGui.acceptDragDropPayload("Path") instanceof DragDropPayload payload) {
				if(payload.object() instanceof Path path) {
					setResource(node, field, Path.of("demo/src/main/resources").relativize(path).toString(), values);
				}
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
