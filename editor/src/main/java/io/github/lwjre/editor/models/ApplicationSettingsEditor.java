package io.github.lwjre.editor.models;

import io.github.lwjre.editor.ProjectPath;
import io.github.lwjre.engine.utils.YamlParser;
import io.github.lwjre.engine.utils.YamlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Class used to read and write the application settings to the {@code settings.yaml} file.
 *
 * @author Nico
 */
public class ApplicationSettingsEditor {

	/** Map containing the application settings */
	private final LinkedHashMap<String, Map<String, Object>> settings = new LinkedHashMap<>();

	/**
	 * Reads the application settings from the settings file.
	 * The default settings are read first, then the project's settings are read and overwritten over the default ones.
	 */
	public void read() {
		this.read("default_settings.yaml");
		this.read("settings.yaml");
	}

	/**
	 * Reads the application settings from the file at the given path in the classpath.
	 *
	 * @param resource Path to the file in the classpath
	 */
	private void read(String resource) {
		((Map<?, ?>) YamlParser.parseResource(resource)).forEach((sectionKey, values) -> {
			this.settings.putIfAbsent(sectionKey.toString(), new LinkedHashMap<>());
			Map<String, Object> section = this.settings.get(sectionKey.toString());
			((Map<?, ?>) values).forEach((key, value) -> section.put(key.toString(), value));
		});
	}

	/**
	 * Performs the given action for each section of the application settings.
	 * The action is executed using {@link Iterable#forEach(Consumer)} over {@link Map#keySet()}.
	 *
	 * @param consumer The action to be performed for each element
	 */
	public void forEachSection(Consumer<String> consumer) {
		this.settings.keySet().forEach(consumer);
	}

	/**
	 * Performs the given action for each entry of the given section.
	 * The action is executed using {@link Map#forEach(BiConsumer)} for the given section.
	 *
	 * @param section Name of the section
	 * @param consumer The action to be performed for each entry
	 */
	public void forEachValue(String section, BiConsumer<String, Object> consumer) {
		if(this.settings.containsKey(section)) {
			this.settings.get(section).forEach(consumer);
		} else {
			throw new NoSuchElementException("There is no section " + section);
		}
	}

	/**
	 * Gets a value from the application settings.
	 *
	 * @param section Name of the section
	 * @param key The value's key
	 * @return The requested value
	 * @throws NoSuchElementException If there is no value associated to the given key and section
	 */
	public Object get(String section, String key) {
		if(this.settings.containsKey(section)) {
			if(this.settings.get(section).containsKey(key)) {
				return this.settings.get(section).get(key);
			}
			throw new NoSuchElementException("Section " + section + " does not have an element with key " + key);
		}
		throw new NoSuchElementException("There is no section " + section);
	}

	/**
	 * Puts the given value in the application settings.
	 *
	 * @param section Name of the section
	 * @param key The value's key
	 * @param value Value to put in the settings
	 */
	public void put(String section, String key, Object value) {
		this.settings.putIfAbsent(section, new LinkedHashMap<>());
		this.settings.get(section).put(key, value);
	}

	/**
	 * Saves the application settings to {@code src/main/resources/settings.yaml}.
	 */
	public void save() {
		try {
			Files.write(ProjectPath.resourcesFolder("settings.yaml"), YamlSerializer.serialize(this.settings).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
