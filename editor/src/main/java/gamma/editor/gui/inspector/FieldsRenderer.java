package gamma.editor.gui.inspector;

import gamma.engine.annotations.EditorVariable;
import gamma.engine.resources.Resource;
import gamma.engine.scene.Component;
import imgui.ImGui;
import vecmatlib.color.Color3f;
import vecmatlib.color.Color4f;
import vecmatlib.vector.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class FieldsRenderer {

	private static final HashMap<Class<?>, IFieldGui> FIELD_GUIS = new HashMap<>();

	static {
		FIELD_GUIS.put(float.class, new RealFieldGui());
		FIELD_GUIS.put(double.class, new RealFieldGui());
		FIELD_GUIS.put(byte.class, new IntegerFieldGui(Byte.MIN_VALUE, Byte.MAX_VALUE));
		FIELD_GUIS.put(short.class, new IntegerFieldGui(Short.MIN_VALUE, Short.MAX_VALUE));
		FIELD_GUIS.put(int.class, new IntegerFieldGui(Integer.MIN_VALUE, Integer.MAX_VALUE));
		FIELD_GUIS.put(long.class, new IntegerFieldGui(Long.MIN_VALUE, Long.MAX_VALUE));
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
		FIELD_GUIS.put(Resource.class, new ResourceFieldGui());
	}

	public static void renderField(Component component, Field field) throws IllegalAccessException {
		if (field.isAnnotationPresent(EditorVariable.class)) {
			field.setAccessible(true);
			EditorVariable annotation = field.getAnnotation(EditorVariable.class);
			ImGui.text(" " + (annotation.value().isEmpty() ? field.getName() : annotation.value()));
			ImGui.sameLine();
			FIELD_GUIS.keySet().forEach(key -> {
				if(key.isAssignableFrom(field.getType())) {
					try {
						FIELD_GUIS.get(key).drawGui(component, field);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			});
			ImGui.sameLine();
			ImGui.newLine();
		}
	}
}
