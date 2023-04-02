package gamma.editor;

import gamma.editor.gui.EditorGui;
import gamma.engine.resources.DeletableResource;
import gamma.engine.scene.Scene;
import org.lwjgl.glfw.GLFW;

public class EditorApplication {

	public static void main(String[] args) {
		if (GLFW.glfwInit()) {
			Thread.currentThread().setContextClassLoader(new EditorClassLoader());
			try {
				System.out.println("Editor started");
				EditorWindow window = new EditorWindow();
				window.setupCallbacks();
				window.makeContextCurrent();
				EditorGui editorGui = new EditorGui();
				// TODO: Change to previously opened scene
				while(!window.isCloseRequested()) {
					Scene.editorProcess();
					window.renderGui(editorGui);
					window.update();
					GLFW.glfwPollEvents();
				}
				window.destroy();
			} catch (Exception e) {
				System.err.println("Uncaught exception in editor");
				e.printStackTrace();
			} finally {
				System.out.println("Terminating editor");
				DeletableResource.deleteAll();
				GLFW.glfwTerminate();
			}
		} else {
			System.err.println("Unable to initialize GLFW");
		}
	}
}
