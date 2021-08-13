package de.dai_labor.conversation_engine_gui.models;

import java.util.prefs.Preferences;

import javax.inject.Singleton;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

@Singleton
public class Settings {

	private static final String LAST_FILE_CHOOSER_PATH = "last_file_chooser_path";
	private static final String STATE_SIZE = "state_size";
	private static final String STATE_FONT_SIZE = "state_font_size";
	private static final String STATE_FONT_COLOR = "state_font_color";
	private static final String STATE_NORMAL_COLOR = "state_normal_color";
	private static final String STATE_SELECTED_COLOR = "state_selected_color";

	private String lastFileChooserPath;
	private String lastOpenedFile = "";
	private SimpleIntegerProperty stateSizeProperty;
	private SimpleIntegerProperty stateFontSizeProperty;
	private SimpleObjectProperty<Color> stateFontColorProperty;
	private SimpleObjectProperty<Color> stateNormalColorProperty;
	private SimpleObjectProperty<Color> stateSelectedColorProperty;

	Preferences prefs;

	public Settings() {
		this.prefs = Preferences.userNodeForPackage(Settings.class);
		this.loadPrefs();
	}

	public String getLastOpenedFile() {
		return this.lastOpenedFile;
	}

	public void setLastOpenedFile(String filePath) {
		this.lastOpenedFile = filePath;
	}

	public String getLastFileChooserPath() {
		return this.lastFileChooserPath;
	}

	public void setLastFileChooserPath(String filePath) {
		this.lastOpenedFile = filePath;
	}

	public SimpleIntegerProperty getStateSizeProperty() {
		return this.stateSizeProperty;
	}

	public SimpleIntegerProperty getStateFontSizeProperty() {
		return this.stateFontSizeProperty;
	}

	public SimpleObjectProperty<Color> getStateFontColorProperty() {
		return this.stateFontColorProperty;
	}

	public SimpleObjectProperty<Color> getStateNormalColorProperty() {
		return this.stateNormalColorProperty;
	}

	public SimpleObjectProperty<Color> getStateSelectedColorProperty() {
		return this.stateSelectedColorProperty;
	}

	public void savePrefs() {
		this.prefs.put(LAST_FILE_CHOOSER_PATH, this.lastFileChooserPath);
		this.prefs.putInt(STATE_SIZE, this.stateSizeProperty.get());
		this.prefs.put(STATE_NORMAL_COLOR, this.stateNormalColorProperty.get().toString());
		// TODO: save all values
	}

	private void loadPrefs() {
		this.lastFileChooserPath = this.prefs.get(LAST_FILE_CHOOSER_PATH, "");
		this.stateSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(STATE_SIZE, 40));
		this.stateFontSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(STATE_FONT_SIZE, 12));
		this.stateFontColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_FONT_COLOR, "WHITE")));
		this.stateNormalColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_NORMAL_COLOR, "STEELBLUE")));
		this.stateSelectedColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_SELECTED_COLOR, "GREEN")));
	}

}
