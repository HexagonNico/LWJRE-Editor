package gamma.engine.editor;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EditorFileSystem extends JPanel {

	public EditorFileSystem() {
		this.setLayout(new BorderLayout());
		JTree tree = new JTree(new FileTreeModel());
		tree.setDragEnabled(true);
		tree.setTransferHandler(new FileTransferHandler());
		JScrollPane scrollPane = new JScrollPane(tree);
		this.add(BorderLayout.CENTER, scrollPane);
	}

	private static class FileTreeModel implements TreeModel {

		@Override
		public Object getRoot() {
			return new FileNode(new File("src/main/resources"));
		}

		@Override
		public Object getChild(Object parent, int index) {
			return Arrays.stream(Objects.requireNonNull(((FileNode) parent).file.listFiles())).map(FileNode::new).sorted().toArray()[index];
		}

		@Override
		public int getChildCount(Object parent) {
			return Objects.requireNonNull(((FileNode) parent).file.listFiles()).length;
		}

		@Override
		public boolean isLeaf(Object node) {
			return !((FileNode) node).file.isDirectory();
		}

		@Override
		public void valueForPathChanged(TreePath treePath, Object o) {

		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			return Arrays.stream(Objects.requireNonNull(((FileNode) parent).file.listFiles())).map(FileNode::new).sorted().toList().indexOf(((FileNode) child));
		}

		@Override
		public void addTreeModelListener(TreeModelListener treeModelListener) {

		}

		@Override
		public void removeTreeModelListener(TreeModelListener treeModelListener) {

		}
	}

	private record FileNode(File file) implements Comparable<FileNode> {

		@Override
		public String toString() {
			return file.toString().equals("src/main/resources") ? "src/main/resources" : file.getName();
		}

		@Override
		public int compareTo(FileNode fileNode) {
			return this.file.isDirectory() == fileNode.file.isDirectory() ? this.file.getName().compareToIgnoreCase(fileNode.file.getName()) : this.file.isDirectory() ? -1 : 1;
		}
	}

	private static class FileTransferHandler extends TransferHandler {

		@Override
		protected Transferable createTransferable(JComponent c) {
			JTree list = (JTree) c;
			java.util.List<File> files = new ArrayList<>();
			for (TreePath path : Objects.requireNonNull(list.getSelectionPaths())) {
				files.add(new File(Arrays.stream(path.getPath()).map(Object::toString).collect(Collectors.joining(File.separator))));
			}
			return new FileTransferable(files);
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return Arrays.asList(transferFlavors).contains(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			FileNode destination = (FileNode) ((JTree) comp).getLastSelectedPathComponent();
			try {
				List<File> files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
				File directory = destination.file.isDirectory() ? destination.file : destination.file.getParentFile();
				for(File file : files) {
					file.renameTo(new File(directory.getAbsolutePath() + File.separator + file.getName()));
				}
				((JTree) comp).setModel(new FileTreeModel());
				for(int i = 0; i < ((JTree) comp).getRowCount(); i++) {
					((JTree) comp).expandRow(i);
				}
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

	private record FileTransferable(List<File> files) implements Transferable {

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
