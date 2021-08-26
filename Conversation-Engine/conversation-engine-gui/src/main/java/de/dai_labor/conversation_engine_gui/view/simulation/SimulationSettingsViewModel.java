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
	private ObservableList<String> availableLanguages = FXCollections.observableArrayList();
	private Settings settings;
	private boolean dataHasChanged = false;

	public SimulationSettingsViewModel(Settings settings) {
		this.settings = settings;
		this.availableLanguages.add("English");
		this.availableLanguages.add("German");
		this.selectedLanguageProperty.set("German");
	}

	public SimpleStringProperty getSelectedLanguageProperty() {
		return this.selectedLanguageProperty;
	}

	public SimpleStringProperty getConversationInputProperty() {
		return this.conversationInputProperty;
	}

	public SimpleStringProperty getNLPComponentPathProperty() {
		return this.settings.getNLPComponentPathProperty();
	}

	public void pickNLPComponentFilePath() {
		this.settings.getNLPComponentPathProperty()
				.set(Util.fileChooser(false, new ExtensionFilter("java-class", "*.class")));
	}

	public ObservableList<String> getAvailableLanguages() {
		return this.availableLanguages;
	}

	public void startSimulation() {
		SimulationStage simulationStage = App.easyDI.getInstance(SimulationStage.class);
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
		return data;
	}

	public void setGUIData(JSONObject data) {
		this.selectedLanguageProperty.set(data.optString("selectedLanguage", "German"));
		this.conversationInputProperty.set(data.optString("conversationInput"));
		this.dataHasChanged = false;
	}

	private void addChangedListener(Property... properties) {
		for (Property property : properties) {
			this.dataHasChanged = true;
		}
	}

}