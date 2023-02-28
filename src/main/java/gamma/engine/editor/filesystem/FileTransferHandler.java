package gamma.engine.editor.filesystem;

import gamma.engine.editor.TreeView;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileTransferHandler extends TransferHandler {

	@Override
	protected Transferable createTransferable(JComponent component) {
		JTree tree = (JTree) component;
		TreePath[] paths = tree.getSelectionPaths();
		if(paths == null)
			return null;
		File[] files = Arrays.stream(paths)
				.map(path -> Arrays.stream(path.getPath())
						.map(Object::toString)
						.collect(Collectors.joining(File.separator)))
				.map(File::new)
				.toArray(File[]::new);
		return new Transferable() {

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] {DataFlavor.javaFileListFlavor};
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
				return dataFlavor.equals(DataFlavor.javaFileListFlavor);
			}

			@Override
			public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
				if (!isDataFlavorSupported(dataFlavor)) {
					throw new UnsupportedFlavorException(dataFlavor);
				}
				return files;
			}
		};
	}

	@Override
	public boolean canImport(JComponent component, DataFlavor[] flavors) {
		return Arrays.asList(flavors).contains(DataFlavor.javaFileListFlavor);
	}

	@Override
	public boolean importData(JComponent component, Transferable transferable) {
		try {
			File file = ((FileNode) ((JTree) component).getLastSelectedPathComponent()).file();
			File destination = file.isDirectory() ? file : file.getParentFile();
			for(File fileToMove : (File[]) transferable.getTransferData(DataFlavor.javaFileListFlavor)) {
				if(!fileToMove.renameTo(new File(destination + File.separator + fileToMove.getName())))
					return false;
			}
			TreeView.refreshAll();
			return true;
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}
}
