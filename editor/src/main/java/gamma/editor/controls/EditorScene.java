package gamma.editor.controls;

import gamma.editor.ProjectPath;
import gamma.engine.resources.YamlLoader;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.Reflection;
import gamma.engine.utils.ReflectionException;
import gamma.engine.utils.YamlSerializer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

public class EditorScene {

	private static Path currentPath = null;

	private static NodeResource copy = null;
	private static NodeResource rootResource = null;
	private static Node rootNode = new Node();

	private static final HashMap<NodeResource, Node> NODES_IN_SCENE = new HashMap<>();

	public static void changeScene(Path path) {
		NODES_IN_SCENE.clear();
		currentPath = path;
		String sceneFile = ProjectPath.resourcesFolder().relativize(path).toString();
		rootResource = (NodeResource) new YamlLoader().load(sceneFile);
		copy = new NodeResource(rootResource);
		removeMissingFields(rootResource);
		rootNode = rootResource.instantiate();
		storeNodes(rootResource, rootNode);
	}

	public static void saveScene() {
		if(currentPath != null) {
			copy = new NodeResource(rootResource);
			YamlSerializer.writeToFile(rootResource, currentPath.toString());
		}
	}

	public static void reload() {
		if(rootResource != null) {
			NODES_IN_SCENE.clear();
			removeMissingFields(rootResource);
			rootNode = rootResource.instantiate();
			storeNodes(rootResource, rootNode);
		}
	}

	private static void removeMissingFields(NodeResource resource) {
		resource.children.forEach((key, child) -> removeMissingFields(child));
		String type = getActualType(resource);
		resource.properties.keySet().removeIf(field -> {
			try {
				return !Reflection.hasField(field, type);
			} catch (ReflectionException e) {
				e.printStackTrace();
				return true;
			}
		});
	}

	private static String getActualType(NodeResource resource) {
		return resource.override == null || resource.override.isEmpty() ? resource.type : getActualType(NodeResource.getOrLoad(resource.override));
	}

	public static Path currentResource() {
		return ProjectPath.resourcesFolder().relativize(currentPath);
	}

	public static Path currentPath() {
		return currentPath;
	}

	public static String currentFileName() {
		return currentPath.getFileName().toString();
	}

	public static boolean hasUnsavedChanges() {
		if(rootResource != null && copy != null) {
			return notEquals(rootResource, copy);
		}
		return false;
	}

	private static boolean notEquals(NodeResource first, NodeResource second) {
		return !Objects.equals(first.type, second.type) ||
				!Objects.equals(first.override, second.override) ||
				!first.properties.equals(second.properties) ||
				first.children.keySet().stream().anyMatch(key -> !second.children.containsKey(key) || notEquals(first.children.get(key), second.children.get(key)));
	}

	private static void storeNodes(NodeResource resource, Node node) {
		NODES_IN_SCENE.put(resource, node);
		resource.children.forEach((key, childResource) -> storeNodes(childResource, node.getChild(key)));
	}

	public static NodeResource rootResource() {
		return rootResource;
	}

	public static Node rootNode() {
		return rootNode;
	}

	public static Node getNode(NodeResource resource) {
		return NODES_IN_SCENE.get(resource);
	}

	public static boolean contains(NodeResource resource) {
		return NODES_IN_SCENE.containsKey(resource);
	}

	public static Node removeNode(NodeResource resource) {
		Node node = NODES_IN_SCENE.remove(resource);
		node.getParent().removeChild(node);
		return node;
	}

	public static String putNode(NodeResource parentResource, NodeResource resource, String key, Node node) {
		if(parentResource != null) {
			Node parentNode = getNode(parentResource);
			if(node.getParent() == parentNode) {
				parentNode.removeChild(key);
				parentNode.addChild(node);
			} else {
				if(parentNode.hasChild(key)) {
					int index = 2;
					while(parentNode.hasChild(key + index)) {
						index ++;
					}
					key = key + index;
				}
				parentNode.addChild(key, node);
				parentResource.children.put(key, resource);
			}
			NODES_IN_SCENE.put(resource, node);
		} else {
			NODES_IN_SCENE.clear();
			rootNode = resource.instantiate();
			storeNodes(resource, rootNode);
		}
		return key;
	}

	public static String putNode(NodeResource parentResource, NodeResource resource, Node node) {
		Node parentNode = getNode(parentResource);
		String key = node.getClass().getSimpleName();
		if(parentNode.hasChild(key)) {
			int index = 2;
			while(parentNode.hasChild(key + index)) {
				index++;
			}
			key = key + index;
		}
		parentNode.addChild(key, node);
		parentResource.children.put(key, resource);
		NODES_IN_SCENE.put(resource, node);
		return key;
	}
}
