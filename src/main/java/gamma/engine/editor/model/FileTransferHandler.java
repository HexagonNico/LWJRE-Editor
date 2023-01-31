package gamma.engine.editor.model;

import gamma.engine.editor.view.FileSystemTree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileTransferHandler extends TransferHandler {

	@Override
	protected Transferable createTransferable(JComponent component) {
		JTree tree = (JTree) component;
		List<Path> files = new ArrayList<>();
		for(TreePath path : Objects.requireNonNull(tree.getSelectionPaths())) {
			files.add(Path.of(
					Arrays.stream(path.getPath())
							.map(Object::toString)
							.collect(Collectors.joining(File.separator)))
			);
		}
		return new FileTransferable(files);
	}

	@Override
	public boolean canImport(JComponent component, DataFlavor[] flavors) {
		return Arrays.asList(flavors).contains(DataFlavor.javaFileListFlavor);
	}

	@Override
	public boolean importData(JComponent component, Transferable transferable) {
		if(component instanceof FileSystemTree tree) {
			Path selected = tree.getSelectedPath();
			try {
				List<?> toTransfer = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				Path destination = Files.isDirectory(selected) ? selected : selected.getParent();
				for(Object path : toTransfer) {
					Files.move(((Path) path), Path.of(destination.toString(), ((Path) path).getFileName().toString()));
				}
				tree.refresh();
				return true;
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public int getSourceActions(JComponent component) {
		return MOVE;
	}

	private record FileTransferable(List<Path> files) implements Transferable {

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{DataFlavor.javaFileListFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(DataFlavor.javaFileListFlavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return files;
		}
	}
}
