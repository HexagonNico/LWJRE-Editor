package io.github.lwjre.editor.gui.fields;

import imgui.ImGui;
import io.github.hexagonnico.vecmatlib.color.Color3f;
import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.hexagonnico.vecmatlib.vector.*;
import io.github.lwjre.engine.resources.Model;
import io.github.lwjre.engine.resources.Shader;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FieldGuis {

	/** Map of field renderers that contains supported field types */
	private static final HashMap<Class<?>, FieldGui> FIELDS = new HashMap<>();

	static {
		FIELDS.put(float.class, new FloatFieldGui());
		FIELDS.put(double.class, new DoubleFieldGui());
		FIELDS.put(int.class, new IntFieldGui());
		FIELDS.put(boolean.class, new CheckboxFieldGui());
		FIELDS.put(String.class, new TextFieldGui());
		FIELDS.put(Vec2f.class, new FloatVectorFieldGui(ImGui::inputFloat2, ImGui::dragFloat2, ImGui::sliderFloat2, array -> new Vec2f(array[0], array[1])));
		FIELDS.put(Vec3f.class, new FloatVectorFieldGui(ImGui::inputFloat3, ImGui::dragFloat3, ImGui::sliderFloat3, array -> new Vec3f(array[0], array[1], array[2])));
		FIELDS.put(Vec4f.class, new FloatVectorFieldGui(ImGui::inputFloat4, ImGui::dragFloat4, ImGui::sliderFloat4, array -> new Vec4f(array[0], array[1], array[2], array[3])));
		FIELDS.put(Vec2i.class, new IntVectorFieldGui(ImGui::inputInt2, ImGui::dragInt2, ImGui::sliderInt2, array -> new Vec2i(array[0], array[1])));
		FIELDS.put(Vec3i.class, new IntVectorFieldGui(ImGui::inputInt3, ImGui::dragInt3, ImGui::sliderInt3, array -> new Vec3i(array[0], array[1], array[2])));
		FIELDS.put(Vec4i.class, new IntVectorFieldGui(ImGui::inputInt4, ImGui::dragInt4, ImGui::sliderInt4, array -> new Vec4i(array[0], array[1], array[2], array[3])));
		FIELDS.put(Color3f.class, new ColorFieldGui());
		FIELDS.put(Color4f.class, new ColorFieldGui());
		FIELDS.put(Model.class, new ResourceFieldGui(Model::getOrLoad));
		FIELDS.put(Shader.class, new ResourceFieldGui(Shader::getOrLoad));
	}

	public static void inputGui(Field field, Object object, HashMap<String, Object> values) {
		FIELDS.keySet().stream().filter(type -> type.isAssignableFrom(field.getType())).findFirst().ifPresent(type -> {
			try {
				ImGui.setNextItemWidth(-1);
				FIELDS.get(type).inputGui(field, object, values);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
}
