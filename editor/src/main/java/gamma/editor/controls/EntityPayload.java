package gamma.editor.controls;

import gamma.engine.scene.EntityResource;

/**
 * Class used to represent a payload object used for ImGui drag and drop.
 *
 * @author Nico
 */
public final class EntityPayload {

	/** The entity in the payload */
	public final EntityResource entity;
	/** The entity's parent is needed because the entity needs to be removed from its previous parent */
	public final EntityResource parent;
	/** The entity's name is kept after moving it */
	public final String name;

	/** The entity's new parent is set on the drop */
	private EntityResource newParent;

	/**
	 * Constructs the entity payload.
	 *
	 * @param entity The entity in the payload
	 * @param parent Its parent
	 * @param name The entity's name
	 */
	public EntityPayload(EntityResource entity, EntityResource parent, String name) {
		this.entity = entity;
		this.parent = parent;
		this.name = name;
	}

	/**
	 * Setter method. Called when an entity has been dragged onto a new parent.
	 *
	 * @param entity The entity on which the one in the payload has been dragged.
	 */
	public void setNewParent(EntityResource entity) {
		this.newParent = entity;
	}

	/**
	 * Performs the drop operation. Must be called when not iterating through entities to avoid concurrent modification.
	 * Moves the entity in the payload to its new parent if it has been set.
	 * The entity won't be moved if it has been dragged onto one of its children or grandchildren.
	 */
	public void performDrop() {
		if(this.newParent != null && !checkChildren(this.entity, this.newParent)) {
			this.parent.children.remove(this.name);
			this.newParent.children.put(this.name, this.entity);
		}
	}

	/**
	 * Checks if the given entity is not a parent of its new parent.
	 *
	 * @param entity The entity to check
	 * @param newParent The new parent
	 * @return True if {@code newParent} is a child or grandchild of {@code entity}, otherwise false
	 */
	private static boolean checkChildren(EntityResource entity, EntityResource newParent) {
		for(EntityResource child : entity.children.values()) {
			if(checkChildren(child, newParent) || child == newParent)
				return true;
		}
		return false;
	}
}
