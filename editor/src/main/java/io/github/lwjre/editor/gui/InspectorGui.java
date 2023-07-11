package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import io.github.lwjre.editor.gui.fields.FieldGuis;
import io.github.lwjre.engine.annotations.EditorVariable;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.NodeResource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Class that represents the fields gui window.
 * Needs a {@link NodeResource} and a {@link Node} to be set using {@link InspectorGui#setNode(Node, NodeResource)} to display their fields.
 * Shows the fields of a class annotated with {@link EditorVariable} as editable fields.
 *
 * @author Nico
 */
public class InspectorGui extends WindowGui {

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
	 * Sets the node that the fields should show.
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
						FieldGuis.inputGui(field, node, nodeResource.properties);
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
