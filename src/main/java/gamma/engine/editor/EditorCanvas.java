package gamma.engine.editor;

import gamma.engine.core.Module;
import gamma.engine.core.tree.SceneTree;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.*;
import java.util.ServiceLoader;

public final class EditorCanvas extends AWTGLCanvas {

	private static final EditorCanvas SINGLETON = new EditorCanvas();

	public static EditorCanvas instance() {
		return SINGLETON;
	}

	private final ServiceLoader<Module> modules;
	private String sceneToLoad = "";

	private EditorCanvas() {
		super(new GLData());
		this.modules = ServiceLoader.load(Module.class);
		// TODO: Canvas size and resizing
		this.setPreferredSize(new Dimension(640, 360));
	}

	@Override
	public void initGL() {
		GL.createCapabilities();
		this.modules.forEach(Module::onStart);
	}

	@Override
	public void paintGL() {
		if(this.sceneToLoad.isEmpty()) {
			SceneTree.process();
			this.modules.forEach(Module::onUpdate);
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
