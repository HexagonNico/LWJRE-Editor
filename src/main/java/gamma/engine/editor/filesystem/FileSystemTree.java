package gamma.engine.editor.filesystem;

import gamma.engine.core.scene.Scene;
import gamma.engine.core.utils.YamlParser;
import gamma.engine.editor.TreeView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FileSystemTree extends TreeView implements MouseListener {

	public FileSystemTree() {
		super(new FileTreeModel());
		this.setShowsRootHandles(true);
		this.setDragEnabled(true);
		this.setTransferHandler(new FileTransferHandler());
		this.addMouseListener(this);
	}

	@Override
	protected void refreshView() {
		super.refreshView(new FileTreeModel());
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		if(mouseEvent.getClickCount() == 2) {
			String file = ((FileNode) this.getLastSelectedPathComponent()).file().getPath();
			Scene scene = YamlParser.loadFile(file, Scene.class);
			Scene.changeScene(scene);
			TreeView.refreshAll();
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {

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
}
