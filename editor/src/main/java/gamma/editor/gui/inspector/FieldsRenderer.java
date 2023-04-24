package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorVariable;
import gamma.engine.rendering.Model;
import gamma.engine.rendering.Shader;
import gamma.engine.scene.Component;
import imgui.ImGui;
import vecmatlib.color.Color3f;
import vecmatlib.color.Color4f;
import vecmatlib.vector.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Static class that handles the rendering of different kinds of fields.
 *
 * @author Nico
 */
public final class FieldsRenderer {

	/** Maps a class to an {@link IFieldGui} to render a field */
	private static final HashMap<Class<?>, IFieldGui> FIELD_GUIS = new HashMap<>();

	static {
		FIELD_GUIS.put(float.class, new FloatFieldGui());
		FIELD_GUIS.put(double.class, new DoubleFieldGui());
		FIELD_GUIS.put(int.class, new IntFieldGui());
		FIELD_GUIS.put(boolean.class, new CheckboxFieldGui());
		FIELD_GUIS.put(String.class, new TextFieldGui());
		FIELD_GUIS.put(Vec2f.class, new FloatVectorFieldGui(ImGui::inputFloat2, ImGui::dragFloat2, ImGui::sliderFloat2, array -> new Vec2f(array[0], array[1])));
		FIELD_GUIS.put(Vec3f.class, new FloatVectorFieldGui(ImGui::inputFloat3, ImGui::dragFloat3, ImGui::sliderFloat3, array -> new Vec3f(array[0], array[1], array[2])));
		FIELD_GUIS.put(Vec4f.class, new FloatVectorFieldGui(ImGui::inputFloat4, ImGui::dragFloat4, ImGui::sliderFloat4, array -> new Vec4f(array[0], array[1], array[2], array[3])));
		FIELD_GUIS.put(Vec2i.class, new IntVectorFieldGui(ImGui::inputInt2, ImGui::dragInt2, ImGui::sliderInt2, array -> new Vec2i(array[0], array[1])));
		FIELD_GUIS.put(Vec3i.class, new IntVectorFieldGui(ImGui::inputInt3, ImGui::dragInt3, ImGui::sliderInt3, array -> new Vec3i(array[0], array[1], array[2])));
		FIELD_GUIS.put(Vec4i.class, new IntVectorFieldGui(ImGui::inputInt4, ImGui::dragInt4, ImGui::sliderInt4, array -> new Vec4i(array[0], array[1], array[2], array[3])));
		FIELD_GUIS.put(Color3f.class, new ColorFieldGui());
		FIELD_GUIS.put(Color4f.class, new ColorFieldGui());
		FIELD_GUIS.put(Model.class, new ResourceFieldGui(Model::getOrLoad));
		FIELD_GUIS.put(Shader.class, new ResourceFieldGui(Shader::getOrLoad));
	}

	public static boolean renderFields(Component component, HashMap<String, Object> values) {
		return renderFields(component, values, component.getClass());
	}

	private static boolean renderFields(Component component, HashMap<String, Object> values, Class<?> fromClass) {
		boolean result = false;
		if(!fromClass.getSuperclass().equals(Component.class)) {
			result = renderFields(component, values, fromClass.getSuperclass());
		}
		for(Field field : fromClass.getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) try {
				if(field.isAnnotationPresent(EditorVariable.class)) {
					field.setAccessible(true);
					EditorVariable annotation = field.getAnnotation(EditorVariable.class);
					ImGui.text(" " + (annotation.name().isEmpty() ? field.getName() : annotation.name()));
					ImGui.sameLine();
					Class<?> type = field.getType();
					if(FIELD_GUIS.containsKey(type)) {
						FIELD_GUIS.get(type).drawGui(component, field, values);
					} else {
						ImGui.newLine();
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
