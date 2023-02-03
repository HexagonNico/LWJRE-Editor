package gamma.engine.editor;

import gamma.engine.core.ApplicationListener;
import gamma.engine.core.ApplicationProperties;
import gamma.engine.core.node.Node;
import gamma.engine.core.node.SceneTree;
import gamma.engine.core.node.SubbranchLoader;
import gamma.engine.editor.view.SceneTreeTree;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.*;
import java.util.ServiceLoader;

public final class EditorCanvas extends AWTGLCanvas {

	private final ServiceLoader<ApplicationListener> modules;

	private final SceneTreeTree sceneTreeTree;
	private String sceneToLoad = "";

	public EditorCanvas(SceneTreeTree sceneTreeTree) {
		super(new GLData());
		this.modules = ServiceLoader.load(ApplicationListener.class);
		this.sceneTreeTree = sceneTreeTree;
		this.setMinimumSize(new Dimension(160, 90));
	}

	@Override
	public void initGL() {
		GL.createCapabilities();
		this.modules.forEach(ApplicationListener::onStart);
	}

	@Override
	public void paintGL() {
		if(this.sceneToLoad.isEmpty()) {
			SceneTree.process();
			this.updateEditorViewport();
			this.modules.forEach(ApplicationListener::onUpdate);
		} else {
			Node node = SubbranchLoader.load(this.sceneToLoad);
			this.sceneTreeTree.setScene(node);
			SceneTree.changeScene(node);
			this.sceneToLoad = "";
		}
		super.swapBuffers();
	}

	public void setSceneToLoad(String sceneToLoad) {
		this.sceneToLoad = sceneToLoad;
	}

	private void updateEditorViewport() {
		int width = ApplicationProperties.get("window/size/width", 160);
		int height = ApplicationProperties.get("window/size/height", 90);
		float aspect = ((float) width) / ((float) height);
		// TODO: Center viewport
		if(this.getWidth() > this.getHeight()) {
			height = (int) (this.getWidth() * (1.0f / aspect));
			GL11.glViewport(0, 0, this.getWidth(), height);
		} else {
			width = (int) (this.getHeight() * aspect);
			GL11.glViewport(0, 0, width, this.getHeight());
		}
	}
}
