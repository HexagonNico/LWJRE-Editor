package gamma.engine.editor.scene;

import gamma.engine.editor.inspector.InspectorPanel;

import javax.swing.*;
import java.awt.*;

public class ScenePanel extends JScrollPane {

	public ScenePanel(InspectorPanel inspectorPanel) {
		super(new SceneTree(inspectorPanel));
		this.setMinimumSize(new Dimension(180, 0));
	}
}
