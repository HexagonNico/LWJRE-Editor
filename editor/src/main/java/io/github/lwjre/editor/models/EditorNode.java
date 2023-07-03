package io.github.lwjre.editor.models;

import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.YamlParser;

import java.nio.file.Path;

/**
 * Node used in the {@link io.github.lwjre.editor.gui.SceneTreeGui}.
 * Represents a {@link Node} and a {@link NodeResource} pair with their label and parent.
 *
 * @param node The actual node
 * @param resource The node resource
 * @param label The node's lable
 * @param parent The node resource's parent
 */
public record EditorNode(Node node, NodeResource resource, String label, NodeResource parent) {

	/**
	 * Instantiates a child node to this node.
	 *
	 * @param type Type of child to instantiate
	 */
	public void addChild(Class<?> type) {
		NodeResource resource = new NodeResource(type.getName());
		String key = this.findUnusedKey(type.getSimpleName());
		this.resource().children.put(key, resource);
		this.node().addChild(key, resource.instantiate());
	}

	/**
	 * Iterates through the resource's children looking for a suitable key to use.
	 * Appends a number at the end of the given starting key to find an unused one.
	 *
	 * @param from The key to start from
	 * @return The resulting key
	 */
	private String findUnusedKey(String from) {
		if(this.resource().children.containsKey(from)) {
			int index = 2;
			from = from.replaceAll("\\d+$", "");
			while(this.resource().children.containsKey(from + index)) {
				index++;
			}
			return from + index;
		} else {
			return from;
		}
	}

	/**
	 * Instantiates a child scene to this node.
	 *
	 * @param filePath Path to the child scene
	 */
	public void addChild(Path filePath) {
		String fileName = filePath.getFileName().toString();
		if(fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
			String resourcePath = filePath.toString();
			Object resource = YamlParser.parseResource(resourcePath);
			if(resource instanceof NodeResource loadedResource) {
				NodeResource nodeResource = new NodeResource(loadedResource.type);
				nodeResource.override = resourcePath;
				String key = this.findUnusedKey(fileName.replaceAll("(\\.yaml|\\.yml)", ""));
				this.node().addChild(key, nodeResource.instantiate());
				this.resource().children.put(key, nodeResource);
			}
		}
	}

	/**
	 * Changes this node's parent to the given one.
	 *
	 * @param parent The new parent
	 */
	public void reparent(EditorNode parent) {
		if(!isDescendant(this.resource(), parent.resource())) {
			if(this.parent() != null)
				this.parent().children.remove(this.label());
			if(this.node().getParent() != null)
				this.node().getParent().removeChild(this.label());
			String key = parent.findUnusedKey(this.label());
			parent.resource().children.put(key, this.resource());
			parent.node().addChild(key, this.node());
		}
	}

	/**
	 * Checks if the given descendant is a child or grandchild of the given ancestor.
	 *
	 * @param ancestor The ancestor
	 * @param descendant The descendant
	 * @return True if the given ancestor is an ancestor of the given descendant, otherwise false
	 */
	private static boolean isDescendant(NodeResource ancestor, NodeResource descendant) {
		if(ancestor.children.containsValue(descendant)) {
			return true;
		} else {
			return ancestor.children.values().stream().anyMatch(next -> isDescendant(next, descendant));
		}
	}

	/**
	 * Renames this node.
	 *
	 * @param name The new name
	 */
	public void rename(String name) {
		Node parent = this.node().getParent();
		parent.removeChild(this.label());
		parent.addChild(name, this.node());
		this.parent().children.remove(this.label());
		this.parent().children.put(name, this.resource());
	}

	/**
	 * Deletes this node.
	 */
	public void delete() {
		this.node().getParent().removeChild(this.label());
		this.parent().children.remove(this.label());
	}
}
