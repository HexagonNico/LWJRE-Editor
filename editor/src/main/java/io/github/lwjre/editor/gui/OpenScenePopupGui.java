package io.github.lwjre.editor.gui;

import imgui.ImGui;
import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.controllers.EditorScene;
import io.github.lwjre.engine.resources.NodeResource;
import io.github.lwjre.engine.utils.YamlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class OpenScenePopupGui extends PopupModalGui {

	/**
	 * Constructs a popup with the given content.
	 *
	 * @param title   Title of the popup
	 * @param content Content of the popup
	 */
	public OpenScenePopupGui(String title, String... content) {
		super(title, content);
	}

	@Override
	protected void onDrawPopup() {
		try(Stream<Path> files = Files.walk(ProjectPath.resourcesFolder())) {
			files.forEach(path -> {
				String fileName = path.getFileName().toString();
				if(fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
					String resourcePath = ProjectPath.resourcesFolder().relativize(path).toString();
					if(ImGui.menuItem(resourcePath)) {
						// TODO: Remove selected node from the fields
						Object resource = YamlParser.parseResource(resourcePath);
						if(resource instanceof NodeResource nodeResource) {
							EditorScene.changeScene(nodeResource, resourcePath);
						}
						this.close();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
