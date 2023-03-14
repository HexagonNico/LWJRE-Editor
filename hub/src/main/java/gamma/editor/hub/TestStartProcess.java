package gamma.editor.hub;

import java.io.File;
import java.io.IOException;

public class TestStartProcess {

	public static void main(String[] args) {
		try {
			Process process = new ProcessBuilder("java", "-cp", "./*:./src/main/resources", "gamma.engine.editor.EditorApplication")
					.directory(new File("run"))
					.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
