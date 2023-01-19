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
		this.canvas.setMinimumSize(new Dimension(320, 180));
		this.canvas.setPreferredSize(new Dimension(960, 540));
		JTabbedPane pane = new JTabbedPane();
		pane.addTab("Scene tree", new JPanel());
		pane.addTab("File system", new EditorFileSystem());
		pane.setMinimumSize(new Dimension(0, 0));
		JTabbedPane pane2 = new JTabbedPane();
		pane2.addTab("Inspector", new JPanel());
		pane2.setMinimumSize(new Dimension(120, 0));
		pane2.setMinimumSize(new Dimension(0, 0));
		JTabbedPane pane3 = new JTabbedPane();
		pane3.addTab("Terminal", new JPanel());
		pane3.setMinimumSize(new Dimension(0, 0));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), pane3);
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane, splitPane);
		JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane2, pane2);
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem("New"));
		fileMenu.add(new JMenuItem("Open"));
		fileMenu.add(new JMenuItem("Save"));
		fileMenu.add(new JMenuItem("Save as"));
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new JMenuItem("Cut"));
		editMenu.add(new JMenuItem("Copy"));
		editMenu.add(new JMenuItem("Paste"));
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		this.frame.setJMenuBar(menuBar);
		this.frame.setContentPane(splitPane3);
		this.frame.pack();
		this.frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Editor());
	}
}
