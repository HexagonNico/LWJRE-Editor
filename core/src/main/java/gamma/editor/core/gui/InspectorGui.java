package gamma.editor.core.gui;

import gamma.engine.core.annotations.EditorRange;
import gamma.engine.core.annotations.EditorVariable;
import gamma.engine.core.resources.Resource;
import gamma.engine.core.resources.Resources;
import gamma.engine.core.scene.Component;
import gamma.engine.core.scene.Entity;
import gamma.engine.core.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;
import vecmatlib.vector.Vec2f;
import vecmatlib.vector.Vec2i;
import vecmatlib.vector.Vec3f;
import vecmatlib.vector.Vec4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class InspectorGui implements IEditorGui {

	private Entity entity;

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 5.0f, ImGuiCond.Once);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.Once);
		ImGui.begin("Inspector");
		if(this.entity != null) {
			this.entity.getComponents().forEach(component -> {
				ImGui.text(component.getClass().getSimpleName());
				for(Field field : component.getClass().getDeclaredFields()) {
					if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
						try {
							renderField(component, field);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				ImGui.separator();
			});
		}
		ImGui.end();
	}

	private static void renderField(Component component, Field field) throws IllegalAccessException {
		if(field.isAnnotationPresent(EditorVariable.class)) {
			field.setAccessible(true);
			EditorVariable annotation = field.getAnnotation(EditorVariable.class);
			ImGui.text(" " + (annotation.value().isEmpty() ? field.getName() : annotation.value()));
			ImGui.sameLine();
			if(field.getType().equals(float.class) || field.getType().equals(double.class)) {
				renderRealField(component, field);
			} else if(field.getType().equals(byte.class)) {
				renderIntegerField(component, field, Byte.MIN_VALUE, Byte.MAX_VALUE);
			} else if(field.getType().equals(short.class)) {
				renderIntegerField(component, field, Short.MIN_VALUE, Short.MAX_VALUE);
			} else if(field.getType().equals(int.class)) {
				renderIntegerField(component, field, Integer.MIN_VALUE, Integer.MAX_VALUE);
			} else if(field.getType().equals(long.class)) {
				renderIntegerField(component, field, Long.MIN_VALUE, Long.MAX_VALUE);
			} else if(field.getType().equals(boolean.class)) {
				renderBooleanField(component, field);
			} else if(field.getType().equals(String.class)) {
				renderTextField(component, field);
			} else if(field.getType().equals(Vec2f.class)) {
				renderVecField2(component, field);
			} else if(field.getType().equals(Vec3f.class)) {
				renderVecField3(component, field);
			} else if(field.getType().equals(Vec4f.class)) {
				renderVecField4(component, field);
			} else if(Resource.class.isAssignableFrom(field.getType())) {
				renderResource(component, field);
			} else {
				ImGui.newLine();
			}
		}
	}

	private static void renderRealField(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		float[] ptr = {(float) field.getDouble(component)};
		float step = range != null ? range.step() : 0.001f;
		float min = range != null ? range.min() : Float.NEGATIVE_INFINITY;
		float max = range != null ? range.max() : Float.POSITIVE_INFINITY;
		if(ImGui.dragFloat("##" + field.getName(), ptr, step, min, max)) {
			field.set(component, ptr[0]);
		}
	}

	private static void renderIntegerField(Component component, Field field, float defaultMin, float defaultMax) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		int[] ptr = {(int) field.getLong(component)};
		float step = range != null ? range.step() : 1.0f;
		float min = range != null ? range.min() : defaultMin;
		float max = range != null ? range.max() : defaultMax;
		if(ImGui.dragInt("##" + field.getName(), ptr, step, min, max)) {
			field.set(component, ptr[0]);
		}
	}

	private static void renderBooleanField(Component component, Field field) throws IllegalAccessException {
		boolean current = field.getBoolean(component);
		if(ImGui.checkbox("##" + field.getName(), current)) {
			field.set(component, !current);
		}
	}

	private static void renderTextField(Component component, Field field) throws IllegalAccessException {
		ImString ptr = new ImString((String) field.get(component));
		if(ImGui.inputText("##" + field.getName(), ptr)) {
			field.set(component, ptr.get());
		}
	}

	private static void renderVecField2(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		Vec2f vector = (Vec2f) field.get(component);
		float[] ptr = {vector.x(), vector.y()};
		float step = range != null ? range.step() : 0.001f;
		float min = range != null ? range.min() : Float.NEGATIVE_INFINITY;
		float max = range != null ? range.max() : Float.POSITIVE_INFINITY;
		if(ImGui.dragFloat2("##" + field.getName(), ptr, step, min, max)) {
			field.set(component, new Vec2f(ptr[0], ptr[1]));
		}
	}

	private static void renderVecField3(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		Vec3f vector = (Vec3f) field.get(component);
		float[] ptr = {vector.x(), vector.y(), vector.z()};
		float step = range != null ? range.step() : 0.001f;
		float min = range != null ? range.min() : Float.NEGATIVE_INFINITY;
		float max = range != null ? range.max() : Float.POSITIVE_INFINITY;
		if(ImGui.dragFloat3("##" + field.getName(), ptr, step, min, max)) {
			field.set(component, new Vec3f(ptr[0], ptr[1], ptr[2]));
		}
	}

	private static void renderVecField4(Component component, Field field) throws IllegalAccessException {
		EditorRange range = field.getAnnotation(EditorRange.class);
		Vec4f vector = (Vec4f) field.get(component);
		float[] ptr = {vector.x(), vector.y(), vector.z(), vector.w()};
		float step = range != null ? range.step() : 0.001f;
		float min = range != null ? range.min() : Float.NEGATIVE_INFINITY;
		float max = range != null ? range.max() : Float.POSITIVE_INFINITY;
		if(ImGui.dragFloat4("##" + field.getName(), ptr, step, min, max)) {
			field.set(component, new Vec4f(ptr[0], ptr[1], ptr[2], ptr[3]));
		}
	}

	private static void renderResource(Component component, Field field) throws IllegalAccessException {
		Resource resource = (Resource) field.get(component);
		ImString ptr = new ImString(Resources.pathOf(resource), 256);
		if(ImGui.inputText("##" + field.getName(), ptr)) {
			setResource(component, field, ptr.get());
		}
		if(ImGui.beginDragDropTarget()) {
			Object payload = ImGui.acceptDragDropPayload("Path");
			if(payload instanceof Path path) {
				setResource(component, field, path.toString().substring(18));
			}
			ImGui.endDragDropTarget();
		}
	}

	private static void setResource(Component component, Field field, String value) throws IllegalAccessException {
		if(value.contains(".") && Files.exists(Path.of("src/main/resources" + value))) {
			try {
				Resource newResource = Resources.getOrLoad(value);
				if(newResource.getClass().isAssignableFrom(field.getType()))
					field.set(component, newResource);
			} catch(NoSuchElementException e) {
				e.printStackTrace();
			}
		}
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
