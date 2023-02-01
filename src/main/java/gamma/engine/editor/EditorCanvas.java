package gamma.engine.editor;

import gamma.engine.core.Module;
import gamma.engine.core.node.Node;
import gamma.engine.core.node.SceneTree;
import gamma.engine.core.node.SubbranchLoader;
import gamma.engine.editor.view.SceneTreeTree;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.*;
import java.util.ServiceLoader;

public final class EditorCanvas extends AWTGLCanvas {

	private final ServiceLoader<Module> modules;

	private final SceneTreeTree sceneTreeTree;
	private String sceneToLoad = "";

	public EditorCanvas(SceneTreeTree sceneTreeTree) {
		super(new GLData());
		this.modules = ServiceLoader.load(Module.class);
		this.sceneTreeTree = sceneTreeTree;
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
			Node node = SubbranchLoader.load(this.sceneToLoad);
			System.out.println(node + " -> " + node.getClass());
			this.sceneTreeTree.setScene(node);
			SceneTree.changeScene(node);
			this.sceneToLoad = "";
		}
		super.swapBuffers();
	}

	public void setSceneToLoad(String sceneToLoad) {
		this.sceneToLoad = sceneToLoad;
	}
}
