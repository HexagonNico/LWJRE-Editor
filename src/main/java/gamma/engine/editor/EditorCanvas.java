package gamma.engine.editor;

import gamma.engine.core.tree.SceneTree;
import gamma.engine.graphics.Graphics;
import gamma.engine.graphics.RenderingSystem3D;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.*;

public final class EditorCanvas extends AWTGLCanvas {

	private static final EditorCanvas SINGLETON = new EditorCanvas();

	public static EditorCanvas instance() {
		return SINGLETON;
	}

	private String sceneToLoad = "";

	private EditorCanvas() {
		super(new GLData());
		this.setPreferredSize(new Dimension(640, 360));
	}

	@Override
	public void initGL() {
		GL.createCapabilities();
		GL11.glClearColor(0.1f, 0.4f, 0.9f, 1.0f);
	}

	@Override
	public void paintGL() {
		if(this.sceneToLoad.isEmpty()) {
			SceneTree.process();
			Graphics.clearFramebuffer();
			RenderingSystem3D.renderingProcess();
		} else {
			SceneTree.loadScene(this.sceneToLoad);
			this.sceneToLoad = "";
		}
		super.swapBuffers();
	}

	public void setSceneToLoad(String sceneToLoad) {
		this.sceneToLoad = sceneToLoad;
	}
}
