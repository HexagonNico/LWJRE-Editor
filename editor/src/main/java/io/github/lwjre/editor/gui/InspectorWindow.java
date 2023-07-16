package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import io.github.hexagonnico.vecmatlib.color.Color3f;
import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.hexagonnico.vecmatlib.vector.*;
import io.github.lwjre.editor.gui.inspector.*;
import io.github.lwjre.editor.models.EditorNode;
import io.github.lwjre.engine.annotations.EditorVariable;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.Model;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.resources.Shader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * The inspector gui.
 *
 * @author Nico
 */
public class InspectorWindow implements GuiComponent {

	/** Map of field renderers that contains supported field types */
	private static final HashMap<Class<?>, InspectorField> FIELDS = new HashMap<>();

	static {
		FIELDS.put(float.class, new FloatInspectorField());
		FIELDS.put(double.class, new DoubleInspectorField());
		FIELDS.put(int.class, new IntInspectorField());
		FIELDS.put(boolean.class, new CheckboxInspectorField());
		FIELDS.put(String.class, new TextInspectorField());
		FIELDS.put(Vec2f.class, new FloatVectorInspectorField(ImGui::inputFloat2, ImGui::dragFloat2, ImGui::sliderFloat2, array -> new Vec2f(array[0], array[1])));
		FIELDS.put(Vec3f.class, new FloatVectorInspectorField(ImGui::inputFloat3, ImGui::dragFloat3, ImGui::sliderFloat3, array -> new Vec3f(array[0], array[1], array[2])));
		FIELDS.put(Vec4f.class, new FloatVectorInspectorField(ImGui::inputFloat4, ImGui::dragFloat4, ImGui::sliderFloat4, array -> new Vec4f(array[0], array[1], array[2], array[3])));
		FIELDS.put(Vec2i.class, new IntVectorInspectorField(ImGui::inputInt2, ImGui::dragInt2, ImGui::sliderInt2, array -> new Vec2i(array[0], array[1])));
		FIELDS.put(Vec3i.class, new IntVectorInspectorField(ImGui::inputInt3, ImGui::dragInt3, ImGui::sliderInt3, array -> new Vec3i(array[0], array[1], array[2])));
		FIELDS.put(Vec4i.class, new IntVectorInspectorField(ImGui::inputInt4, ImGui::dragInt4, ImGui::sliderInt4, array -> new Vec4i(array[0], array[1], array[2], array[3])));
		FIELDS.put(Color3f.class, new ColorInspectorField());
		FIELDS.put(Color4f.class, new ColorInspectorField());
		FIELDS.put(Model.class, new ResourceInspectorField(Model::getOrLoad));
		FIELDS.put(Shader.class, new ResourceInspectorField(Shader::getOrLoad));
	}

	/** Current node */
	private EditorNode editorNode = null;

	@Override
	public void draw() {
		if(ImGui.begin("Inspector")) {
			if(this.editorNode != null) {
				this.renderFields(this.editorNode.resource(), this.editorNode.node(), this.editorNode.resource());
			}
		}
		ImGui.end();
	}

	/**
	 * Sets the current node.
	 * Pass null to clear the inspector.
	 *
	 * @param editorNode The node to inspect
	 */
	public void setNode(EditorNode editorNode) {
		this.editorNode = editorNode;
	}

	/**
	 * Renders the inspector of the given node.
	 * If the given node resource has an override, this function will call itself recursively to show its base's inspector as well.
	 *
	 * @param nodeResource The resource to show
	 * @param node The actual node
	 * @param base The base resource if the given resource has an override or the same resource if it doesn't
	 */
	private void renderFields(NodeResource nodeResource, Node node, NodeResource base) {
		try {
			if(nodeResource.override != null && !nodeResource.override.isEmpty()) {
				NodeResource override = NodeResource.getOrLoad(nodeResource.override);
				this.renderFields(override, node, base);
			} else if(nodeResource.type != null && !nodeResource.type.isEmpty()) {
				Class<?> nodeClass = Thread.currentThread().getContextClassLoader().loadClass(nodeResource.type);
				if(!nodeClass.equals(Node.class)) {
					this.renderFields(nodeClass, base, node);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders the inspector of the given class from the given node resource.
	 * This function calls itself recursively to render inspector of parent classes as well.
	 *
	 * @param fromClass Class to start from
	 * @param nodeResource The node resource
	 * @param node The actual node
	 */
	private void renderFields(Class<?> fromClass, NodeResource nodeResource, Node node) {
		ImGui.text(fromClass.getSimpleName());
		if(ImGui.beginTable("##" + fromClass, 2, ImGuiTableFlags.SizingStretchProp)) {
			ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed);
			ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthStretch);
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
						inputGui(field, node, nodeResource.properties);
					}
				}
			}
			ImGui.endTable();
		}
		ImGui.separator();
		fromClass = fromClass.getSuperclass();
		if(!fromClass.equals(Node.class)) {
			this.renderFields(fromClass, nodeResource, node);
		}
	}

	/**
	 * Renders an input gui.
	 *
	 * @param field The field to render
	 * @param object The object to which the field belongs
	 * @param values Values map
	 */
	private static void inputGui(Field field, Object object, HashMap<String, Object> values) {
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
