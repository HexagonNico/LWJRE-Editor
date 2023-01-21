package gamma.engine.editor.panels;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemPanel extends JPanel {

	private static final FileSystemPanel SINGLETON = new FileSystemPanel();

	public static FileSystemPanel instance() {
		return SINGLETON;
	}

	private final JTree fileSystemTree = new JTree(new FileTreeModel());

	private FileSystemPanel() {
		this.setLayout(new BorderLayout());
		this.fileSystemTree.setDragEnabled(true);
		this.fileSystemTree.setTransferHandler(new FileTransferHandler());
		this.fileSystemTree.addMouseListener(new TreeMouseListener());
		JScrollPane scrollPane = new JScrollPane(this.fileSystemTree);
		this.add(BorderLayout.CENTER, scrollPane);
	}

	private FileNode selectedNode() {
		return (FileNode) this.fileSystemTree.getLastSelectedPathComponent();
	}

	public void createFolder(String name) {
		try {
			FileNode selected = this.selectedNode();
			String directory = selected == null ? "src/main/resources" : Files.isDirectory(selected.path()) ? selected.path().toString() : selected.path.getParent().toString();
			Files.createDirectories(Path.of(directory, name));
			this.refresh();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSelectedPath() {
		FileNode selected = this.selectedNode();
		if(selected == null)
			return "";
		return selected.path().toString();
	}

	public void deleteSelected() {
		try(Stream<Path> paths = Files.walk(this.selectedNode().path())) {
			paths.sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			this.refresh();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void refresh() {
		this.fileSystemTree.setModel(new FileTreeModel());
		for(int i = 0; i < this.fileSystemTree.getRowCount(); i++) {
			this.fileSystemTree.expandRow(i);
		}
	}

	private static class FileTreeModel implements TreeModel {

		@Override
		public Object getRoot() {
			return new FileNode(Path.of("src/main/resources"));
		}

		@Override
		public Object getChild(Object parent, int index) {
			try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
				return files.map(FileNode::new).sorted().toList().get(index);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int getChildCount(Object parent) {
			try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
				return files.map(FileNode::new).sorted().toList().size();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean isLeaf(Object node) {
			return !Files.isDirectory(((FileNode) node).path());
		}

		@Override
		public void valueForPathChanged(TreePath treePath, Object o) {

		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			try(Stream<Path> files = Files.list(((FileNode) parent).path())) {
				return files.map(FileNode::new).sorted().toList().indexOf((FileNode) child);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void addTreeModelListener(TreeModelListener treeModelListener) {

		}

		@Override
		public void removeTreeModelListener(TreeModelListener treeModelListener) {

		}
	}

	private record FileNode(Path path) implements Comparable<FileNode> {

		@Override
		public String toString() {
			return this.path().toString().equals("src/main/resources") ? "src/main/resources" : this.path.getFileName().toString();
		}

		@Override
		public int compareTo(FileNode fileNode) {
			return Files.isDirectory(this.path()) == Files.isDirectory(fileNode.path()) ? this.path().getFileName().toString().compareToIgnoreCase(fileNode.path.getFileName().toString()) : Files.isDirectory(this.path()) ? -1 : 1;
		}
	}

	private static class FileTransferHandler extends TransferHandler {

		@Override
		protected Transferable createTransferable(JComponent component) {
			JTree tree = (JTree) component;
			List<Path> files = new ArrayList<>();
			for(TreePath path : Objects.requireNonNull(tree.getSelectionPaths())) {
				files.add(Path.of(Arrays.stream(path.getPath()).map(Object::toString).collect(Collectors.joining(File.separator))));
			}
			return new FileTransferable(files);
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return Arrays.asList(transferFlavors).contains(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			FileNode selected = (FileNode) ((JTree) comp).getLastSelectedPathComponent();
			try {
				List<?> toTransfer = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
				Path destination = Files.isDirectory(selected.path()) ? selected.path() : selected.path().getParent();
				for(Object path : toTransfer) {
					Files.move(((Path) path), Path.of(destination.toString(), ((Path) path).getFileName().toString()));
				}
				FileSystemPanel.instance().refresh();
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}
	}

	private record FileTransferable(List<Path> files) implements Transferable {

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{DataFlavor.javaFileListFlavor};
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(DataFlavor.javaFileListFlavor);
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return files;
		}
	}

	private static class TreeMouseListener extends MouseInputAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				FileNode selected = FileSystemPanel.instance().selectedNode();
				boolean isSelected = selected != null && !selected.path().equals(Path.of("src/main/resources"));
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.add(new JMenuItem(new AbstractAction("New folder") {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						JTextField folderNameTextField = new JTextField("NewFolder");
						if(JOptionPane.showConfirmDialog(null, new Object[] {"Folder name", folderNameTextField}, "Create new folder", JOptionPane.DEFAULT_OPTION) != -1) {
							FileSystemPanel.instance().createFolder(folderNameTextField.getText());
						}
					}
				}));
				if(isSelected) {
					popupMenu.add(new JMenuItem(new AbstractAction("Delete") {
						@Override
						public void actionPerformed(ActionEvent actionEvent) {
							String path = FileSystemPanel.instance().getSelectedPath();
							if(JOptionPane.showConfirmDialog(null, new Object[] {"Delete \"" + path + "\"?"}, "Confirm delete", JOptionPane.OK_CANCEL_OPTION) == 0) {
								FileSystemPanel.instance().deleteSelected();
							}
						}
					}));
				}
				popupMenu.show(FileSystemPanel.instance(), e.getX(), e.getY());
			}
		}
	}
}
