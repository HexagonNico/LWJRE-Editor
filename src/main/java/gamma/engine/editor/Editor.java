package gamma.engine.editor;

import gamma.engine.core.scene.Scene;
import gamma.engine.core.utils.YamlParser;
import gamma.engine.editor.filesystem.FileSystemPanel;
import gamma.engine.editor.inspector.InspectorPanel;
import gamma.engine.editor.scene.ScenePanel;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;

public final class Editor implements Runnable {

	private final EditorCanvas editorCanvas = new EditorCanvas();

	private Editor() {
		JFrame frame = new JFrame("Gamma Engine - Editor");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JTabbedPane westPane = new JTabbedPane();
		InspectorPanel inspectorPanel = new InspectorPanel();
		westPane.addTab("Scene", new ScenePanel(inspectorPanel));
		westPane.addTab("File system", new FileSystemPanel());
		JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(BorderLayout.CENTER, this.editorCanvas);
		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPane, canvasPanel);
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, inspectorPanel);
		frame.setContentPane(splitPane2);
		frame.setMinimumSize(new Dimension(360, 180));
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void run() {
		if(!this.editorCanvas.isValid()) {
			GL.setCapabilities(null);
			System.out.println(YamlParser.serialize(Scene.getCurrent()));
			return;
		}
		this.editorCanvas.render();
		SwingUtilities.invokeLater(this);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Editor());
	}
}
