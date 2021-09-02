package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.interfaces.IStorableGuiData;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.main.MainView;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;

@Singleton
public class DialogueViewModel implements ViewModel, IStorableGuiData {
	private static final int DIAGRAM_ELEMENT_LAYER_SIZE = 50000;
	private final DialoguePane dialoguePane;
	private final SimpleObjectProperty<Node> viewProperty = new SimpleObjectProperty<>();
	private final Pane dialogueElementsLayer = new Pane();
	private final SimpleStringProperty insertMode = new SimpleStringProperty("");
	private SimpleObjectProperty<State> selectedState = new SimpleObjectProperty<>();
	private SimpleObjectProperty<Transition> selectedTransition = new SimpleObjectProperty<>();
	private final Settings settings;
	private Toggle toggleButton;
	private final ObservableMap<Integer, State> states = FXCollections.observableHashMap();
	private final List<Transition> transitions = new ArrayList<>();
	private boolean dataHasChanged = false;
	private State startState;
	private State endState;

	public DialogueViewModel(Settings settings, MainView mainView) {
		this.settings = settings;
		this.dialoguePane = new DialoguePane(this.dialogueElementsLayer, this.insertMode, this.selectedState,
				this.selectedTransition, this::addState, this::addTransition, this::removeState,
				this::removeTransition);
		Platform.runLater(() -> {
			this.dialogueElementsLayer.setMinWidth(DIAGRAM_ELEMENT_LAYER_SIZE);
			this.dialogueElementsLayer.setMinHeight(DIAGRAM_ELEMENT_LAYER_SIZE);
			this.dialogueElementsLayer.relocate(DIAGRAM_ELEMENT_LAYER_SIZE / -2.0, DIAGRAM_ELEMENT_LAYER_SIZE / -2.0);
		});
		this.dialoguePane.toBack();
		this.subscribe("unload", (ignore, ignore2) -> this.dialoguePane.unselectAll());
		this.dialoguePane.toBack();
		this.viewProperty.set(this.dialoguePane);
	}

	public SimpleObjectProperty<Node> getViewProperty() {
		return this.viewProperty;
	}

	public DialoguePane getView() {
		return this.dialoguePane;
	}

	public void centerElements() {
		this.dialoguePane.centerMovingElement();
	}

	public void setMode(Toggle toggleButton) {
		this.toggleButton = toggleButton;
		if (toggleButton == null) {
			this.insertMode.set("");
		} else {
			this.insertMode.set(toggleButton.getUserData().toString());
		}
	}

	public void addState(double locationX, double locationY) {
		// center location to mousePointer
		locationX -= this.settings.getStateSizeProperty().get();
		locationY -= this.settings.getStateSizeProperty().get();
		int emptyStateId = this.getFirstEmptyStateId();
		this.addState(emptyStateId, "State" + (emptyStateId + 1), locationX, locationY, true);
	}

	public void addState(int id, String name, double locationX, double locationY, boolean focusRequest) {
		final State newState = new State(id, name, locationX, locationY, this.settings, this.selectedState,
				focusRequest);
		newState.getTextArea().textProperty().addListener((s, y, z) -> this.dataHasChanged = true);
		this.states.put(id, newState);
		this.dataHasChanged = true;
		this.dialogueElementsLayer.getChildren().add(newState);
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	public ObservableMap<Integer, State> getStates() {
		return this.states;
	}

	public State getStartState() {
		return this.startState;
	}

	public void setStartState(String selectedItem) {
		State newStartState = this.getStateByName(selectedItem);
		if (this.startState != newStartState && newStartState != null) {
			if (this.startState != null) {
				this.startState.setNormalState();
			}
			newStartState.setStartState();
			this.startState = newStartState;
			if (this.startState == this.endState) {
				this.endState = null;
			}
		}

	}

	public State getEndState() {
		return this.endState;
	}

	public void setEndState(String selectedItem) {
		State newEndState = this.getStateByName(selectedItem);
		if (this.endState != newEndState && newEndState != null) {
			if (this.endState != null) {
				this.endState.setNormalState();
			}
			newEndState.setEndState();
			this.endState = newEndState;
			if (this.startState == this.endState) {
				this.startState = null;
			}
		}

	}

	public void removeState(State state) {
		for (final Iterator<Transition> iterator = this.transitions.iterator(); iterator.hasNext();) {
			final Transition transition = iterator.next();
			if (transition.getSource().equals(state) || transition.getTarget().equals(state)) {
				iterator.remove();
				this.dialogueElementsLayer.getChildren().remove(transition);
			}
		}
		this.dialogueElementsLayer.getChildren().remove(this.states.remove(state.getStateId()));
		if (this.startState != null && this.startState.equals(state)) {
			this.startState = null;
		} else if (this.endState != null && this.endState.equals(state)) {
			this.endState = null;
		}
		this.dataHasChanged = true;
	}

	public void removeTransition(Transition transition) {
		this.dialogueElementsLayer.getChildren().remove(transition);
		this.transitions.remove(transition);
		this.dataHasChanged = true;
	}

	public void addTransition(State source, State target) {
		this.addTransition(source, target, this.getFirstEmptyTransitionName(), true);
	}

	public void addTransition(State source, State target, String trigger, boolean focusRequest) {
		if (!this.transitionExists(source, target)) {
			final Transition newTransition = new Transition(source, target, trigger, this.selectedTransition,
					focusRequest, this.settings);
			newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.dataHasChanged = true);
			this.transitions.add(newTransition);
			this.dataHasChanged = true;
			this.dialogueElementsLayer.getChildren().add(newTransition);
			newTransition.toBack();
		}
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	public boolean transitionExists(State source, State target) {
		if (source == target) {
			return true;
		}
		for (final Transition transition : this.transitions) {
			if (transition.getSource().equals(source) && transition.getTarget().equals(target)) {
				return true;
			}
		}
		return false;
	}

	public List<Transition> getTransitions() {
		return this.transitions;
	}

	@Override
	public void resetData() {
		this.dialogueElementsLayer.getChildren().clear();
		this.transitions.clear();
		this.states.clear();
		this.dataHasChanged = false;
	}

	@Override
	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	@Override
	public void setUnchanged() {
		this.dataHasChanged = false;
	}

	@Override
	public void setGUIData(JSONObject dialogueModelDataObject) {
		this.resetData();
		try {
			final JSONArray newStates = dialogueModelDataObject.getJSONArray("states");
			final JSONArray newTransitions = dialogueModelDataObject.getJSONArray("transitions");
			final double locationX = dialogueModelDataObject.optDouble("locationX", -5000);
			final double locationY = dialogueModelDataObject.optDouble("locationY", -5000);
			final double scale = dialogueModelDataObject.optDouble("scale", 1.0);
			this.setDiagramScale(1);
			this.dialogueElementsLayer.setTranslateX(0);
			this.dialogueElementsLayer.setTranslateY(0);
			this.dialogueElementsLayer.relocate(locationX, locationY);
			this.setDiagramScale(scale);
			this.setStatesGUIData(newStates);
			this.setTransitionsGUIData(newTransitions);
		} catch (final JSONException ex) {
			Util.showError("Error loading your file", ex.getLocalizedMessage());
		}

		this.setUnchanged();
	}

	@Override
	public JSONObject getGUIData() {
		final JSONObject data = new JSONObject();
		final double scale = this.dialogueElementsLayer.getScaleX();
		this.setDiagramScale(1);
		data.put("states", this.getStatesGUIData());
		data.put("transitions", this.getTransitionsGUIData());
		data.put("locationX", this.dialogueElementsLayer.getBoundsInParent().getMinX());
		data.put("locationY", this.dialogueElementsLayer.getBoundsInParent().getMinY());
		data.put("scale", scale);
		this.setDiagramScale(scale);
		return data;

	}

	private int getFirstEmptyStateId() {
		for (int i = 0; i < this.states.size(); i++) {
			if (!this.states.containsKey(i)) {
				return i;
			}
		}
		return this.states.size();
	}

	private String getFirstEmptyTransitionName() {
		for (int i = 1; i <= this.transitions.size(); i++) {
			String newTransitionName = "Transition" + i;
			if (this.transitions.stream()
					.anyMatch(transition -> transition.getTriggerTextField().getText().equals(newTransitionName))) {
				continue;
			}
			return newTransitionName;
		}
		return "Transition" + (this.transitions.size() + 1);
	}

	private JSONArray getStatesGUIData() {
		final JSONArray statesData = new JSONArray();
		for (final Entry<Integer, State> stateEntry : this.states.entrySet()) {
			final State state = stateEntry.getValue();
			final int id = stateEntry.getKey();
			final JSONObject stateData = new JSONObject();
			stateData.put("locationX", state.getBoundsInParent().getMinX());
			stateData.put("locationY", state.getBoundsInParent().getMinY());
			stateData.put("name", state.getTextArea().getText());
			stateData.put("id", id);
			statesData.put(stateData);
		}
		return statesData;
	}

	private JSONArray getTransitionsGUIData() {
		final JSONArray transitionsData = new JSONArray();
		for (final Transition transition : this.transitions) {
			final JSONObject transitionData = new JSONObject();
			transitionData.put("source", transition.getSource().getStateId());
			transitionData.put("target", transition.getTarget().getStateId());
			transitionData.put("trigger", transition.getTriggerTextField().getText());
			transitionsData.put(transitionData);
		}
		return transitionsData;
	}

	private void setStatesGUIData(JSONArray states) throws JSONException {

		for (int i = 0; i < states.length(); i++) {
			final JSONObject state = states.getJSONObject(i);
			this.addState(state.getInt("id"), state.getString("name"), state.getDouble("locationX"),
					state.getDouble("locationY"), false);
		}
	}

	private void setTransitionsGUIData(JSONArray transitions) {
		for (int i = 0; i < transitions.length(); i++) {
			final JSONObject transition = transitions.getJSONObject(i);
			final State sourceState = this.states.get(transition.getInt("source"));
			final State targetState = this.states.get(transition.getInt("target"));
			final String trigger = transition.getString("trigger");
			this.addTransition(sourceState, targetState, trigger, false);
		}
	}

	private void setDiagramScale(double scale) {
		this.dialogueElementsLayer.setScaleX(scale);
		this.dialogueElementsLayer.setScaleY(scale);
	}

	private State getStateByName(String stateName) {
		for (State state : this.states.values()) {
			if (state.getName().equals(stateName)) {
				return state;
			}
		}
		return null;
	}

}