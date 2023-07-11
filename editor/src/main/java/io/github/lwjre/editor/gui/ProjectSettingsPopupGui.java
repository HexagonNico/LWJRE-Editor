package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.hexagonnico.vecmatlib.vector.Vec2i;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.utils.YamlParser;
import io.github.lwjre.engine.utils.YamlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectSettingsPopupGui extends AlertPopupGui {

	private final LinkedHashMap<String, Map<String, Object>> defaultValues = new LinkedHashMap<>();
	private final LinkedHashMap<String, Map<String, Object>> properties = new LinkedHashMap<>();

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title   Title of the popup
	 * @param content Content of the popup
	 */
	public ProjectSettingsPopupGui(String title, String... content) {
		super(title, content);
	}

	@Override
	public void open() {
		((Map<?, ?>) YamlParser.parseResource("defaultSettings.yaml")).forEach((sectionKey, values) -> {
			LinkedHashMap<String, Object> section = new LinkedHashMap<>();
			this.defaultValues.put(sectionKey.toString(), section);
			((Map<?, ?>) values).forEach((key, value) -> section.put(key.toString(), value));
		});
		((Map<?, ?>) YamlParser.parseResource("application.yaml")).forEach((sectionKey, values) -> {
			LinkedHashMap<String, Object> section = new LinkedHashMap<>();
			this.properties.put(sectionKey.toString(), section);
			((Map<?, ?>) values).forEach((key, value) -> section.put(key.toString(), value));
		});
		super.open();
	}

	@Override
	protected void onDrawPopup() {
		this.defaultValues.forEach((section, defaultValues) -> {
			if(!this.properties.containsKey(section)) {
				this.properties.put(section, new LinkedHashMap<>());
			}
			Map<String, Object> values = this.properties.get(section);
			if(ImGui.collapsingHeader(section)) {
				if(ImGui.beginTable("##" + section, 2, ImGuiTableFlags.SizingStretchProp)) {
					ImGui.tableSetupColumn("0", ImGuiTableColumnFlags.WidthFixed);
					ImGui.tableSetupColumn("1", ImGuiTableColumnFlags.WidthStretch);
					defaultValues.forEach((key, value) -> {
						if(values.containsKey(key)) {
							value = values.get(key);
						}
						ImGui.tableNextColumn();
						ImGui.text(key);
						ImGui.tableNextColumn();
						if(value instanceof String) {
							ImString ptr = new ImString((String) value, 256);
							if(ImGui.inputText("##" + section + ':' + key, ptr)) {
								values.put(key, ptr.get());
							}
						} else if(value instanceof Integer) {
							ImInt ptr = new ImInt((Integer) value);
							if(ImGui.inputInt("##" + section + ':' + key, ptr)) {
								values.put(key, ptr.get());
							}
						} else if(value instanceof Boolean) {
							if(ImGui.checkbox("##" + section + ':' + key, (Boolean) value)) {
								values.put(key, !((Boolean) value));
							}
						} else if(value instanceof Vec2i vec) {
							int[] ptr = new int[] {vec.x(), vec.y()};
							if(ImGui.inputInt2("##" + section + ':' + key, ptr)) {
								values.put(key, new Vec2i(ptr[0], ptr[1]));
							}
						} else if(value instanceof Color4f col) {
							float[] ptr = new float[] {col.r(), col.g(), col.b(), col.a()};
							if(ImGui.colorEdit4("##" + section + ':' + key, ptr)) {
								values.put(key, new Color4f(ptr[0], ptr[1], ptr[2], ptr[3]));
							}
						}
						// TODO: Add all input types
					});
					ImGui.endTable();
				}
			}
		});
		super.onDrawPopup();
	}

	// TODO: Replace with "apply" and "cancel" buttons

	@Override
	public void close() {
		try {
			Files.write(ProjectPath.resourcesFolder("application.yaml"), YamlSerializer.serialize(this.properties).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.close();
	}
}
