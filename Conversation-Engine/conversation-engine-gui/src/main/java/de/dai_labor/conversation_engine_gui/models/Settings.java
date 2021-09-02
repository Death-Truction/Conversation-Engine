package de.dai_labor.conversation_engine_gui.models;

import java.util.prefs.Preferences;

import javax.inject.Singleton;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

@Singleton
public class Settings {

	private static final String LAST_FILE_CHOOSER_PATH = "last_file_chooser_path";
	private static final String LAST_NLP_COMPONENT_FOLDER_PATH = "nlp_component_folder_path";
	private static final String LAST_SKILL_FOLDER_PATH = "skill_folder_path";
	private static final String STATE_SIZE = "state_size";
	private static final String STATE_FONT_SIZE = "state_font_size";
	private static final String STATE_FONT_COLOR = "state_font_color";
	private static final String STATE_NORMAL_COLOR = "state_normal_color";
	private static final String STATE_SELECTED_COLOR = "state_selected_color";
	private static final String TRANSITION_SIZE = "transition_size";
	private static final String TRANSITION_FONT_SIZE = "transition_font_size";
	private static final String TRANSITION_FONT_COLOR = "transition_font_color";
	private static final String TRANSITION_NORMAL_COLOR = "transition_normal_color";
	private static final String TRANSITION_SELECTED_COLOR = "transition_selected_color";

	private String lastOpenedFile = "";
	private SimpleStringProperty lastFileChooserFolderProperty;
	private SimpleIntegerProperty stateSizeProperty;
	private SimpleIntegerProperty stateFontSizeProperty;
	private SimpleObjectProperty<Color> stateFontColorProperty;
	private SimpleObjectProperty<Color> stateNormalColorProperty;
	private SimpleObjectProperty<Color> stateSelectedColorProperty;
	private SimpleIntegerProperty transitionSizeProperty;
	private SimpleIntegerProperty transitionFontSizeProperty;
	private SimpleObjectProperty<Color> transitionFontColorProperty;
	private SimpleObjectProperty<Color> transitionNormalColorProperty;
	private SimpleObjectProperty<Color> transitionSelectedColorProperty;
	private SimpleStringProperty lastNLPComponentFolderPathProperty;
	private SimpleStringProperty lastSkillFolderPathProperty;

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

	public SimpleStringProperty getLastFileChooserFolderProperty() {
		return this.lastFileChooserFolderProperty;
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

	public SimpleIntegerProperty getTransitionSizeProperty() {
		return this.transitionSizeProperty;
	}

	public SimpleIntegerProperty getTransitionFontSizeProperty() {
		return this.transitionFontSizeProperty;
	}

	public SimpleObjectProperty<Color> getTransitionFontColorProperty() {
		return this.transitionFontColorProperty;
	}

	public SimpleObjectProperty<Color> getTransitionNormalColorProperty() {
		return this.transitionNormalColorProperty;
	}

	public SimpleObjectProperty<Color> getTransitionSelectedColorProperty() {
		return this.transitionSelectedColorProperty;
	}

	public SimpleStringProperty getLastNLPComponentFolderPathProperty() {
		return this.lastNLPComponentFolderPathProperty;
	}

	public SimpleStringProperty getLastSkillFolderPathProperty() {
		return this.lastSkillFolderPathProperty;
	}

	public void savePrefs() {
		this.prefs.put(LAST_FILE_CHOOSER_PATH, this.lastFileChooserFolderProperty.get());
		this.prefs.putInt(STATE_SIZE, this.stateSizeProperty.get());
		this.prefs.putInt(STATE_FONT_SIZE, this.stateFontSizeProperty.get());
		this.prefs.put(STATE_FONT_COLOR, this.stateFontColorProperty.get().toString());
		this.prefs.put(STATE_NORMAL_COLOR, this.stateNormalColorProperty.get().toString());
		this.prefs.put(STATE_SELECTED_COLOR, this.stateSelectedColorProperty.get().toString());
		this.prefs.putInt(TRANSITION_SIZE, this.transitionSizeProperty.get());
		this.prefs.putInt(TRANSITION_FONT_SIZE, this.transitionFontSizeProperty.get());
		this.prefs.put(TRANSITION_FONT_COLOR, this.transitionFontColorProperty.get().toString());
		this.prefs.put(TRANSITION_NORMAL_COLOR, this.transitionNormalColorProperty.get().toString());
		this.prefs.put(TRANSITION_SELECTED_COLOR, this.transitionSelectedColorProperty.get().toString());
		this.prefs.put(LAST_NLP_COMPONENT_FOLDER_PATH, this.lastNLPComponentFolderPathProperty.get());
		this.prefs.put(LAST_SKILL_FOLDER_PATH, this.lastSkillFolderPathProperty.get());
	}

	private void loadPrefs() {
		this.lastFileChooserFolderProperty = new SimpleStringProperty(this.prefs.get(LAST_FILE_CHOOSER_PATH, ""));
		this.stateSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(STATE_SIZE, 40));
		this.stateFontSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(STATE_FONT_SIZE, 12));
		this.stateFontColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_FONT_COLOR, "BLACK")));
		this.stateNormalColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_NORMAL_COLOR, "STEELBLUE")));
		this.stateSelectedColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(STATE_SELECTED_COLOR, "GREEN")));
		this.transitionSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(TRANSITION_SIZE, 12));
		this.transitionFontSizeProperty = new SimpleIntegerProperty(this.prefs.getInt(TRANSITION_FONT_SIZE, 12));
		this.transitionFontColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(TRANSITION_FONT_COLOR, "STEELBLUE")));
		this.transitionNormalColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(TRANSITION_NORMAL_COLOR, "BLACK")));
		this.transitionSelectedColorProperty = new SimpleObjectProperty<>(
				Color.valueOf(this.prefs.get(TRANSITION_SELECTED_COLOR, "GREEN")));
		this.lastNLPComponentFolderPathProperty = new SimpleStringProperty(
				this.prefs.get(LAST_NLP_COMPONENT_FOLDER_PATH, ""));
		this.lastSkillFolderPathProperty = new SimpleStringProperty(this.prefs.get(LAST_SKILL_FOLDER_PATH, ""));
	}
}
