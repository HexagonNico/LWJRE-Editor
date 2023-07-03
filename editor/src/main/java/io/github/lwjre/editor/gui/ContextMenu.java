package io.github.lwjre.editor.gui;

import imgui.ImGui;

import java.util.ArrayList;

/**
 * Represents a context menu shown when an item is right-clicked.
 *
 * @author Nico
 */
public class ContextMenu {

	/** List of menu items */
	private final ArrayList<Runnable> menuItems = new ArrayList<>();

	/**
	 * Adds an item to the menu.
	 *
	 * @param label The item's label
	 * @param shortcut The item's shortcut
	 * @param action Action to perform when the item is clicked
	 */
	public void menuItem(String label, String shortcut, Runnable action) {
		this.menuItems.add(() -> {
			if(ImGui.menuItem(label, shortcut)) {
				action.run();
			}
		});
	}

	/**
	 * Adds an item to the menu.
	 *
	 * @param label The item's label
	 * @param action Action to perform when the item is clicked
	 */
	public void menuItem(String label, Runnable action) {
		this.menuItems.add(() -> {
			if(ImGui.menuItem(label)) {
				action.run();
			}
		});
	}

	/**
	 * Adds a separator to the menu.
	 */
	public void separator() {
		this.menuItems.add(ImGui::separator);
	}

	/**
	 * Adds a submenu to the menu.
	 *
	 * @param label The item's label
	 * @param submenu The submenu
	 */
	public void submenu(String label, ContextMenu submenu) {
		this.menuItems.add(() -> {
			if(ImGui.beginMenu(label)) {
				submenu.menuItems.forEach(Runnable::run);
				ImGui.endMenu();
			}
		});
	}

	/**
	 * Draws the menu.
	 *
	 * @return True if the menu is open, otherwise false
	 */
	public boolean draw() {
		if(ImGui.beginPopupContextItem()) {
			this.menuItems.forEach(Runnable::run);
			ImGui.endPopup();
			return true;
		}
		return false;
	}
}
