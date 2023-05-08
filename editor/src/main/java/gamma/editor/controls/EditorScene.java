package gamma.editor.controls;

import gamma.engine.tree.Node;
import gamma.engine.tree.NodeResource;

import java.nio.file.Path;
import java.util.HashMap;

public class EditorScene {

	private static NodeResource rootResource = null;
	private static Node rootNode = new Node();

	private static final HashMap<NodeResource, Node> NODES_IN_SCENE = new HashMap<>();

	public static void changeScene(Path path) {
		NODES_IN_SCENE.clear();
		Path resourcesFolder = Path.of("demo/src/main/resources");
		String sceneFile = resourcesFolder.relativize(path).toString();
		rootResource = NodeResource.getOrLoad(sceneFile);
		rootNode = rootResource.instantiate();
		storeNodes(rootResource, rootNode);
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
}
