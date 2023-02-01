package gamma.engine.editor.view;

import gamma.engine.editor.EditorCanvas;
import gamma.engine.editor.controller.FileTreeMouseListener;
import gamma.engine.editor.model.FileTransferHandler;
import gamma.engine.editor.model.FileTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSystemTree extends JTree {

	public FileSystemTree(EditorCanvas editorCanvas) {
		super(new FileTreeModel());
		this.setShowsRootHandles(true);
		this.setDragEnabled(true);
		this.setTransferHandler(new FileTransferHandler());
		this.addMouseListener(new FileTreeMouseListener(this, editorCanvas));
	}

	public Path getSelectedPath() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
		return selectedNode != null ? (Path) selectedNode.getUserObject() : Path.of("src/main/resources");
	}

	public void refresh() {
		List<TreePath> expanded = new ArrayList<>();
		for(int i = 0; i < this.getRowCount() - 1; i++) {
			if(this.isExpanded(i)) {
				expanded.add(this.getPathForRow(i));
			}
		}
		this.setModel(new FileTreeModel());
		for(TreePath path : expanded) {
			this.expandPath(path);
		}
	}
}
