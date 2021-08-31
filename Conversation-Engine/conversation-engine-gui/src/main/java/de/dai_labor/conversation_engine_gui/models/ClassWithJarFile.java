package de.dai_labor.conversation_engine_gui.models;

import java.io.File;

public class ClassWithJarFile {
	private Class<?> myClass;
	private File jarFile;

	public ClassWithJarFile(Class<?> selectedClass, File jarFile) {
		this.myClass = selectedClass;
		this.jarFile = jarFile;
	}

	public Class<?> getSelectedClass() {
		return this.myClass;
	}

	public File getJarFile() {
		return this.jarFile;
	}
}
