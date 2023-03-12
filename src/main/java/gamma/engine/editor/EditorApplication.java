package gamma.engine.editor;

import gamma.engine.core.scene.Scene;
import org.lwjgl.glfw.GLFW;

public class EditorApplication {

	public static void main(String[] args) {
		if(GLFW.glfwInit()) {
			EditorWindow editorWindow = new EditorWindow();
			editorWindow.makeContextCurrent();
			editorWindow.show();
			while(!editorWindow.isCloseRequested()) {
				editorWindow.update();
				Scene.getCurrent().root.editorProcess();
				GLFW.glfwPollEvents();
			}
			editorWindow.destroy();
			GLFW.glfwTerminate();
		} else {
			System.err.println("Could not initialize GLFW");
		}
	}

}
