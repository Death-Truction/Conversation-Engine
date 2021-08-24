package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser.ExtensionFilter;

@Singleton
public class DialogueDataViewModel implements ViewModel {

	private DialogueViewModel dialogueViewModel;
	private SimpleStringProperty skillNameProperty = new SimpleStringProperty();
	private SimpleStringProperty skillFilePathProperty = new SimpleStringProperty();
	private SimpleStringProperty intentsProperty = new SimpleStringProperty();
	private SimpleStringProperty entitiesProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedStartStateProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedEndStateProperty = new SimpleStringProperty();
	private ObservableList<String> availableStates = FXCollections.observableArrayList();
	private boolean hasDataChanged = false;

	public DialogueDataViewModel(DialogueViewModel dialogueViewModel) {
		this.dialogueViewModel = dialogueViewModel;
		this.updateAvailableStates(null);
		this.dialogueViewModel.getStates()
				.addListener((MapChangeListener.Change<? extends Integer, ? extends State> change) -> {
					this.updateAvailableStates(null);
				});
		this.selectedStartStateProperty.addListener(change -> {
			dialogueViewModel.setStartState(this.selectedStartStateProperty.get());
			if (this.selectedEndStateProperty.get() != null
					&& this.selectedEndStateProperty.get().equals(this.selectedStartStateProperty.get())) {
				this.selectedEndStateProperty.set(null);
			}
		});
		this.selectedEndStateProperty.addListener(change -> {
			dialogueViewModel.setEndState(this.selectedEndStateProperty.get());
			if (this.selectedStartStateProperty.get() != null
					&& this.selectedStartStateProperty.get().equals(this.selectedEndStateProperty.get())) {
				this.selectedStartStateProperty.set(null);
			}
		});
		this.addChangedListener(this.skillNameProperty, this.skillFilePathProperty, this.intentsProperty,
				this.entitiesProperty, this.selectedEndStateProperty, this.selectedStartStateProperty);
	}

	public SimpleStringProperty getSkillFilePathProperty() {
		return this.skillFilePathProperty;
	}

	public SimpleStringProperty getSkillNameProperty() {
		return this.skillNameProperty;
	}

	public SimpleStringProperty getIntentsProperty() {
		return this.intentsProperty;
	}

	public SimpleStringProperty getEntitiesProperty() {
		return this.entitiesProperty;
	}

	public SimpleStringProperty getSelectedStartStateProperty() {
		return this.selectedStartStateProperty;
	}

	public SimpleStringProperty getSelectedEndStateProperty() {
		return this.selectedEndStateProperty;
	}

	public ObservableList<String> getAvailableState() {
		return this.availableStates;
	}

	public void updateAvailableStates(MouseEvent e) {
		List<String> allStates = new ArrayList<>();
		for (State state : this.dialogueViewModel.getStates().values()) {
			allStates.add(state.getName());
		}
		this.availableStates.retainAll(allStates);
		allStates.removeAll(this.availableStates);
		this.availableStates.addAll(allStates);
	}

	public void pickSkillFilePath(MouseEvent e) {
		String filepath = Util.fileChooser(false, new ExtensionFilter("Java Class", "*.class", "*.CLASS"));
		this.skillFilePathProperty.set(filepath);

		if (this.skillNameProperty.get() == null || this.skillNameProperty.get().isBlank()) {
			String filename = new File(filepath).getName();
			if (filename.indexOf(".") > -1) {
				filename = filename.substring(0, filename.lastIndexOf("."));
			}
			this.skillNameProperty.set(filename);
		}
	}

	public void resetData() {
		this.skillNameProperty.set("");
		this.skillFilePathProperty.set("");
		this.intentsProperty.set("");
		this.entitiesProperty.set("");
		this.selectedStartStateProperty.set("");
		this.selectedEndStateProperty.set("");
		this.hasDataChanged = false;
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("skillName", this.skillNameProperty.get());
		data.put("skillFilePath", this.skillFilePathProperty.get());
		data.put("intents", this.intentsProperty.get());
		data.put("entities", this.entitiesProperty.get());
		data.put("startState", this.selectedStartStateProperty.get());
		data.put("endState", this.selectedEndStateProperty.get());
		return data;
	}

	public void setGUIData(JSONObject data) {
		this.skillNameProperty.set(data.optString("skillName", ""));
		this.skillFilePathProperty.set(data.optString("skillFilePath", ""));
		this.intentsProperty.set(data.optString("intents", ""));
		this.entitiesProperty.set(data.optString("entities", ""));
		this.selectedStartStateProperty.set(data.optString("startState", ""));
		this.selectedEndStateProperty.set(data.optString("endState", ""));
		this.hasDataChanged = false;
	}

	public boolean hasChanged() {
		return this.hasDataChanged;
	}

	private void addChangedListener(Property... properties) {
		for (Property property : properties) {
			property.addListener(change -> this.hasDataChanged = true);
		}
	}

	public void setUnchanged() {
		this.hasDataChanged = false;

	}
}
