package gamma.engine.editor;

import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class Editor implements Runnable {

	private final JFrame frame;
	private final EditorCanvas canvas;

	private Editor() {
		if(!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		this.frame = new JFrame("Gamma Engine - Editor");
		this.canvas = new EditorCanvas();
	}

	@Override
	public void run() {
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canvas.destroy();
				frame.dispose();
				GLFW.glfwTerminate();
			}
		});
		this.frame.setLayout(new BorderLayout());
		this.canvas.setSize(640, 480);
		this.frame.add(this.canvas, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Editor());
	}
}
