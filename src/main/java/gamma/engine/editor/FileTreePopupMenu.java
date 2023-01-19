package gamma.engine.editor;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FileTreePopupMenu extends JPopupMenu {

	public FileTreePopupMenu(boolean selected) {
		this.add(new JMenuItem(new AbstractAction("New folder") {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				JTextField folderNameTextField = new JTextField("NewFolder");
				if(JOptionPane.showConfirmDialog(null, new Object[] {"Folder name", folderNameTextField}, "Create new folder", JOptionPane.DEFAULT_OPTION) != -1) {
					FileTree tree = (FileTree) FileTreePopupMenu.this.getInvoker();
					tree.createFolder(folderNameTextField.getText());
				}
			}
		}));
		if(selected) {
			this.add(new JMenuItem(new AbstractAction("Delete") {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					FileTree tree = (FileTree) FileTreePopupMenu.this.getInvoker();
					String path = tree.getSelectedPath();
					if(JOptionPane.showConfirmDialog(null, new Object[] {"Delete \"" + path + "\"?"}, "Confirm delete", JOptionPane.OK_CANCEL_OPTION) == 0) {
						tree.deleteSelected();
					}
				}
			}));
		}
	}
}
