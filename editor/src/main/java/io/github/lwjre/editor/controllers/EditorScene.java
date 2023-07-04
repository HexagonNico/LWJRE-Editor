package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.nodes.Node;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.Reflection;
import io.github.lwjre.engine.utils.ReflectionException;
import io.github.lwjre.engine.utils.YamlSerializer;

import java.nio.file.Path;

/**
 * Static class used to store the scene that is currently running in the editor.
 *
 * @author Nico
 */
public class EditorScene {

	/** Path to the current scene in the resources folder */
	private static String currentPath = null;

	/** Copy of the current scene used to check if there are unsaved changes */
	private static NodeResource copy = null;
	/** Resource of the current scene */
	private static NodeResource rootResource = null;
	/** Root of the current scene */
	private static Node rootNode = new Node();

	/**
	 * Changes the current scene.
	 *
	 * @param nodeResource Resource of the new scene
	 * @param path Path to the new scene in the resources folder
	 */
	public static void changeScene(NodeResource nodeResource, String path) {
		rootResource = nodeResource;
		copy = new NodeResource(nodeResource);
		rootNode = rootResource.instantiate();
		currentPath = path;
	}

	/**
	 * Reloads the current scene.
	 * Needs to be called when classes are loaded dynamically.
	 */
	public static void reload() {
		if(rootResource != null) {
			removeMissingFields(rootResource);
			rootNode = rootResource.instantiate();
		}
	}

	/**
	 * Removes missing fields from the given resource and its children.
	 *
	 * @param resource The resource to remove children from
	 */
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

	/**
	 * Gets the type of the given resource by taking into account overridden resources.
	 *
	 * @param resource The resource to check
	 * @return The actual type of the given resource
	 */
	private static String getActualType(NodeResource resource) {
		return resource.override == null || resource.override.isEmpty() ? resource.type : getActualType(NodeResource.getOrLoad(resource.override));
	}

	/**
	 * Saves the current scene to its file.
	 */
	public static void saveScene() {
		if(currentPath != null) {
			copy = new NodeResource(rootResource);
			YamlSerializer.writeToFile(rootResource, ProjectPath.resourcesFolder(currentPath).toString());
		}
	}

	/**
	 * Returns the path to the current scene's file.
	 *
	 * @return The path to the current scene's file
	 */
	public static Path currentPath() {
		return Path.of(currentPath);
	}

	/**
	 * Returns the name of the current scene's file without extension.
	 * Used to get the name of the root node.
	 *
	 * @return The name of the current scene's file without extension
	 */
	public static String rootName() {
		String fileName = currentPath().getFileName().toString();
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	/**
	 * Checks if the current scene has unsaved changes.
	 *
	 * @return True if the current scene has unsaved changes, otherwise false
	 */
	public static boolean hasUnsavedChanges() {
		if(rootResource != null && copy != null) {
			return !rootResource.equals(copy);
		}
		return false;
	}

	/**
	 * Returns the current scene's root resource.
	 *
	 * @return The current scene's root resource
	 */
	public static NodeResource rootResource() {
		return rootResource;
	}

	/**
	 * Returns the current scene's root node.
	 *
	 * @return The current scene's root node
	 */
	public static Node rootNode() {
		return rootNode;
	}
}
