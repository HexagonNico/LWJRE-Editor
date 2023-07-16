package io.github.lwjre.editor.models;

import io.github.lwjre.editor.ProjectPath;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Implementation of a {@link WatchService} that listens for changes in the file system.
 *
 * @author Nico
 */
public class FileSystemListener {

	/** Action to perform */
	private final Runnable onEvent;
	/** True when running, false when stopped */
	private boolean listening = true;

	/**
	 * Constructs a {@code FileSystemListener}.
	 *
	 * @param onEvent Action to perform when a change in the file system is detected
	 */
	public FileSystemListener(Runnable onEvent) {
		this.onEvent = onEvent;
	}

	/**
	 * Starts listening for changes to the file system.
	 * This method is supposed to be called on a separate thread, since it enters an infinite loop that terminates when {@link FileSystemListener#stopListening()} is called.
	 */
	public void listen() {
		try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
			Files.walkFileTree(ProjectPath.sourcesFolder(), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
					directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}
			});
			while(this.listening) {
				WatchKey watchKey = watchService.poll();
				if(watchKey != null) {
					for(WatchEvent<?> event : watchKey.pollEvents()) {
						if(event.context().toString().endsWith(".java")) {
							this.onEvent.run();
							break;
						}
					}
					watchKey.reset();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.listening = true;
	}

	/**
	 * Must be called after {@link FileSystemListener#listen()} to stop the thread that is running this listener.
	 */
	public void stopListening() {
		this.listening = false;
	}
}
