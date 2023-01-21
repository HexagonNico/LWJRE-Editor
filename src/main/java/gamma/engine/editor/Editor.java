package gamma.engine.editor;

import gamma.engine.editor.panels.FileSystemPanel;
import gamma.engine.editor.panels.SceneTreePanel;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;

public final class Editor implements Runnable {

	private final EditorCanvas canvas;

	private Editor() {
		JFrame frame = new JFrame("Gamma Engine - Editor");
		this.canvas = new EditorCanvas();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.canvas.setPreferredSize(new Dimension(640, 360));
		JTabbedPane pane = new JTabbedPane();
		pane.addTab("Scene tree", SceneTreePanel.instance());
		pane.addTab("File system", FileSystemPanel.instance());
		pane.setMinimumSize(new Dimension(120, 0));
		JTabbedPane pane2 = new JTabbedPane();
		pane2.addTab("Inspector", new JPanel());
		pane2.setMinimumSize(new Dimension(120, 0));
		pane2.setMinimumSize(new Dimension(0, 0));
		JTabbedPane pane3 = new JTabbedPane();
		pane3.addTab("Terminal", new JPanel());
		pane3.setMinimumSize(new Dimension(0, 0));
		JPanel canvasPanel = new JPanel();
		canvasPanel.add(this.canvas);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, canvasPanel, pane3);
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
		frame.setJMenuBar(menuBar);
		frame.setContentPane(splitPane3);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void run() {
		if(!this.canvas.isValid()) {
			GL.setCapabilities(null);
			System.out.println("Invalid");
			return;
		}
		this.canvas.render();
		SwingUtilities.invokeLater(this);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Editor());
	}
}
