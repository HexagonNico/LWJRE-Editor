package io.github.lwjre.editor.controllers;

import io.github.lwjre.editor.ProjectPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

public class RuntimeHelper {

	// TODO: Check if it is possible without keeping all the running processes

	private static final ArrayList<RuntimeHelper> RUNNING = new ArrayList<>();

	public static RuntimeHelper execute(String command) {
		try {
			return new RuntimeHelper(Runtime.getRuntime().exec(command, null, ProjectPath.current().toFile()));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

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

	public static void terminate() {
		RUNNING.forEach(helper -> helper.process.destroy());
	}

	private final Process process;
	private Runnable onExit = () -> {};
	private Runnable onError = () -> {};

	public RuntimeHelper(Process process) {
		this.process = process;
		RUNNING.add(this);
	}

	public RuntimeHelper onExit(Runnable runnable) {
		this.onExit = runnable != null ? runnable : () -> {};
		return this;
	}

	public RuntimeHelper onError(Runnable runnable) {
		this.onError = runnable != null ? runnable : () -> {};
		return this;
	}
}
