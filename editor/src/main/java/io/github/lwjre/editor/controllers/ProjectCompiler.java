package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.editor.gui.BasicPopup;
import io.github.lwjre.editor.gui.InspectorWindow;
import io.github.lwjre.editor.gui.NewScenePopup;
import io.github.lwjre.editor.gui.SceneTreeWindow;
import io.github.lwjre.editor.models.EditorClassLoader;
import io.github.lwjre.editor.models.FileSystemListener;
import io.github.lwjre.editor.utils.ClassFilesLoader;

import java.io.IOException;
import java.util.HashSet;

/**
 * Listens to changes in the project's files and recompile the project if a change is detected.
 *
 * @author Nico
 */
public class ProjectCompiler {

	/** Listens for changes in the project's files and reloads the project when a change is detected */
	private final FileSystemListener fileSystemListener = new FileSystemListener(this::reloadProject);
	/** A reference to the calling thread is needed to update the thread's class loader */
	private final Thread mainThread = Thread.currentThread();
	/** Thread that uses the {@link FileSystemListener#listen} method */
	private final Thread listenerThread = new Thread(this.fileSystemListener::listen);

	/** Needs to receive the newly loaded node classes */
	private final SceneTreeWindow sceneTreeWindow;
	/** Needs to be cleared when the project is recompiled */
	private final InspectorWindow inspectorWindow;
	/** Needs to receive the newly loaded node classes */
	private final NewScenePopup newScenePopup;
	/** Shown when the project is being reloaded */
	private final BasicPopup compilingProjectPopup;

	/** Prevents the project from being reloaded again if it is already being reloaded */
	private boolean reloading = false;

	/**
	 * Constructs the project compiler.
	 *
	 * @param sceneTreeWindow Needs to receive the newly loaded node classes when the project is recompiled
	 * @param inspectorWindow Needs to be cleared when the project is recompiled
	 * @param newScenePopup Needs to receive the newly loaded node classes when the project is recompiled
	 * @param compilingProjectPopup Shown when the project is being reloaded
	 */
	public ProjectCompiler(SceneTreeWindow sceneTreeWindow, InspectorWindow inspectorWindow, NewScenePopup newScenePopup, BasicPopup compilingProjectPopup) {
		this.sceneTreeWindow = sceneTreeWindow;
		this.inspectorWindow = inspectorWindow;
		this.newScenePopup = newScenePopup;
		this.compilingProjectPopup = compilingProjectPopup;
	}

	/**
	 * Initializes the project by running {@code mvn install -DskipTests} and reloading dependencies and project classes.
	 * Shows the popup when the reloading starts and closes it when it is finished.
	 * This method does nothing if the project is already being reloaded.
	 */
	public void init() {
		if(!this.reloading) try {
			this.compilingProjectPopup.setTitle("Compiling project");
			this.compilingProjectPopup.setContent("mvn clean install -DskipTests");
			this.compilingProjectPopup.open();
			this.reloading = true;
			Runtime.getRuntime().exec("mvn clean install -DskipTests", null, ProjectPath.current().toFile()).onExit().thenRun(() -> {
				this.compilingProjectPopup.setContent("Looking for classes");
				HashSet<String> nodeClasses = ClassFilesLoader.lookForClasses();
				this.sceneTreeWindow.setNodeClasses(nodeClasses);
				this.newScenePopup.setNodeClasses(nodeClasses);
				this.inspectorWindow.setNode(null);
				EditorClassLoader classLoader = new EditorClassLoader();
				this.mainThread.setContextClassLoader(classLoader);
				this.listenerThread.setContextClassLoader(classLoader);
				this.listenerThread.start();
				this.compilingProjectPopup.close();
				this.reloading = false;
			});
		} catch (IOException e) {
			e.printStackTrace();
			// TODO; Error popup
			this.compilingProjectPopup.close();
			this.reloading = false;
		}
	}

	/**
	 * Reloads the project's classes by running {@code mvn clean install -DskipTests} and loading the class files.
	 * Shows the popup when the reloading starts and closes it when it is finished.
	 * This method does nothing if the project is already being reloaded.
	 */
	private void reloadProject() {
		if(!this.reloading) try {
			this.compilingProjectPopup.setTitle("Compiling project");
			this.compilingProjectPopup.setContent("mvn clean install -DskipTests");
			this.compilingProjectPopup.open();
			this.reloading = true;
			Runtime.getRuntime().exec("mvn clean install -DskipTests", null, ProjectPath.current().toFile()).onExit().thenRun(() -> {
				this.compilingProjectPopup.setContent("Looking for classes");
				HashSet<String> nodeClasses = ClassFilesLoader.lookForClasses();
				this.compilingProjectPopup.close();
				this.sceneTreeWindow.setNodeClasses(nodeClasses);
				this.newScenePopup.setNodeClasses(nodeClasses);
				this.inspectorWindow.setNode(null);
				EditorClassLoader classLoader = new EditorClassLoader();
				this.mainThread.setContextClassLoader(classLoader);
				Thread.currentThread().setContextClassLoader(classLoader);
				EditorScene.reload();
				this.reloading = false;
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads the project's classes by running {@code mvn clean install -DskipTests} and loading classes from the dependencies.
	 * Shows the popup when the reloading starts and closes it when it is finished.
	 * This method does nothing if the project is already being reloaded.
	 */
	private void reloadDependencies() {
		if(!this.reloading) try {
			this.compilingProjectPopup.setTitle("Compiling project");
			this.compilingProjectPopup.setContent("mvn clean install -DskipTests");
			this.compilingProjectPopup.open();
			this.reloading = true;
			Runtime.getRuntime().exec("mvn clean install -DskipTests", null, ProjectPath.current().toFile()).onExit().thenRun(() -> {
				this.compilingProjectPopup.setContent("Looking for classes");
				HashSet<String> nodeClasses = ClassFilesLoader.lookForClasses();
				this.compilingProjectPopup.close();
				this.sceneTreeWindow.setNodeClasses(nodeClasses);
				this.newScenePopup.setNodeClasses(nodeClasses);
				this.inspectorWindow.setNode(null);
				EditorClassLoader classLoader = new EditorClassLoader();
				this.mainThread.setContextClassLoader(classLoader);
				Thread.currentThread().setContextClassLoader(classLoader);
				EditorScene.reload();
				this.reloading = false;
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the thread handling the {@link FileSystemListener}.
	 * Must be called when the editor is being closed.
	 */
	public void terminate() {
		try {
			this.fileSystemListener.stopListening();
			this.listenerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
