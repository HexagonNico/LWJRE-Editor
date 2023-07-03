package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import io.github.hexagonnico.vecmatlib.color.Color3f;
import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.hexagonnico.vecmatlib.vector.*;
import io.github.lwjre.editor.gui.inspector.*;
import io.github.lwjre.engine.annotations.EditorVariable;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.Model;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.resources.Shader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Class that represents the inspector gui window.
 * Needs a {@link NodeResource} and a {@link Node} to be set using {@link InspectorGui#setNode(Node, NodeResource)} to display their fields.
 * Shows the fields of a class annotated with {@link EditorVariable} as editable fields.
 *
 * @author Nico
 */
public class InspectorGui extends WindowGui {

	/** Map of field renderers that contains supported field types */
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

	/** The actual node */
	private Node node = null;
	/** The node resource */
	private NodeResource nodeResource = null;

	@Override
	public void init() {

	}

	@Override
	protected String title() {
		return "Inspector";
	}

	@Override
	protected void drawWindow() {
		if(this.nodeResource != null) {
			renderFields(this.nodeResource, this.node, this.nodeResource);
		}
	}

	/**
	 * Sets the node that the inspector should show.
	 * Can be set to null to not show any node.
	 *
	 * @param node The actual node
	 * @param resource The node resource
	 */
	public void setNode(Node node, NodeResource resource) {
		this.node = node;
		this.nodeResource = resource;
	}

	/**
	 * Renders the fields of the given node.
	 * If the given node resource has an override, this function will call itself recursively to show its base's fields as well.
	 *
	 * @param nodeResource The resource to show
	 * @param node The actual node
	 * @param base The base resource if the given resource has an override or the same resource if it doesn't
	 */
	private static void renderFields(NodeResource nodeResource, Node node, NodeResource base) {
		try {
			if(nodeResource.override != null && !nodeResource.override.isEmpty()) {
				NodeResource override = NodeResource.getOrLoad(nodeResource.override);
				renderFields(override, node, base);
			} else if(nodeResource.type != null && !nodeResource.type.isEmpty()) {
				Class<?> nodeClass = Thread.currentThread().getContextClassLoader().loadClass(nodeResource.type);
				if(!nodeClass.equals(Node.class)) {
					renderFields(nodeClass, base, node);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders the fields of the given class from the given node resource.
	 * This function calls itself recursively to render fields of parent classes as well.
	 *
	 * @param fromClass Class to start from
	 * @param nodeResource The node resource
	 * @param node The actual node
	 */
	private static void renderFields(Class<?> fromClass, NodeResource nodeResource, Node node) {
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
								FIELDS.get(type).inputGui(field, node, nodeResource.properties);
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
			renderFields(fromClass, nodeResource, node);
		}
	}

	@Override
	public void cleanUp() {

	}
}
