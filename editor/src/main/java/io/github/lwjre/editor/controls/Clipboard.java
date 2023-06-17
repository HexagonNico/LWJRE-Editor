package io.github.lwjre.editor.controls;

import java.util.function.Consumer;

public final class Clipboard {

	private static Object content;
	private static Consumer<Object> pasteFunction;

	public static void setContent(Object object) {
		content = object;
	}

	public static void setContent(Object object, Consumer<Object> onPaste) {
		content = object;
		pasteFunction = onPaste;
	}

	public static Object getContent() {
		return content;
	}

	public static void paste(Object target) {
		pasteFunction.accept(target);
	}
}
