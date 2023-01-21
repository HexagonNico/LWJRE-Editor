package gamma.engine.editor;

import gamma.engine.core.tree.SceneTree;
import gamma.engine.editor.panels.SceneTreePanel;
import gamma.engine.graphics.Graphics;
import gamma.engine.graphics.RenderingSystem3D;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

public final class EditorCanvas extends AWTGLCanvas {

	public EditorCanvas() {
		super(new GLData());
	}

	@Override
	public void initGL() {
		GL.createCapabilities();
		GL11.glClearColor(0.1f, 0.4f, 0.9f, 1.0f);
		SceneTreePanel.instance().loadScene("/test_scene.yaml");
	}

	@Override
	public void paintGL() {
		SceneTree.process();
		Graphics.clearFramebuffer();
		RenderingSystem3D.renderingProcess();
		swapBuffers();
	}
}
