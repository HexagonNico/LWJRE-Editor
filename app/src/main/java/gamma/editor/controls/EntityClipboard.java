package gamma.editor.controls;

import gamma.editor.EditorUtils;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class EntityClipboard {

	private Entity entity;
	private String name;

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

	public void cut(Entity entity, String name) {
		this.entity = entity;
		this.name = name;
	}

	public void copy(Entity entity, String name) {
		this.entity = deepCopy(entity);
		this.name = name;
	}

	public void paste(Entity entity) {
		if(this.entity != null) {
			String actualName = EditorUtils.findUnusedName(this.name, entity);
			this.entity.setParent(actualName, entity);
			this.entity = deepCopy(this.entity);
		}
	}

	public boolean isInClipboard(Entity entity) {
		return this.entity == entity;
	}

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

	private static Component deepCopy(Component from) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Component copy = from.getClass().getConstructor().newInstance();
		copyFields(copy.getClass(), from, copy);
		return copy;
	}

	private static void copyFields(Class<?> fromClass, Component fromObject, Component toObject) throws IllegalAccessException {
		for(Field field : fromClass.getDeclaredFields()) {
			if(!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(toObject, field.get(fromObject));
			}
		}
		if(!fromClass.getSuperclass().equals(Component.class)) {
			copyFields(fromClass.getSuperclass(), fromObject, toObject);
		}
	}
}
