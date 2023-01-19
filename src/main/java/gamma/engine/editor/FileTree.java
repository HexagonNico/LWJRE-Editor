package gamma.engine.editor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTree extends JTree {

	public FileTree() {
		super(new FileTreeModel());
		this.setDragEnabled(true);
		this.setTransferHandler(new FileTransferHandler());
		this.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					FileNode selected = (FileNode) FileTree.this.getLastSelectedPathComponent();
					boolean isSelected = selected != null && !selected.path().equals(Path.of("src/main/resources"));
					new FileTreePopupMenu(isSelected).show(FileTree.this, e.getX(), e.getY());
				}
			}
		});
	}

	public void createFolder(String name) {
		try {
			FileNode selected = (FileNode) this.getLastSelectedPathComponent();
			String directory = selected == null ? "src/main/resources" : Files.isDirectory(selected.path()) ? selected.path().toString() : selected.path.getParent().toString();
			Files.createDirectories(Path.of(directory, name));
			this.refresh();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSelectedPath() {
		FileNode selected = (FileNode) this.getLastSelectedPathComponent();
		if(selected == null)
			return "";
		return selected.path().toString();
	}

	public void deleteSelected() {
		try(Stream<Path> paths = Files.walk(((FileNode) this.getLastSelectedPathComponent()).path())) {
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
		this.setModel(new FileTreeModel());
		for(int i = 0; i < this.getRowCount(); i++) {
			this.expandRow(i);
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
		protected Transferable createTransferable(JComponent c) {
			JTree list = (JTree) c;
			List<Path> files = new ArrayList<>();
			for (TreePath path : Objects.requireNonNull(list.getSelectionPaths())) {
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
				List<Path> toTransfer = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
				Path destination = Files.isDirectory(selected.path()) ? selected.path() : selected.path().getParent();
				for(Path path : toTransfer) {
					Files.move(path, Path.of(destination.toString(), path.getFileName().toString()));
				}
				((FileTree) comp).refresh();
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
}
