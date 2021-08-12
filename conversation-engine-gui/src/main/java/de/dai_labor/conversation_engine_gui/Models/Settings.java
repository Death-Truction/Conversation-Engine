package de.dai_labor.conversation_engine_gui.models;

import javax.inject.Singleton;

@Singleton
public class Settings {

	private String lastOpenedFilePath = "";
	private String lastSavedFilePath = "";

	public Settings() {

	}

	public void setLastOpenedFilePath(String filePath) {
		this.lastOpenedFilePath = filePath;
	}

	public String getLastOpenedFilePath() {
		return this.lastOpenedFilePath;
	}

	public void setLastSavedFilePath(String filePath) {
		this.lastSavedFilePath = filePath;
	}

	public String getLastSavedFilePath() {
		return this.lastSavedFilePath;
	}
}
