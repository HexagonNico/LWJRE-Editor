package gamma.editor.gui;

import gamma.editor.EditorApplication;
import gamma.editor.gui.inspector.FieldsRenderer;
import gamma.engine.annotations.EditorIndex;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
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

	/** The entity that is currently being inspected */
	public Entity entity;

	/**
	 * Constructs the inspector gui and loads all component classes.
	 */
	public InspectorGui() {
		try(ScanResult result = new ClassGraph().enableClassInfo().scan()) {
			this.components = result.getSubclasses(Component.class).loadClasses();
		}
	}

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
				doMenuItems(this.components, entity);
				// Look for components in the project's compiled classes
				Path mavenClasses = Path.of(EditorApplication.currentPath() + "/target/classes");
				Path gradleClasses = Path.of(EditorApplication.currentPath() + "/build/classes/java/main");
				doMenuItems(mavenClasses, entity);
				doMenuItems(gradleClasses, entity);
				ImGui.endPopup();
			}
		}
		ImGui.end();
	}

	/**
	 * Renders the given list of classes as menu items for the "Add component" button.
	 *
	 * @param classes List of classes
	 * @param entity Needed to add the component when the menu item is clicked
	 */
	private static void doMenuItems(List<Class<?>> classes, Entity entity) {
		classes.forEach(classObject -> {
			if(ImGui.menuItem(classObject.getSimpleName())) {
				Class<? extends Component> componentClass = classObject.asSubclass(Component.class);
				if(entity.getComponent(componentClass).isEmpty()) try {
					entity.addComponent(componentClass.getConstructor().newInstance());
				} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Looks for components in compiled {@code .class} files and renders them as menu items.
	 *
	 * @param classesPath Path where to look for classes
	 * @param entity Needed to add the component when the menu item is clicked
	 */
	private static void doMenuItems(Path classesPath, Entity entity) {
		doMenuItems(findComponentClasses(classesPath), entity);
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
