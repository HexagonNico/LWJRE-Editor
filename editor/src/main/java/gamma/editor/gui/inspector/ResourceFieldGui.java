package gamma.editor.gui.inspector;

import gamma.engine.annotations.DefaultValueString;
import gamma.engine.annotations.EditorVariable;
import gamma.engine.resources.Resource;
import gamma.engine.resources.Resources;
import gamma.engine.scene.Component;
import imgui.ImGui;
import imgui.type.ImString;

import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class ResourceFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		Resource resource = (Resource) field.get(component);
		String str = Resources.pathOf(resource);
		if(field.isAnnotationPresent(DefaultValueString.class)) {
			DefaultValueString defaultValue = field.getAnnotation(DefaultValueString.class);
			if(str.equals(defaultValue.value()) && defaultValue.hide()) {
				ImString ptr = new ImString("", 256);
				if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
					setResource(component, field, ptr.get());
				}
			} else {
				ImString ptr = new ImString(str, 256);
				if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
					String val = ptr.get();;
					setResource(component, field, val.isEmpty() ? defaultValue.value() : val);
				}
			}
			// TODO: Add button to reset to default value
		} else {
			ImString ptr = new ImString(str, 256);
			if(ImGui.inputText("##" + component.getClass() + ":" + field.getName(), ptr)) {
				setResource(component, field, ptr.get());
			}
		}
		if(ImGui.beginDragDropTarget()) {
			Object payload = ImGui.acceptDragDropPayload("Path");
			if(payload instanceof Path path) {
				setResource(component, field, path.toString().substring(19));
			}
			ImGui.endDragDropTarget();
		}
	}

	private static void setResource(Component component, Field field, String value) throws IllegalAccessException {
		try {
			Resource newResource = Resources.getOrLoad(value);
			if(newResource.getClass().isAssignableFrom(field.getType())) {
				EditorVariable annotation = field.getAnnotation(EditorVariable.class);
				if(!annotation.setter().isEmpty()) {
					try {
						component.getClass().getDeclaredMethod(annotation.setter(), newResource.getClass()).invoke(component, newResource);
					} catch (InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				} else {
					field.set(component, newResource);
				}
			}
		} catch(NoSuchElementException | UncheckedIOException ignored) {}
	}
}
