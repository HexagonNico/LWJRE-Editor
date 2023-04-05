package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorRange;
import gamma.engine.scene.Component;
import imgui.ImGui;
import vecmatlib.vector.*;

import java.lang.reflect.Field;

public class VectorFieldGui implements IFieldGui {
	
	@Override
	public void drawGui(Component component, Field field) throws IllegalAccessException {
		if(field.isAnnotationPresent(EditorRange.class)) {
			EditorRange range = field.getAnnotation(EditorRange.class);
			float step = range != null ? range.step() : 0.001f;
			float min = range != null ? range.min() : Float.NEGATIVE_INFINITY;
			float max = range != null ? range.max() : Float.POSITIVE_INFINITY;
			this.dragGui(component, field, step, min, max);
		} else {
			this.inputGui(component, field);
		}
	}

	private void dragGui(Component component, Field field, float step, float min, float max) throws IllegalAccessException {
		Object obj = field.get(component);
		if(obj instanceof Vec2f vector) {
			float[] ptr = {vector.x(), vector.y()};
			if(ImGui.dragFloat2("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec2f(ptr[0], ptr[1]));
			}
		} else if(obj instanceof Vec3f vector) {
			float[] ptr = {vector.x(), vector.y(), vector.z()};
			if(ImGui.dragFloat3("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec3f(ptr[0], ptr[1], ptr[2]));
			}
		} else if(obj instanceof Vec4f vector) {
			float[] ptr = {vector.x(), vector.y(), vector.z(), vector.w()};
			if(ImGui.dragFloat4("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec4f(ptr[0], ptr[1], ptr[2], ptr[3]));
			}
		} else if(obj instanceof Vec2i vector) {
			int[] ptr = {vector.x(), vector.y()};
			if(ImGui.dragInt2("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec2i(ptr[0], ptr[1]));
			}
		} else if(obj instanceof Vec3i vector) {
			int[] ptr = {vector.x(), vector.y(), vector.z()};
			if(ImGui.dragInt3("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec3i(ptr[0], ptr[1], ptr[2]));
			}
		} else if(obj instanceof Vec4i vector) {
			int[] ptr = {vector.x(), vector.y(), vector.z(), vector.w()};
			if(ImGui.dragInt4("##" + component.getClass() + ":" + field.getName(), ptr, step, min, max)) {
				field.set(component, new Vec4i(ptr[0], ptr[1], ptr[2], ptr[3]));
			}
		}
	}

	private void inputGui(Component component, Field field) throws IllegalAccessException {
		Object obj = field.get(component);
		if(obj instanceof Vec2f vector) {
			float[] ptr = {vector.x(), vector.y()};
			if(ImGui.inputFloat2("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec2f(ptr[0], ptr[1]));
			}
		} else if(obj instanceof Vec3f vector) {
			float[] ptr = {vector.x(), vector.y(), vector.z()};
			if(ImGui.inputFloat3("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec3f(ptr[0], ptr[1], ptr[2]));
			}
		} else if(obj instanceof Vec4f vector) {
			float[] ptr = {vector.x(), vector.y(), vector.z(), vector.w()};
			if(ImGui.inputFloat4("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec4f(ptr[0], ptr[1], ptr[2], ptr[3]));
			}
		} else if(obj instanceof Vec2i vector) {
			int[] ptr = {vector.x(), vector.y()};
			if(ImGui.inputInt2("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec2i(ptr[0], ptr[1]));
			}
		} else if(obj instanceof Vec3i vector) {
			int[] ptr = {vector.x(), vector.y(), vector.z()};
			if(ImGui.inputInt3("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec3i(ptr[0], ptr[1], ptr[2]));
			}
		} else if(obj instanceof Vec4i vector) {
			int[] ptr = {vector.x(), vector.y(), vector.z(), vector.w()};
			if(ImGui.inputInt4("##" + component.getClass() + ":" + field.getName(), ptr)) {
				field.set(component, new Vec4i(ptr[0], ptr[1], ptr[2], ptr[3]));
			}
		}
	}
}
