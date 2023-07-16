package io.github.lwjre.editor;

/**
 * Interface used to represent a state of the editor application.
 *
 * @author Nico
 */
public interface ApplicationState {

	/**
	 * Called when the application starts or when the current state is changed to this one.
	 */
	void init();

	/**
	 * Called every frame while the application is in this state.
	 */
	void process();

	/**
	 * Called when the application is terminated or the current state is changed.
	 */
	void terminate();
}
