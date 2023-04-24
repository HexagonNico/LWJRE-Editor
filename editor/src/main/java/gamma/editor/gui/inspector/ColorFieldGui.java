package gamma.editor.gui.inspector;

import gamma.engine.scene.Component;
import imgui.ImGui;
import vecmatlib.color.Color3f;
import vecmatlib.color.Color4f;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Gui component to render a {@link Color3f} or {@link Color4f} field as a {@link ImGui#colorEdit3(String, float[])} or {@link ImGui#colorEdit4(String, float[])} gui.
 *
 * @author Nico
 */
public class ColorFieldGui implements IFieldGui {

	@Override
	public void drawGui(Component component, Field field, HashMap<String, Object> values) throws IllegalAccessException {
		Object obj = field.get(component);
		if(obj instanceof Color3f color) {
			float[] ptr = {color.r(), color.g(), color.b()}; // TODO: Color flags
			if(ImGui.colorEdit3("##" + component.getClass() + ":" + field.getName(), ptr)) {
				color = new Color3f(ptr[0], ptr[1], ptr[2]);
				field.set(component, color);
				values.put(field.getName(), color);
			}
		} else if(obj instanceof Color4f color) {
			float[] ptr = {color.r(), color.g(), color.b(), color.a()}; // TODO: Color flags
			if(ImGui.colorEdit4("##" + component.getClass() + ":" + field.getName(), ptr)) {
				color = new Color4f(ptr[0], ptr[1], ptr[2], ptr[3]);
				field.set(component, color);
				values.put(field.getName(), color);
			}
		}
	}
}
