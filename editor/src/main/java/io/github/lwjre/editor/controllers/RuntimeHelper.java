package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

/**
 * Helper class used to start processes using {@link Runtime#exec(String)}.
 * Keeps track of all running processes.
 *
 * @author Nico
 */
public class RuntimeHelper {

	/** Keeps track of all running processes */
	private static final ArrayList<RuntimeHelper> RUNNING = new ArrayList<>();

	/**
	 * Executes the given command in the current project path.
	 *
	 * @param command The command to execute
	 * @return A new {@link RuntimeHelper}
	 * @throws UncheckedIOException If an {@link IOException} occurs while running the command
	 */
	public static RuntimeHelper execute(String command) {
		try {
			return new RuntimeHelper(Runtime.getRuntime().exec(command, null, ProjectPath.current().toFile()));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Called from the main application process.
	 * Keeps track of the running processes and execute their exit function if they finished running.
	 */
	public static void process() {
		ArrayList<Runnable> queue = new ArrayList<>();
		RUNNING.removeIf(helper -> {
			if(!helper.process.isAlive()) {
				if(helper.process.exitValue() == 0) {
					queue.add(helper.onExit);
				} else {
					queue.add(helper.onError);
				}
				return true;
			}
			return false;
		});
		queue.forEach(Runnable::run);
	}

	/**
	 * Called when the application is about to terminate.
	 * Calls {@link Process#destroy()} on all processes that are still running.
	 */
	public static void terminate() {
		RUNNING.forEach(helper -> helper.process.destroy());
	}

	/** Current process */
	private final Process process;
	/** Function to execute when the process has finished running */
	private Runnable onExit = () -> {};
	/** Function to execute if the process terminates with an error */
	private Runnable onError = () -> {};

	/**
	 * Constructs a new {@code RuntimeHelper}.
	 *
	 * @param process The process to handle
	 */
	private RuntimeHelper(Process process) {
		this.process = process;
		RUNNING.add(this);
	}

	/**
	 * Sets the function to execute when this process has finished running.
	 *
	 * @param runnable The function to execute when this process has finished running
	 * @return {@code this} for method chaining
	 */
	public RuntimeHelper onExit(Runnable runnable) {
		this.onExit = runnable != null ? runnable : () -> {};
		return this;
	}

	/**
	 * Sets the function to execute if the process terminates with an error.
	 *
	 * @param runnable The function to execute if the process terminates with an error
	 * @return {@code this} for method chaining
	 */
	public RuntimeHelper onError(Runnable runnable) {
		this.onError = runnable != null ? runnable : () -> {};
		return this;
	}
}
