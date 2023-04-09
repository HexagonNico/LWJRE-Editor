package gamma.editor.controls;

import gamma.editor.EditorUtils;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Class that represents the entity clipboard.
 * Used to implement cut/copy/paste functions.
 *
 * @author Nico
 */
public class EntityClipboard {

	/** The entity in the clipboard */
	private Entity entity;
	/** Name of the entity */
	private String name;

	/**
	 * Listens for clipboard hotkeys: Ctrl+X, Ctrl+C, Ctrl+V.
	 *
	 * @param entity The entity to handle in case a hotkey is pressed
	 * @param name Name of the entity
	 */
	public void listenForKeyBinds(Entity entity, String name) {
		if((ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))) {
			if(ImGui.isKeyPressed(GLFW.GLFW_KEY_X, false)) {
				this.cut(entity, name);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_C, false)) {
				this.copy(entity, name);
			} else if(ImGui.isKeyPressed(GLFW.GLFW_KEY_V, false)) {
				this.paste(entity);
			}
		}
	}

	/**
	 * Cut operation. Sets the entity in the clipboard as the given one.
	 *
	 * @param entity The entity to cut
	 * @param name The entity's name
	 */
	public void cut(Entity entity, String name) {
		this.entity = entity;
		this.name = name;
	}

	/**
	 * Copy operations. Clones the given entity and puts it in the clipboard.
	 *
	 * @param entity The entity to copy
	 * @param name The entity's name
	 */
	public void copy(Entity entity, String name) {
		this.entity = deepCopy(entity);
		this.name = name;
	}

	/**
	 * Paste operation. Adds the entity in the clipboard as a child of the given entity.
	 * This method has no effect if there is no entity in the clipboard.
	 *
	 * @param entity The entity on which to paste the entity in the clipboard
	 */
	public void paste(Entity entity) {
		if(this.entity != null) {
			String actualName = EditorUtils.findUnusedName(this.name, entity);
			this.entity.setParent(actualName, entity);
			this.entity = deepCopy(this.entity);
		}
	}

	/**
	 * Checks if the given entity is in the clipboard.
	 *
	 * @param entity The entity to check
	 * @return True if the entity in the clipboard is equal to the given one, otherwise false
	 */
	public boolean isInClipboard(Entity entity) {
		return this.entity == entity;
	}

	/**
	 * Creates a deep copy of an entity and all of its components.
	 *
	 * @param from The entity to copy
	 * @return An exact copy of the entity that is not the same entity
	 */
	private static Entity deepCopy(Entity from) {
		Entity entity = new Entity();
		from.getComponents().forEach(component -> {
			try {
				entity.addComponent(deepCopy(component));
			} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		from.forEachChild((key, child) -> {
			entity.addChild(key, deepCopy(child));
		});
		return entity;
	}

	/**
	 * Creates a deep copy of a component.
	 *
	 * @param from The component to copy
	 * @return An exact copy of the component that is not the same component
	 * @throws NoSuchMethodException If the component does not have a no-args constructor
	 * @throws InvocationTargetException If the component's constructor throws an exception
	 * @throws InstantiationException If the component is abstract
	 * @throws IllegalAccessException If the component's constructor is not public
	 */
	private static Component deepCopy(Component from) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Component copy = from.getClass().getConstructor().newInstance();
		copyFields(copy.getClass(), from, copy);
		return copy;
	}

	/**
	 * Copies all the fields of a component to another one.
	 *
	 * @param fromClass The class to get the fields from
	 * @param fromObject The object to copy
	 * @param toObject The destination of the copy
	 */
	private static void copyFields(Class<?> fromClass, Component fromObject, Component toObject) {
		for(Field field : fromClass.getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers())) try {
				field.setAccessible(true);
				field.set(toObject, field.get(fromObject));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(!fromClass.getSuperclass().equals(Component.class)) {
			copyFields(fromClass.getSuperclass(), fromObject, toObject);
		}
	}
}
