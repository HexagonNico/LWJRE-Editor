package io.github.lwjre.editor.controls;

import io.github.lwjre.engine.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EditorLayout {

	public static void defaultLayout() {
		Path path = Path.of("./editorLayout.ini");
		if(!Files.exists(path)) try {
			String defaultLayout = FileUtils.readResourceAsString("defaultLayout.ini");
			Files.writeString(path, defaultLayout);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
