package io.github.lwjre.editor.gui;

import imgui.ImGui;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.YamlParser;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Popup used to show a list of all the project's scenes and select one to open.
 *
 * @author Nico
 */
public class OpenScenePopup implements GuiComponent {

	/** Set to true to open the popup */
	private boolean shouldOpen = false;

	@Override
	public void draw() {
		if(this.shouldOpen) {
			ImGui.openPopup("Open scene");
			this.shouldOpen = false;
		}
		if(ImGui.beginPopupModal("Open scene")) {
			try(Stream<Path> files = Files.walk(ProjectPath.resourcesFolder())) {
				files.forEach(path -> {
					String fileName = path.getFileName().toString();
					if(!fileName.equals("settings.yaml") && (fileName.endsWith(".yaml") || fileName.endsWith(".yml"))) {
						String resourcePath = ProjectPath.resourcesFolder().relativize(path).toString();
						if(ImGui.menuItem(resourcePath)) {
							// TODO: Remove selected node from the fields
							Object resource = YamlParser.parseResource(resourcePath);
							if(resource instanceof NodeResource nodeResource) {
								EditorScene.changeScene(nodeResource, resourcePath);
							}
							ImGui.closeCurrentPopup();
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(ImGui.button("Cancel") || ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				ImGui.closeCurrentPopup();
			}
			ImGui.endPopup();
		}
	}

	/**
	 * Opens the popup.
	 */
	public void open() {
		this.shouldOpen = true;
	}
}
