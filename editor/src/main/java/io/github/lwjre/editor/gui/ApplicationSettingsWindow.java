package io.github.lwjre.editor.gui;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import io.github.hexagonnico.vecmatlib.color.Color3f;
import io.github.hexagonnico.vecmatlib.color.Color4f;
import io.github.hexagonnico.vecmatlib.vector.*;
import io.github.lwjre.editor.models.ApplicationSettingsEditor;

/**
 * Closeable window used to update application settings.
 *
 * @author Nico
 */
public class ApplicationSettingsWindow implements GuiComponent {

	/** Needed to read application settings */
	private final ApplicationSettingsEditor settings;
	/** Remembers if the window is open or closed */
	private final ImBoolean open = new ImBoolean(false);

	/**
	 * Constructs the application settings window.
	 *
	 * @param settings Needed to read application settings
	 */
	public ApplicationSettingsWindow(ApplicationSettingsEditor settings) {
		this.settings = settings;
	}

	@Override
	public void draw() {
		if(this.open.get()) {
			if(ImGui.begin("Application settings", this.open)) {
				this.settings.forEachSection(section -> {
					if(ImGui.collapsingHeader(section)) {
						if(ImGui.beginTable("##Table", 2, ImGuiTableFlags.SizingStretchProp)) {
							ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthFixed);
							ImGui.tableSetupColumn("", ImGuiTableColumnFlags.WidthStretch);
							this.settings.forEachValue(section, (key, value) -> {
								ImGui.tableNextColumn();
								ImGui.text(key);
								ImGui.tableNextColumn();
								if(value instanceof Integer i) {
									this.input(section, key, i);
								} else if(value instanceof Double d) {
									this.input(section, key, d.floatValue());
								} else if(value instanceof String str) {
									this.input(section, key, str);
								} else if(value instanceof Boolean bool) {
									this.input(section, key, bool);
								} else if(value instanceof Vec2f vec) {
									this.input(section, key, vec);
								} else if(value instanceof Vec3f vec) {
									this.input(section, key, vec);
								} else if(value instanceof Vec4f vec) {
									this.input(section, key, vec);
								} else if(value instanceof Vec2i vec) {
									this.input(section, key, vec);
								} else if(value instanceof Vec3i vec) {
									this.input(section, key, vec);
								} else if(value instanceof Vec4i vec) {
									this.input(section, key, vec);
								} else if(value instanceof Color3f col) {
									this.input(section, key, col);
								} else if(value instanceof Color4f col) {
									this.input(section, key, col);
								}
							});
							ImGui.endTable();
						}
					}
				});
				if(ImGui.button("Apply")) {
					this.settings.save();
					this.close();
				}
				ImGui.sameLine();
				if(ImGui.button("Cancel")) {
					this.close();
				}
			}
			ImGui.end();
		}
	}

	/**
	 * Opens or closes the window.
	 *
	 * @param open True to open the window, false to close it
	 */
	public void setOpen(boolean open) {
		this.open.set(open);
	}

	/**
	 * Opens this window
	 */
	public void open() {
		this.setOpen(true);
	}

	/**
	 * Closes this window
	 */
	public void close() {
		this.setOpen(false);
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param i Value
	 */
	private void input(String section, String key, int i) {
		ImInt ptr = new ImInt(i);
		if(ImGui.inputInt("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, ptr.get());
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param f Value
	 */
	private void input(String section, String key, float f) {
		ImFloat ptr = new ImFloat(f);
		if(ImGui.inputFloat("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, ptr.get());
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param str Value
	 */
	private void input(String section, String key, String str) {
		ImString ptr = new ImString(str, 256);
		if(ImGui.inputText("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, ptr.get());
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param bool Value
	 */
	private void input(String section, String key, boolean bool) {
		ImBoolean ptr = new ImBoolean(bool);
		if(ImGui.checkbox("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, ptr.get());
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec2f vec) {
		float[] ptr = new float[] {vec.x(), vec.y()};
		if(ImGui.inputFloat2("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec2f(ptr[0], ptr[1]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec3f vec) {
		float[] ptr = new float[] {vec.x(), vec.y(), vec.z()};
		if(ImGui.inputFloat3("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec3f(ptr[0], ptr[1], ptr[2]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec4f vec) {
		float[] ptr = new float[] {vec.x(), vec.y(), vec.z(), vec.w()};
		if(ImGui.inputFloat4("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec4f(ptr[0], ptr[1], ptr[2], ptr[3]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec2i vec) {
		int[] ptr = new int[] {vec.x(), vec.y()};
		if(ImGui.inputInt2("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec2i(ptr[0], ptr[1]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec3i vec) {
		int[] ptr = new int[] {vec.x(), vec.y(), vec.z()};
		if(ImGui.inputInt3("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec3i(ptr[0], ptr[1], ptr[2]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param vec Value
	 */
	private void input(String section, String key, Vec4i vec) {
		int[] ptr = new int[] {vec.x(), vec.y(), vec.z(), vec.w()};
		if(ImGui.inputInt4("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Vec4i(ptr[0], ptr[1], ptr[2], ptr[3]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param col Value
	 */
	private void input(String section, String key, Color3f col) {
		float[] ptr = new float[] {col.x(), col.y(), col.z()};
		if(ImGui.colorEdit3("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Color3f(ptr[0], ptr[1], ptr[2]));
		}
	}

	/**
	 * Renders an input field.
	 *
	 * @param section Name of the section
	 * @param key Key
	 * @param col Value
	 */
	private void input(String section, String key, Color4f col) {
		float[] ptr = new float[] {col.x(), col.y(), col.z(), col.w()};
		if(ImGui.colorEdit4("##" + section + ':' + key, ptr)) {
			this.settings.put(section, key, new Color4f(ptr[0], ptr[1], ptr[2], ptr[3]));
		}
	}
}
