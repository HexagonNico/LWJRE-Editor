package io.github.lwjre.editor.controllers;

import java.util.function.Consumer;

/**
 * Class responsible for the copy/paste function.
 *
 * @author Nico
 */
public final class Clipboard {

	/** Clipboard content */
	private static Object content;
	/** Action to run when the content is pasted */
	private static Consumer<Object> pasteFunction;

	/**
	 * Sets the clipboard's content.
	 *
	 * @param object Clipboard content
	 * @param onPaste Action to run when the content is pasted
	 */
	public static void setContent(Object object, Consumer<Object> onPaste) {
		content = object;
		pasteFunction = onPaste;
	}

	/**
	 * Gets the clipboard's content.
	 *
	 * @return The clipboard's content
	 */
	public static Object getContent() {
		return content;
	}

	/**
	 * Pastes the clipboard's content.
	 *
	 * @param target The paste target
	 */
	public static void paste(Object target) {
		pasteFunction.accept(target);
	}
}
