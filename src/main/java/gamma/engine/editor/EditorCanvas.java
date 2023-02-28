package gamma.engine.editor;

import gamma.engine.core.ApplicationListener;
import gamma.engine.core.ApplicationProperties;
import gamma.engine.core.scene.Scene;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;
import vecmatlib.vector.Vec2i;

import java.awt.*;
import java.util.ServiceLoader;

public final class EditorCanvas extends AWTGLCanvas {

	private final ServiceLoader<ApplicationListener> modules;

	private final EditorCamera camera = new EditorCamera();

	public EditorCanvas() {
		super(new GLData());
		this.modules = ServiceLoader.load(ApplicationListener.class);
		this.setMinimumSize(new Dimension(160, 90));
	}

	@Override
	public void initGL() {
		GL.createCapabilities();
		this.camera.makeCurrent();
		this.modules.forEach(ApplicationListener::onStart);
	}

	@Override
	public void paintGL() {
		this.modules.forEach(ApplicationListener::onUpdate);
		this.camera.viewport = this.updateEditorViewport();
		Scene.getCurrent().root.editorProcess();
		super.swapBuffers();
	}

	private Vec2i updateEditorViewport() {
		int width = ApplicationProperties.get("window/size/width", 160);
		int height = ApplicationProperties.get("window/size/height", 90);
		float aspect = ((float) width) / ((float) height);
		// TODO: Center viewport
		if(this.getWidth() > this.getHeight()) {
			height = (int) (this.getWidth() * (1.0f / aspect));
			GL11.glViewport(0, 0, this.getWidth(), height);
			return new Vec2i(this.getWidth(), height);
		} else {
			width = (int) (this.getHeight() * aspect);
			GL11.glViewport(0, 0, width, this.getHeight());
			return new Vec2i(width, this.getHeight());
		}
	}
}
