package de.dai_labor.conversation_engine_gui.view.simulation;

import javax.inject.Singleton;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser.ExtensionFilter;

@Singleton
public class SimulationSettingsViewModel implements ViewModel {

	private SimpleStringProperty selectedLanguageProperty = new SimpleStringProperty();
	private SimpleStringProperty conversationInputProperty = new SimpleStringProperty();
	private SimpleStringProperty skillFilePathProperty = new SimpleStringProperty();
	private SimpleStringProperty nlpComponentPathProperty = new SimpleStringProperty();
	private ObservableList<String> availableLanguages = FXCollections.observableArrayList();
	private Settings settings;
	private boolean dataHasChanged = false;

	public SimulationSettingsViewModel(Settings settings) {
		this.settings = settings;
		this.availableLanguages.add("English");
		this.availableLanguages.add("German");
		this.selectedLanguageProperty.set("German");
		this.addChangedListener(this.selectedLanguageProperty, this.conversationInputProperty,
				this.skillFilePathProperty, this.nlpComponentPathProperty);
		this.nlpComponentPathProperty.addListener(change -> {
			String path = this.nlpComponentPathProperty.get().split("\\.")[0];
			this.settings.getLastNLPComponentFolderPath().set(path);
		});
	}

	public SimpleStringProperty getSelectedLanguageProperty() {
		return this.selectedLanguageProperty;
	}

	public SimpleStringProperty getConversationInputProperty() {
		return this.conversationInputProperty;
	}

	public SimpleStringProperty getNLPComponentPathProperty() {
		return this.nlpComponentPathProperty;

	}

	public SimpleStringProperty getSkillFilePathProperty() {
		return this.skillFilePathProperty;
	}

	public void pickNLPComponentFilePath() {
		this.nlpComponentPathProperty.set(Util.fileChooser(false, new ExtensionFilter("java-class", "*.class"),
				this.settings.getLastNLPComponentFolderPath().get()));
	}

	public ObservableList<String> getAvailableLanguages() {
		return this.availableLanguages;
	}

	public void startSimulation() {
		// creates a new class and injects the required dependencies
		// the simulation stage does everything by itself
		App.easyDI.getInstance(SimulationStage.class);
	}

	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	public void setUnchanged() {
		this.dataHasChanged = false;
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("selectedLanguage", this.selectedLanguageProperty.get());
		data.put("conversationInput", this.conversationInputProperty.get());
		data.put("skillFilePath", this.skillFilePathProperty.get());
		data.put("nlpFilePath", this.nlpComponentPathProperty.get());
		return data;
	}

	public void setGUIData(JSONObject data) {
		this.selectedLanguageProperty.set(data.optString("selectedLanguage", "German"));
		this.conversationInputProperty.set(data.optString("conversationInput"));
		this.skillFilePathProperty.set(data.optString("conversationInput"));
		this.nlpComponentPathProperty.set(data.optString("nlpFilePath"));
		this.dataHasChanged = false;
	}

	public void pickSkillFilePath() {
		String filepath = Util.fileChooser(false, new ExtensionFilter("Java Class", "*.class", "*.CLASS"));
		this.skillFilePathProperty.set(filepath);
	}

	private void addChangedListener(Property... properties) {
		for (Property property : properties) {
			property.addListener(change -> this.dataHasChanged = true);
		}
	}

	public void resetData() {
		this.selectedLanguageProperty.set("");
		this.conversationInputProperty.set("");
		this.skillFilePathProperty.set("");

	}

}
