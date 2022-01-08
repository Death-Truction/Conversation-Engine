package de.dai_labor.dialog_modeling_tool.models;

import java.io.File;

/**
 * Stores any class of a jar file and the path to the jar file
 *
 * @author Marcel Engelmann
 *
 */
public class ClassWithJarFile {
	private Class<?> myClass;
	private File jarFile;

	/**
	 * Creates a new ClassWithJarFile instance
	 *
	 * @param selectedClass The class to store
	 * @param jarFile       The path to the jar file that the selectedClass is
	 *                      originating from
	 */
	public ClassWithJarFile(Class<?> selectedClass, File jarFile) {
		this.myClass = selectedClass;
		this.jarFile = jarFile;
	}

	/**
	 * Gets the stored class.
	 *
	 * @return the stored class.
	 */
	public Class<?> getSelectedClass() {
		return this.myClass;
	}

	/**
	 * Gets the path to the jar file.
	 *
	 * @return the path to the jar file.
	 */
	public File getJarFile() {
		return this.jarFile;
	}
}
