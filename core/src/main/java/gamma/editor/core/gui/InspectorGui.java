package gamma.editor.core.gui;

import gamma.engine.core.scene.Entity;
import gamma.engine.core.window.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import vecmatlib.vector.Vec2i;

public class InspectorGui implements IEditorGui {

	private Entity entity;

	@Override
	public void draw() {
		Vec2i windowSize = Window.getCurrent().getSize();
		ImGui.setNextWindowPos(windowSize.x() - 5.0f - windowSize.x() / 8.0f, 5.0f, ImGuiCond.Once);
		ImGui.setNextWindowSize(windowSize.x() / 8.0f, windowSize.y() / 2.0f - 10.0f, ImGuiCond.Once);
		ImGui.begin("Inspector");
		if(this.entity != null) {
			this.entity.getComponents().forEach(component -> {
				ImGui.labelText(component.getClass().getSimpleName(), "Label");
			});
		}
		ImGui.end();
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
