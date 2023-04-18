package gamma.editor;

import gamma.editor.gui.IGui;
import gamma.editor.gui.ProjectsGui;
import gamma.engine.rendering.DeletableResource;
import gamma.engine.scene.Scene;
import org.lwjgl.glfw.GLFW;

public final class EditorApplication implements Runnable {

	private static EditorApplication instance;

	public static void setGui(IGui gui) {
		if(instance != null && gui != null) {
			instance.rootGui = gui;
		}
	}

	public static void setCurrentPath(String path) {
		if(instance != null && path != null) {
			instance.currentPath = path;
		}
	}

	public static String currentPath() {
		return instance != null ? instance.currentPath : "";
	}

	private final EditorWindow window;
	private IGui rootGui;

	private String currentPath = "";

	private EditorApplication() {
		this.window = new EditorWindow();
	}

	@Override
	public void run() {
		this.window.makeContextCurrent();
		this.rootGui = new ProjectsGui();
		while(!this.window.isCloseRequested()) {
			Scene.editorProcess();
			this.window.renderGui(this.rootGui);
			this.window.update();
			GLFW.glfwPollEvents();
		}
		this.window.destroy();
	}

	public static void main(String[] args) {
		if(GLFW.glfwInit()) try {
			System.out.println("Editor started");
			Thread.currentThread().setContextClassLoader(new EditorClassLoader());
			instance = new EditorApplication();
			instance.run();
		} catch(Exception any) {
			System.err.println("Uncaught exception in editor");
			any.printStackTrace();
		} finally {
			System.out.println("Terminating editor");
			DeletableResource.deleteAll(); // TODO: Move in another class
			GLFW.glfwTerminate();
		} else {
			System.err.println("Unable to initialize GLFW");
		}
	}
}
