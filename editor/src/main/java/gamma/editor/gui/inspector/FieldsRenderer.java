package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorVariable;
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
		FIELD_GUIS.put(float.class, new RealFieldGui());
		FIELD_GUIS.put(double.class, new RealFieldGui());
		FIELD_GUIS.put(byte.class, new IntegerFieldGui());
		FIELD_GUIS.put(short.class, new IntegerFieldGui());
		FIELD_GUIS.put(int.class, new IntegerFieldGui());
		FIELD_GUIS.put(long.class, new IntegerFieldGui());
		FIELD_GUIS.put(boolean.class, new CheckboxFieldGui());
		FIELD_GUIS.put(String.class, new TextFieldGui());
		FIELD_GUIS.put(Vec2f.class, new VectorFieldGui());
		FIELD_GUIS.put(Vec3f.class, new VectorFieldGui());
		FIELD_GUIS.put(Vec4f.class, new VectorFieldGui());
		FIELD_GUIS.put(Vec2i.class, new VectorFieldGui());
		FIELD_GUIS.put(Vec3i.class, new VectorFieldGui());
		FIELD_GUIS.put(Vec4i.class, new VectorFieldGui());
		FIELD_GUIS.put(Color3f.class, new ColorFieldGui());
		FIELD_GUIS.put(Color4f.class, new ColorFieldGui());
	}

	/**
	 * Renders all the fields of the given component.
	 *
	 * @param component The component to render
	 */
	public static boolean renderFields(Component component) {
		return renderFields(component, component.getClass());
	}

	/**
	 * Renders all the fields of the given component.
	 *
	 * @param component The component to render
	 * @param fromClass The class to get the fields from
	 */
	private static boolean renderFields(Component component, Class<?> fromClass) {
		boolean result = false;
		if(!fromClass.getSuperclass().equals(Component.class)) {
			result = renderFields(component, fromClass.getSuperclass());
		}
		for(Field field : fromClass.getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) try {
				if(FieldsRenderer.renderField(component, field)) {
					result = true;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Renders the given field.
	 *
	 * @param component The component to which the field belongs to
	 * @param field The field to render
	 * @throws IllegalAccessException If the field could not be accessed.
	 */
	public static boolean renderField(Component component, Field field) throws IllegalAccessException {
		if (field.isAnnotationPresent(EditorVariable.class)) {
			field.setAccessible(true);
			EditorVariable annotation = field.getAnnotation(EditorVariable.class);
			ImGui.text(" " + (annotation.name().isEmpty() ? field.getName() : annotation.name()));
			ImGui.sameLine();
			Class<?> type = field.getType();
			if(FIELD_GUIS.containsKey(type)) {
				return FIELD_GUIS.get(type).drawGui(component, field);
			} else {
				ImGui.newLine();
			}
		}
		return false;
	}
}
