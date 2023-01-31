package gamma.engine.editor.controller;

import gamma.engine.editor.view.FileSystemTree;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileTreeMouseListener extends MouseInputAdapter {

	private final FileSystemTree fileSystemTree;

	public FileTreeMouseListener(FileSystemTree fileSystemTree) {
		this.fileSystemTree = fileSystemTree;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if(SwingUtilities.isRightMouseButton(event)) {
			boolean selected = this.fileSystemTree.getSelectionCount() > 0;
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add(new JMenuItem(new AbstractAction("New folder") {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					JTextField folderNameTextField = new JTextField("folder");
					if(JOptionPane.showConfirmDialog(null, new Object[] {"Folder name", folderNameTextField}, "Create new folder", JOptionPane.DEFAULT_OPTION) != -1) {
						try {
							String selectedPath = fileSystemTree.getSelectedPath().toString();
							Files.createDirectory(Path.of(selectedPath, folderNameTextField.getText()));
							fileSystemTree.refresh();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}));
			if(selected) {
				popupMenu.add(new JMenuItem(new AbstractAction("Delete") {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						Path pathToDelete = fileSystemTree.getSelectedPath();
						if(JOptionPane.showConfirmDialog(null, new Object[] {"Delete \"" + pathToDelete + "\"?"}, "Confirm delete", JOptionPane.OK_CANCEL_OPTION) == 0) {
							try(Stream<Path> paths = Files.walk((pathToDelete))) {
								paths.sorted(Comparator.reverseOrder()).forEach(path -> {
									try {
										Files.delete(path);
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								});
								fileSystemTree.refresh();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}));
			}
			popupMenu.show(this.fileSystemTree, event.getX(), event.getY());
		}
	}
}
