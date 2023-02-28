package gamma.engine.editor.filesystem;

import javax.swing.*;
import java.awt.*;

public class FileSystemPanel extends JScrollPane {

	public FileSystemPanel() {
		super(new FileSystemTree());
		this.setMinimumSize(new Dimension(180, 0));
	}
}
