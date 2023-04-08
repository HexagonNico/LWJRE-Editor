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

public class InspectorGui implements IGui {

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

	public Entity entity;

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 25.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() - 30.0f, ImGuiCond.FirstUseEver);
		if(ImGui.begin("Inspector")) {
			if(this.entity != null) {
				this.entity.getComponents().sorted((component1, component2) -> {
					EditorIndex index1 = component1.getClass().getAnnotation(EditorIndex.class);
					EditorIndex index2 = component2.getClass().getAnnotation(EditorIndex.class);
					if(index1 != null && index2 != null)
						return Integer.compare(index1.value(), index2.value());
					return 1;
				}).forEach(component -> {
					ImGui.text(component.getClass().getSimpleName());
					ImGui.sameLine(ImGui.getWindowWidth() - 25);
					if(ImGui.smallButton("X##" + component.getClass())) {
						entity.removeComponent(component);
					}
					if(ImGui.isItemHovered()) {
						ImGui.beginTooltip();
						ImGui.text("Remove component");
						ImGui.endTooltip();
					}
					FieldsRenderer.renderFields(component);
					ImGui.separator();
				});
				if(ImGui.button("Add component")) {
					ImGui.openPopup("Add component");
				}
				if(ImGui.beginPopupContextItem("Add component")) {
					COMPONENTS.forEach(name -> doComponentMenuItem(name, entity, () -> Class.forName(name).asSubclass(Component.class)));
					Path mavenClasses = Path.of(EditorApplication.currentPath() + "/target/classes");
					Path gradleClasses = Path.of(EditorApplication.currentPath() + "/build/classes/java/main");
					try(URLClassLoader classLoader = new URLClassLoader(new URL[]{mavenClasses.toUri().toURL(), gradleClasses.toUri().toURL()})) {
						doMenuItems(classLoader, mavenClasses, entity);
						doMenuItems(classLoader, gradleClasses, entity);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ImGui.endPopup();
				}
			}
		}
		ImGui.end();
	}

	private static void doComponentMenuItem(String menuLabel, Entity entity, ClassSupplier classSupplier) {
		if(ImGui.menuItem(menuLabel)) {
			try {
				Class<? extends Component> componentClass = classSupplier.supply();
				if(entity.getComponent(componentClass).isEmpty()) {
					entity.addComponent(componentClass.getConstructor().newInstance());
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

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

	private static ArrayList<String> findComponentClasses(Path fromPath) {
		return findComponentClasses(fromPath, fromPath);
	}

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

	private interface ClassSupplier {

		Class<? extends Component> supply() throws ClassNotFoundException;
	}
}
