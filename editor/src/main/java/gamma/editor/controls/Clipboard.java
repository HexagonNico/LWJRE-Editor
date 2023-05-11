package gamma.editor.controls;

import java.util.function.Supplier;

public final class Clipboard {

	private static Object content;
	private static Supplier<Object> onPaste = () -> null;

	public static void setContent(Object content) {
		setContent(content, () -> null);
	}

	public static void setContent(Object content, Supplier<Object> onPaste) {
		Clipboard.content = content;
		Clipboard.onPaste = onPaste;
	}

	public static Object getContent() {
		return content;
	}

	public static Object notifyPaste() {
		Object result = onPaste.get();
		onPaste = () -> null;
		return result;
	}
}
