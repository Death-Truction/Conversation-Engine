package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;

@Singleton
public class DialogueDataViewModel implements ViewModel {

	private ObservableMap<Integer, State> allStates;
	private SimpleStringProperty skillNameProperty = new SimpleStringProperty();
	private SimpleStringProperty intentsProperty = new SimpleStringProperty();
	private SimpleStringProperty entitiesProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedStartStateProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedEndStateProperty = new SimpleStringProperty();
	private ObservableList<String> availableStates = FXCollections.observableArrayList();
	private boolean dataHasChanged = false;

	public DialogueDataViewModel(DialogueViewModel dialogueViewModel) {
		this.allStates = dialogueViewModel.getStates();
		this.updateAvailableStates(null);
		this.allStates.addListener((MapChangeListener.Change<? extends Integer, ? extends State> change) -> {
			this.updateAvailableStates(null);
		});
		this.selectedStartStateProperty.addListener(change -> {
			if (this.selectedStartStateProperty.get() == null) {
				return;
			}
			dialogueViewModel.setStartState(this.selectedStartStateProperty.get());
			if (this.selectedEndStateProperty.get() != null
					&& this.selectedEndStateProperty.get().equals(this.selectedStartStateProperty.get())) {
				this.selectedEndStateProperty.set(null);
			}
		});
		this.selectedEndStateProperty.addListener(change -> {
			if (this.selectedEndStateProperty.get() == null) {
				return;
			}
			dialogueViewModel.setEndState(this.selectedEndStateProperty.get());
			if (this.selectedStartStateProperty.get() != null
					&& this.selectedStartStateProperty.get().equals(this.selectedEndStateProperty.get())) {
				this.selectedStartStateProperty.set(null);
			}
		});
		this.addChangedListener(this.intentsProperty, this.entitiesProperty, this.selectedEndStateProperty,
				this.selectedStartStateProperty, this.skillNameProperty);
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

	public SimpleStringProperty getSkillNameProperty() {
		return this.skillNameProperty;
	}

	public ObservableList<String> getAvailableState() {
		return this.availableStates;
	}

	public void updateAvailableStates(MouseEvent e) {
		List<String> allStateNames = new ArrayList<>();
		for (State state : this.allStates.values()) {
			allStateNames.add(state.getName());
		}
		this.availableStates.retainAll(allStateNames);
		allStateNames.removeAll(this.availableStates);
		this.availableStates.addAll(allStateNames);
	}

	public void resetData() {
		this.skillNameProperty.set("");
		this.intentsProperty.set("");
		this.entitiesProperty.set("");
		this.selectedStartStateProperty.set("");
		this.selectedEndStateProperty.set("");
		this.dataHasChanged = false;
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("skillName", this.skillNameProperty.get());
		data.put("intents", this.intentsProperty.get());
		data.put("entities", this.entitiesProperty.get());
		data.put("startState", this.selectedStartStateProperty.get());
		data.put("endState", this.selectedEndStateProperty.get());
		return data;
	}

	public void setGUIData(JSONObject data) {
		this.skillNameProperty.set(data.optString("skillName"));
		this.intentsProperty.set(data.optString("intents", ""));
		this.entitiesProperty.set(data.optString("entities", ""));
		this.selectedStartStateProperty.set(data.optString("startState", ""));
		this.selectedEndStateProperty.set(data.optString("endState", ""));
		this.dataHasChanged = false;
	}

	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	private void addChangedListener(Property... properties) {
		for (Property property : properties) {
			property.addListener(change -> this.dataHasChanged = true);
		}
	}

	public void setUnchanged() {
		this.dataHasChanged = false;

	}
}
