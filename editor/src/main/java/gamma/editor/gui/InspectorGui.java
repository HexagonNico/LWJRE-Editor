package gamma.editor.gui;

import gamma.editor.EditorApplication;
import gamma.editor.gui.inspector.FieldsRenderer;
import gamma.engine.annotations.EditorIndex;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import vecmatlib.vector.Vec2i;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Gui component that represents the inspector panel, the one that shows the properties of an entity when selected.
 *
 * @author Nico
 */
public class InspectorGui implements IGui {

	// TODO: Find a way to get the components from the engine's jar file
	private static final Set<String> COMPONENTS = Set.of(
			"gamma.engine.components.BoundingBox3D",
			"gamma.engine.components.Camera3D",
			"gamma.engine.components.CollisionObject3D",
			"gamma.engine.components.KinematicBody3D",
			"gamma.engine.components.MeshRenderer",
			"gamma.engine.components.ModelRenderer",
			"gamma.engine.components.PointLight3D",
			"gamma.engine.components.Transform3D"
	);

	/** The entity that is currently being inspected */
	public Entity entity;

	@Override
	public void draw() {
		// Window size and position
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() - 30.0f, ImGuiCond.FirstUseEver);
		// Show window if an entity is selected
		if(ImGui.begin("Inspector") && this.entity != null) {
			// Show all components in the right order
			this.entity.getComponents().sorted((component1, component2) -> {
				EditorIndex index1 = component1.getClass().getAnnotation(EditorIndex.class);
				EditorIndex index2 = component2.getClass().getAnnotation(EditorIndex.class);
				if (index1 != null && index2 != null)
					return Integer.compare(index1.value(), index2.value());
				return 1;
			}).forEach(component -> {
				// The name of the component is the simple name of the class
				ImGui.text(component.getClass().getSimpleName());
				// Show "Remove component" button
				ImGui.sameLine(ImGui.getWindowWidth() - 25);
				if(ImGui.smallButton("X##" + component.getClass())) {
					entity.removeComponent(component);
				}
				// Show "Remove component" tooltip
				if(ImGui.isItemHovered()) {
					ImGui.beginTooltip();
					ImGui.text("Remove component");
					ImGui.endTooltip();
				}
				// Show the component's fields
				FieldsRenderer.renderFields(component);
				// Show a separator between every component
				ImGui.separator();
			});
			// Show "Add component" button
			if(ImGui.button("Add component")) {
				ImGui.openPopup("Add component");
			}
			// Show popup when "Add component" is pressed
			if(ImGui.beginPopupContextItem("Add component")) {
				// Show base component classes
				doMenuItems(entity);
				// Look for components in the project's compiled classes
				Path mavenClasses = Path.of(EditorApplication.currentPath() + "/target/classes");
				Path gradleClasses = Path.of(EditorApplication.currentPath() + "/build/classes/java/main");
				try (URLClassLoader classLoader = new URLClassLoader(new URL[]{mavenClasses.toUri().toURL(), gradleClasses.toUri().toURL()})) {
					doMenuItems(classLoader, mavenClasses, entity);
					doMenuItems(classLoader, gradleClasses, entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
				ImGui.endPopup();
			}
		}
		ImGui.end();
	}

	// TODO: Find a way to unify the two doMenuItems

	/**
	 * Shows all the classes {@link InspectorGui#COMPONENTS} in the popup menu.
	 *
	 * @param entity Needed to add the component when an item is pressed
	 */
	private static void doMenuItems(Entity entity) {
		COMPONENTS.forEach(name -> {
			if(ImGui.menuItem(name)) try {
				Class<? extends Component> componentClass = Class.forName(name).asSubclass(Component.class);
				if(entity.getComponent(componentClass).isEmpty()) {
					entity.addComponent(componentClass.getConstructor().newInstance());
				}
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Looks for component classes in the given path using the given class loader.
	 *
	 * @param classLoader The class loader to load the classes
	 * @param classesPath Path at which to look for {@code .class} files
	 * @param entity Needed to add the component when an item is pressed
	 */
	private static void doMenuItems(ClassLoader classLoader, Path classesPath, Entity entity) {
		findComponentClasses(classesPath).forEach(name -> {
			try {
				Class<?> cls = classLoader.loadClass(name);
				if(Component.class.isAssignableFrom(cls) && ImGui.menuItem(name)) {
					Class<? extends Component> componentClass = cls.asSubclass(Component.class);
					if(entity.getComponent(componentClass).isEmpty()) {
						entity.addComponent(componentClass.getConstructor().newInstance());
					}
				}
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * Looks for components in compiled {@code .class} files.
	 *
	 * @param fromPath Path to start from
	 * @return A list containing the names of all component classes
	 */
	private static ArrayList<String> findComponentClasses(Path fromPath) {
		return findComponentClasses(fromPath, fromPath);
	}

	/**
	 * Looks for components in compiled {@code .class} files.
	 *
	 * @param originalPath Starting path needed to get relative path
	 * @param fromPath Path to look in
	 * @return A list containing the names of all component classes
	 */
	private static ArrayList<String> findComponentClasses(Path originalPath, Path fromPath) {
		ArrayList<String> result = new ArrayList<>();
		if(Files.exists(fromPath)) try(Stream<Path> directory = Files.list(fromPath)) {
			directory.forEach(path -> {
				if(Files.isDirectory(path)) {
					result.addAll(findComponentClasses(originalPath, path));
				} else if(path.toString().endsWith(".class")) {
					path = originalPath.relativize(path);
					String className = path.toString().replace(".class", "").replace(File.separatorChar, '.');
					result.add(className);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
