package gamma.editor.inspector;

import gamma.editor.gui.IGui;
import gamma.engine.annotations.EditorIndex;
import gamma.engine.scene.Component;
import gamma.engine.scene.Entity;
import gamma.engine.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import vecmatlib.vector.Vec2i;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;

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
				return 0;
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
				// TODO: Load class files from 'run/build/classes'
				COMPONENTS.forEach(name -> {
					if(ImGui.menuItem(name)) {
						try {
							Class<? extends Component> type = Class.forName(name).asSubclass(Component.class);
							if(entity.getComponent(type).isEmpty()) {
								entity.addComponent(type.getConstructor().newInstance());
							}
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
							throw new RuntimeException(e); // TODO: Better error message
						}
					}
				});
				ImGui.endPopup();
			}
		}
		ImGui.end();
	}
}
