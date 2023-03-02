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
import java.awt.event.*;
import java.util.ServiceLoader;

public final class EditorCanvas extends AWTGLCanvas implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final ServiceLoader<ApplicationListener> modules;

	private final EditorCamera camera = new EditorCamera();
	private Point dragPoint = new Point(0, 0);
	private int mouseButton = MouseEvent.NOBUTTON;

	public EditorCanvas() {
		super(new GLData());
		this.modules = ServiceLoader.load(ApplicationListener.class);
		this.setMinimumSize(new Dimension(160, 90));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
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

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {

	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		this.dragPoint = mouseEvent.getPoint();
		this.mouseButton = mouseEvent.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		float dragX = (this.dragPoint.x - mouseEvent.getX()) * 0.01f;
		float dragY = (mouseEvent.getY() - this.dragPoint.y) * 0.01f;
		if(this.mouseButton == MouseEvent.BUTTON1) {
			this.camera.position = this.camera.position.plus(dragX, dragY, 0.0f);
		} else if(this.mouseButton == MouseEvent.BUTTON3) {
			this.camera.pitch -= dragX;
			this.camera.yaw += dragY;
		}
		this.dragPoint = mouseEvent.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
		this.camera.position = this.camera.position.plus(this.camera.forwardDirection().multipliedBy(-mouseWheelEvent.getWheelRotation()));
	}
}
