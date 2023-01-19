package gamma.engine.editor;

import javax.swing.*;
import java.awt.*;

public class EditorFileSystem extends JPanel {

	public EditorFileSystem() {
		this.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(new FileTree());
		this.add(BorderLayout.CENTER, scrollPane);
	}
}
