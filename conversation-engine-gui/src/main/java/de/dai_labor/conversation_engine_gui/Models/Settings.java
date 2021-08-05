package de.dai_labor.conversation_engine_gui.models;

import javax.inject.Singleton;

@Singleton
public class Settings {

	private String openedFilePath = "";

	public Settings() {

	}

	public void setOpenedFilePath(String filePath) {
		this.openedFilePath = filePath;
	}

	public String getOpenedFilePath() {
		return this.openedFilePath;
	}
}
