package gamma.editor.controls;

import gamma.engine.scene.EntityResource;

/**
 * Class responsible for renaming an entity in the scene tree gui.
 *
 * @author Nico
 */
public class EntityRenamer {

	/** The entity's parent is needed because only the parent knows its children's names */
	private EntityResource parent;
	/** The entity that should be renamed */
	private EntityResource entity;
	/** The entity's old name is needed to retrieve the entity from its parent */
	private String oldName;
	/** The entity's new name */
	private String newName;

	/**
	 * Sets the entity that should be renamed.
	 *
	 * @param entity The entity to rename
	 * @param parent The entity's parent
	 */
	public void setEntity(EntityResource entity, EntityResource parent) {
		this.parent = parent;
		this.entity = entity;
	}

	/**
	 * Sets the entity's new name.
	 *
	 * @param newName The entity's new name
	 * @param oldName The entity's old name
	 */
	public void setName(String newName, String oldName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	/**
	 * Stops the renaming. Notifies that the renaming has been canceled.
	 */
	public void stopRenaming() {
		this.setEntity(null, null);
		this.setName(null, null);
	}

	/**
	 * Checks if the given entity is the one being renamed.
	 *
	 * @param entity The entity to check
	 * @return True if the given entity is the one being renamed, otherwise false
	 */
	public boolean isRenaming(EntityResource entity) {
		return this.entity == entity;
	}

	/**
	 * Renames the entity if {@link EntityRenamer#setEntity(EntityResource, EntityResource)} and {@link EntityRenamer#setName(String, String)} have been called.
	 * This method does nothing if there is no entity being renamed or if the entity's parent already contains an entity with said name.
	 * Calls {@link EntityRenamer#stopRenaming()} after the renaming has happened.
	 */
	public void performRename() {
		if(this.parent != null && this.entity != null && this.oldName != null && this.newName != null) {
			if(this.parent.children.containsKey(this.newName)) {
				this.parent.children.remove(this.oldName);
				this.parent.children.put(this.newName, this.entity);
				this.stopRenaming();
			}
		}
	}
}
