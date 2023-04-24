package gamma.editor.gui;

import gamma.editor.EditorApplication;
import gamma.editor.gui.inspector.FieldsRenderer;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
import gamma.engine.scene.EntityResource;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import vecmatlib.vector.Vec2i;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Gui component that represents the inspector panel, the one that shows the properties of an entity when selected.
 *
 * @author Nico
 */
public class InspectorGui implements IGui {

	/** List of built-in component classes in classpath */
	private final List<Class<?>> components;

	/** The entity resource that is currently being inspected */
	private EntityResource entityResource;
	/** The actual entity */
	private Entity entity;

	/**
	 * Constructs the inspector gui and loads all component classes.
	 */
	public InspectorGui() {
		try(ScanResult result = new ClassGraph().enableClassInfo().scan()) {
			this.components = result.getSubclasses(Component.class).loadClasses();
		}
	}

	/**
	 * Sets the entity to inspect.
	 *
	 * @param resource Entity resource
	 * @param entity Actual entity
	 */
	public void setEntity(EntityResource resource, Entity entity) {
		this.entityResource = resource;
		this.entity = entity;
	}

	/**
	 * Checks if the given entity is the one being inspected.
	 *
	 * @param resource The entity to check
	 * @return True if the given entity is the one being inspected, otherwise false
	 */
	public boolean isSelected(EntityResource resource) {
		return this.entityResource == resource;
	}

	@Override
	public void draw() {
		// Window size and position
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() - 30.0f, ImGuiCond.FirstUseEver);
		if(ImGui.begin("Inspector") && this.entityResource != null) {
			showComponents(this.entityResource, this.entity);
			// Show "Add component" button
			if(ImGui.button("Add component")) {
				ImGui.openPopup("Add component");
			}
			// Show popup when "Add component" is pressed
			if(ImGui.beginPopupContextItem("Add component")) {
				// Show base component classes
				doMenuItems(this.components, this.entityResource, this.entity);
				// Look for components in the project's compiled classes
				Path mavenClasses = Path.of(EditorApplication.currentPath() + "/target/classes");
				Path gradleClasses = Path.of(EditorApplication.currentPath() + "/build/classes/java/main");
				doMenuItems(mavenClasses, this.entityResource, this.entity);
				doMenuItems(gradleClasses, this.entityResource, this.entity);
				ImGui.endPopup();
			}
		}
		ImGui.end();
	}

	// TODO: Give an order to components

	private static void showComponents(EntityResource resource, Entity entity) {
		entity.getComponents().forEach(component -> {
			String simpleName = component.getClass().getSimpleName();
			String fullName = component.getClass().getName();
			ImGui.text(simpleName);
			// TODO: Show a "Reset" button next to all fields
			if(!EntityResource.getOrLoad(resource.base).components.containsKey(fullName)) {
				ImGui.sameLine(ImGui.getWindowWidth() - 35);
				if(ImGui.smallButton("X##" + simpleName)) {
					resource.components.remove(fullName);
					entity.removeComponent(component);
				}
				if(ImGui.isItemHovered()) {
					ImGui.beginTooltip();
					ImGui.text("Remove component");
					ImGui.endTooltip();
				}
			}
			// TODO: Remove elements if they are the same as the base
			if(resource.components.containsKey(fullName)) {
				FieldsRenderer.renderFields(component, resource.components.get(fullName));
			} else {
				HashMap<String, Object> data = new HashMap<>();
				FieldsRenderer.renderFields(component, data);
				if(!data.isEmpty()) {
					resource.components.put(fullName, data);
				}
			}
			ImGui.separator();
		});
	}

	/**
	 * Renders the given list of component classes as menu items.
	 *
	 * @param classes List of component classes
	 * @param resource Entity resource
	 * @param entity Actual entity
	 */
	private static void doMenuItems(List<Class<?>> classes, EntityResource resource, Entity entity) {
		classes.forEach(givenClass -> {
			if(ImGui.menuItem(givenClass.getSimpleName())) {
				Class<? extends Component> componentClass = givenClass.asSubclass(Component.class);
				if(entity.getComponent(componentClass).isEmpty()) try {
					Component component = componentClass.getConstructor().newInstance();
					resource.components.put(componentClass.getName(), new HashMap<>());
					entity.addComponent(component);
				} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Looks for components in compiled {@code .class} files and renders them as menu items.
	 *
	 * @param classesPath Path where to look for {@code .class} files
	 * @param resource Entity resource
	 * @param entity Actual entity
	 */
	private static void doMenuItems(Path classesPath, EntityResource resource, Entity entity) {
		doMenuItems(findComponentClasses(classesPath), resource, entity);
	}

	/**
	 * Looks for components in compiled {@code .class} files.
	 *
	 * @param fromPath Path to start from
	 * @return A list containing the names of all component classes
	 */
	private static ArrayList<Class<?>> findComponentClasses(Path fromPath) {
		return findComponentClasses(fromPath, fromPath);
	}

	/**
	 * Looks for components in compiled {@code .class} files.
	 *
	 * @param originalPath Starting path needed to get relative path
	 * @param fromPath Path to look in
	 * @return A list containing the names of all component classes
	 */
	private static ArrayList<Class<?>> findComponentClasses(Path originalPath, Path fromPath) {
		ArrayList<Class<?>> result = new ArrayList<>();
		if(Files.exists(fromPath)) try(Stream<Path> directory = Files.list(fromPath)) {
			directory.forEach(path -> {
				if(Files.isDirectory(path)) {
					result.addAll(findComponentClasses(originalPath, path));
				} else if(path.toString().endsWith(".class")) try {
					path = originalPath.relativize(path);
					String className = path.toString().replace(".class", "").replace(File.separatorChar, '.');
					result.add(Thread.currentThread().getContextClassLoader().loadClass(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
