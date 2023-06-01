package gamma.editor.gui;

import gamma.editor.controls.EditorScene;
import gamma.editor.gui.inspector.*;
import gamma.engine.annotations.EditorVariable;
import gamma.engine.rendering.Model;
import gamma.engine.rendering.Shader;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import vecmatlib.color.Color3f;
import vecmatlib.color.Color4f;
import vecmatlib.vector.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class InspectorGui extends WindowGui {

	private static final HashMap<Class<?>, InspectorField> FIELDS = new HashMap<>();

	static {
		FIELDS.put(float.class, new FloatField());
		FIELDS.put(double.class, new DoubleField());
		FIELDS.put(int.class, new IntField());
		FIELDS.put(boolean.class, new CheckboxField());
		FIELDS.put(String.class, new TextField());
		FIELDS.put(Vec2f.class, new FloatVectorField(ImGui::inputFloat2, ImGui::dragFloat2, ImGui::sliderFloat2, array -> new Vec2f(array[0], array[1])));
		FIELDS.put(Vec3f.class, new FloatVectorField(ImGui::inputFloat3, ImGui::dragFloat3, ImGui::sliderFloat3, array -> new Vec3f(array[0], array[1], array[2])));
		FIELDS.put(Vec4f.class, new FloatVectorField(ImGui::inputFloat4, ImGui::dragFloat4, ImGui::sliderFloat4, array -> new Vec4f(array[0], array[1], array[2], array[3])));
		FIELDS.put(Vec2i.class, new IntVectorField(ImGui::inputInt2, ImGui::dragInt2, ImGui::sliderInt2, array -> new Vec2i(array[0], array[1])));
		FIELDS.put(Vec3i.class, new IntVectorField(ImGui::inputInt3, ImGui::dragInt3, ImGui::sliderInt3, array -> new Vec3i(array[0], array[1], array[2])));
		FIELDS.put(Vec4i.class, new IntVectorField(ImGui::inputInt4, ImGui::dragInt4, ImGui::sliderInt4, array -> new Vec4i(array[0], array[1], array[2], array[3])));
		FIELDS.put(Color3f.class, new ColorField());
		FIELDS.put(Color4f.class, new ColorField());
		FIELDS.put(Model.class, new ResourceField(Model::getOrLoad));
		FIELDS.put(Shader.class, new ResourceField(Shader::getOrLoad));
	}

	private NodeResource nodeResource;

	@Override
	protected String title() {
		return "Inspector";
	}

	@Override
	protected void drawWindow() {
		if(this.nodeResource != null) {
			renderFields(this.nodeResource, this.nodeResource);
		}
	}

	public void setNode(NodeResource nodeResource) {
		this.nodeResource = nodeResource;
	}

	private static void renderFields(NodeResource nodeResource, NodeResource base) {
		try {
			if(nodeResource.override != null && !nodeResource.override.isEmpty()) {
				NodeResource override = NodeResource.getOrLoad(nodeResource.override);
				renderFields(override, base);
			} else if(nodeResource.type != null && !nodeResource.type.isEmpty()) {
				Class<?> nodeClass = Thread.currentThread().getContextClassLoader().loadClass(nodeResource.type);
				if(!nodeClass.equals(Node.class)) {
					renderFields(nodeClass, base);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void renderFields(Class<?> fromClass, NodeResource nodeResource) {
		ImGui.text(fromClass.getSimpleName());
		if(ImGui.beginTable("##" + fromClass, 2, ImGuiTableFlags.SizingStretchProp)) {
			ImGui.tableSetupColumn("0", ImGuiTableColumnFlags.WidthFixed);
			ImGui.tableSetupColumn("1", ImGuiTableColumnFlags.WidthStretch);
			for(Field field : fromClass.getDeclaredFields()) {
				field.setAccessible(true);
				int modifiers = field.getModifiers();
				if(!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
					EditorVariable editorVariable = field.getAnnotation(EditorVariable.class);
					if(editorVariable != null) {
						ImGui.tableNextColumn();
						String label = editorVariable.name().isEmpty() ? field.getName() : editorVariable.name();
						ImGui.textColored(0.75f, 0.75f, 0.75f, 1.0f, label);
						ImGui.tableNextColumn();
						FIELDS.keySet().stream().filter(type -> type.isAssignableFrom(field.getType())).findFirst().ifPresent(type -> {
							try {
								ImGui.setNextItemWidth(-1);
								FIELDS.get(type).inputGui(field, EditorScene.getNode(nodeResource), nodeResource.properties);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						});
					}
				}
			}
			ImGui.endTable();
		}
		ImGui.separator();
		fromClass = fromClass.getSuperclass();
		if(!fromClass.equals(Node.class)) {
			renderFields(fromClass, nodeResource);
		}
	}
}
