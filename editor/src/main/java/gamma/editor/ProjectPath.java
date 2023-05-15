package gamma.editor;

import java.nio.file.Path;

public final class ProjectPath {

	private static Path current = Path.of("demo");

	public static Path append(String... path) {
		return Path.of(current.toString(), path);
	}

	public static Path sourcesFolder() {
		return append("src/main/java");
	}

	public static Path resourcesFolder() {
		return append("src/main/resources");
	}

	public static Path resourcesFolder(String... path) {
		return Path.of(resourcesFolder().toString(), path);
	}
}
