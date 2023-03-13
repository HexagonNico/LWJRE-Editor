package gamma.engine.editor;

import gamma.engine.core.ApplicationListener;
import gamma.engine.core.scene.Scene;
import org.lwjgl.glfw.GLFW;

import java.util.ServiceLoader;

public class EditorApplication {

	public static void main(String[] args) {
		if (GLFW.glfwInit()) {
			ServiceLoader<ApplicationListener> listeners = ServiceLoader.load(ApplicationListener.class);
			try {
				System.out.println("Editor started");
				EditorWindow window = new EditorWindow();
				window.makeContextCurrent();
				window.show();
				listeners.forEach(ApplicationListener::onStart);
//				Scene.changeScene(ApplicationProperties.getString("startScene"));
				while(!window.isCloseRequested()) {
					listeners.forEach(ApplicationListener::onUpdate);
					Scene.getCurrent().root.editorProcess();
					window.update();
					GLFW.glfwPollEvents();
				}
				window.destroy();
			} catch (Exception e) {
				System.err.println("Uncaught exception in editor");
				e.printStackTrace();
			} finally {
				System.out.println("Terminating editor");
				listeners.forEach(ApplicationListener::onTerminate);
				GLFW.glfwTerminate();
			}
		} else {
			System.err.println("Unable to initialize GLFW");
		}
	}
}
