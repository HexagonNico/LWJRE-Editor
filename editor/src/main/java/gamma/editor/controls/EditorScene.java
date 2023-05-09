package gamma.editor.controls;

import gamma.engine.resources.YamlLoader;
import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;
import gamma.engine.utils.YamlSerializer;

import java.nio.file.Path;
import java.util.HashMap;

public class EditorScene {

	private static Path currentPath = null;

	private static NodeResource rootResource = null;
	private static Node rootNode = new Node();

	private static final HashMap<NodeResource, Node> NODES_IN_SCENE = new HashMap<>();

	public static void changeScene(Path path) {
		NODES_IN_SCENE.clear();
		currentPath = path;
		Path resourcesFolder = Path.of("demo/src/main/resources");
		String sceneFile = resourcesFolder.relativize(path).toString();
		rootResource = (NodeResource) new YamlLoader().load(sceneFile);
		rootNode = rootResource.instantiate();
		storeNodes(rootResource, rootNode);
	}

	public static void saveScene() {
		if(currentPath != null) {
			YamlSerializer.writeToFile(rootResource, currentPath.toString());
		}
	}

	private static void storeNodes(NodeResource resource, Node node) {
		NODES_IN_SCENE.put(resource, node);
		resource.children.forEach((key, childResource) -> {
			Node childNode = node.getChild(key).orElseThrow();
			storeNodes(childResource, childNode);
		});
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

	public static Node removeNode(NodeResource resource) {
		Node node = NODES_IN_SCENE.remove(resource);
		node.getParent().removeChild(node);
		return node;
	}

	public static String putNode(NodeResource parentResource, NodeResource resource, String key, Node node) {
		Node parentNode = getNode(parentResource);
		if(parentNode.hasChild(key)) {
			int index = 2;
			while(parentNode.hasChild(key + index)) {
				index ++;
			}
			key = key + index;
		}
		parentNode.addChild(key, node);
		parentResource.children.put(key, resource);
		NODES_IN_SCENE.put(resource, node);
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
