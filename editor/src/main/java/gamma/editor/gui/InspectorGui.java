package gamma.editor.gui;

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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 20.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 25.0f, ImGuiCond.FirstUseEver);
		ImGui.begin("Inspector");
		if(this.entity != null) {
			this.entity.getComponents().sorted((component1, component2) -> {
				EditorIndex index1 = component1.getClass().getAnnotation(EditorIndex.class);
				EditorIndex index2 = component2.getClass().getAnnotation(EditorIndex.class);
				if(index1 != null && index2 != null)
					return Integer.compare(index1.value(), index2.value());
				return 1;
			}).forEach(component -> {
				ImGui.text(component.getClass().getSimpleName());
				for(Field field : component.getClass().getDeclaredFields()) {
					if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
						try {
							FieldsRenderer.renderField(component, field);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				ImGui.separator();
			});
			if(ImGui.button("Add component")) {
				ImGui.openPopup("Add component");
			}
			if(ImGui.beginPopupContextItem("Add component")) {
				COMPONENTS.forEach(name -> doComponentMenuItem(name, entity, () -> Class.forName(name).asSubclass(Component.class)));
				Path classes = Path.of("build/classes/java/main");
				try(URLClassLoader classLoader = new URLClassLoader(new URL[]{classes.toUri().toURL()})) {
					findComponentClasses(classes).forEach(name -> doComponentMenuItem(name, entity, () -> classLoader.loadClass(name).asSubclass(Component.class)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				ImGui.endPopup();
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

	private static ArrayList<String> findComponentClasses(Path fromPath) {
		ArrayList<String> result = new ArrayList<>();
		try(Stream<Path> directory = Files.list(fromPath)) {
			directory.forEach(path -> {
				if(Files.isDirectory(path)) {
					result.addAll(findComponentClasses(path));
				} else if(path.toString().endsWith(".class")) {
					String pathStr = path.toString();
					String className = pathStr.substring(24, pathStr.lastIndexOf('.')).replace(File.separatorChar, '.');
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
