package gamma.editor.core;

import gamma.engine.core.rendering.DebugRenderer;
import gamma.engine.core.rendering.RenderingSystem;
import gamma.engine.core.resources.DeletableResource;
import gamma.engine.core.scene.Scene;
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
				// TODO: Change to previously opened scene
				while(!window.isCloseRequested()) {
					Scene.getCurrent().root.editorProcess();
					RenderingSystem.render();
					DebugRenderer.render();
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
