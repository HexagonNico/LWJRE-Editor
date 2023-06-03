package gamma.editor.gui;

import imgui.ImGui;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ContextMenu {

	private final ArrayList<Runnable> menuItems = new ArrayList<>();

	public void menuItem(String label, String shortcut, Runnable action) {
		this.menuItems.add(() -> {
			if(ImGui.menuItem(label, shortcut)) {
				action.run();
			}
		});
	}

	public void menuItem(String label, String shortcut) {
		this.menuItems.add(() -> ImGui.menuItem(label, shortcut));
	}

	public void menuItem(String label, Runnable action) {
		this.menuItems.add(() -> {
			if(ImGui.menuItem(label)) {
				action.run();
			}
		});
	}

	public void separator() {
		this.menuItems.add(ImGui::separator);
	}

	public void submenu(String label, Supplier<ContextMenu> submenu) {
		this.menuItems.add(() -> {
			if(ImGui.beginMenu(label)) {
				submenu.get().menuItems.forEach(Runnable::run);
				ImGui.endMenu();
			}
		});
	}

	public void draw() {
		if(ImGui.beginPopupContextItem()) {
			this.menuItems.forEach(Runnable::run);
			ImGui.endPopup();
		}
	}

	public void draw(Runnable onDraw) {
		if(ImGui.beginPopupContextItem()) {
			onDraw.run();
			this.menuItems.forEach(Runnable::run);
			ImGui.endPopup();
		}
	}
}
