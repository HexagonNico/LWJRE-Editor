package gamma.editor;

import gamma.editor.gui.IGui;
import gamma.editor.gui.ProjectsGui;
import gamma.engine.EditorListener;
import gamma.engine.rendering.FrameBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ServiceLoader;

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

	public static FrameBuffer sceneFrameBuffer() {
		return instance.sceneFrameBuffer;
	}

	private final EditorWindow window;
	private IGui rootGui;

	public final FrameBuffer sceneFrameBuffer;

	private String currentPath = "";

	private EditorApplication() {
		this.window = new EditorWindow();
		this.window.makeContextCurrent();
		this.rootGui = new ProjectsGui();
		this.sceneFrameBuffer = new FrameBuffer(1920, 1080);
	}

	@Override
	public void run() {
		ServiceLoader<EditorListener> services = ServiceLoader.load(EditorListener.class);
		services.forEach(EditorListener::onEditorInit);
		while(!this.window.isCloseRequested()) {
			FrameBuffer.bind(this.sceneFrameBuffer);
			services.forEach(EditorListener::onEditorProcess);
			FrameBuffer.unbind(); // TODO: Clean up this code
			GL11.glViewport(0, 0, 1920, 1080);
			this.window.renderGui(this.rootGui);
			this.window.update();
			GLFW.glfwPollEvents();
		}
		this.window.destroy();
		services.forEach(EditorListener::onEditorTerminate);
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
			GLFW.glfwTerminate();
		} else {
			System.err.println("Unable to initialize GLFW");
		}
	}
}
