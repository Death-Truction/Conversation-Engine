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
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;

@Singleton
public class DialogueViewModel implements ViewModel {
	private final DialoguePane dialoguePane;
	private final Pane diagramElementsLayer;
	private final SimpleStringProperty insertMode = new SimpleStringProperty("");
	private final Settings settings;
	private Toggle toggleButton;
	private final ObservableMap<Integer, State> states;
	private final List<Transition> transitions;
	private boolean hasDataChanged;

	public DialogueViewModel(Settings settings) {
		this.settings = settings;
		this.diagramElementsLayer = new Pane();
		this.dialoguePane = new DialoguePane(this.diagramElementsLayer, this.insertMode, this::addState,
				this::addTransition, this::removeState, this::removeTransition);
		this.diagramElementsLayer.setMinWidth(10000);
		this.diagramElementsLayer.setMinHeight(10000);
		this.diagramElementsLayer.relocate(-5000, -5000);
		this.diagramElementsLayer.toBack();
		this.dialoguePane.toBack();
		this.hasDataChanged = false;
		this.transitions = new ArrayList<>();
		this.states = FXCollections.observableHashMap();
		this.subscribe("unload", (ignore, ignore2) -> {
			this.dialoguePane.unselectAll();
		});
	}

	public Node getView() {
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
		this.addState(this.getStateId(), this.getStateName(), locationX, locationY);
	}

	public void addState(int id, String name, double locationX, double locationY) {
		final State newState = new State(id, name, locationX, locationY, true, this.settings);
		newState.getTextArea().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
		this.states.put(id, newState);
		this.hasDataChanged = true;
		this.diagramElementsLayer.getChildren().add(newState);
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	public ObservableMap<Integer, State> getStates() {
		return this.states;
	}

	public void removeState(State state) {
		for (final Iterator<Transition> iterator = this.transitions.iterator(); iterator.hasNext();) {
			final Transition transition = iterator.next();
			if (transition.getSource().equals(state) || transition.getTarget().equals(state)) {
				this.hasDataChanged = true;
				iterator.remove();
				this.diagramElementsLayer.getChildren().remove(transition);
			}
		}
		for (final Iterator<Entry<Integer, State>> iterator = this.states.entrySet().iterator(); iterator.hasNext();) {
			final State tmpState = iterator.next().getValue();
			if (tmpState.equals(state)) {
				this.hasDataChanged = true;
				iterator.remove();
				this.diagramElementsLayer.getChildren().remove(tmpState);
			}
		}
	}

	public void removeTransition(Transition transition) {
		this.diagramElementsLayer.getChildren().remove(transition);
		this.transitions.remove(transition);
		this.hasDataChanged = true;
	}

	public void addTransition(State source, State target) {
		final boolean transitionExist = this.transitionExists(source, target);
		if (!transitionExist) {
			final Transition newTransition = new Transition(source, target, "Transition" + this.transitions.size());
			newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
			this.transitions.add(newTransition);
			this.hasDataChanged = true;
			this.diagramElementsLayer.getChildren().add(newTransition);
			newTransition.toBack();
		}
		this.toggleButton.setSelected(false);
	}

	public void addTransition(State source, State target, String trigger) {
		final Transition newTransition = new Transition(source, target, trigger);
		newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
		this.transitions.add(newTransition);
		this.hasDataChanged = true;
		this.diagramElementsLayer.getChildren().add(newTransition);
		newTransition.toBack();
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

	public void resetData() {
		// TODO: better algo?
		for (final Transition transition : this.transitions) {
			((Pane) transition.getParent()).getChildren().remove(transition);
		}
		for (final Entry<Integer, State> stateEntry : this.states.entrySet()) {
			final State state = stateEntry.getValue();
			((Pane) state.getParent()).getChildren().remove(state);
		}
		this.transitions.clear();
		this.states.clear();
		this.hasDataChanged = false;
	}

	public boolean hasChanged() {
		return this.hasDataChanged;
	}

	public void hasChanged(boolean value) {
		this.hasDataChanged = value;
	}

	public void setGUIData(JSONObject dialogueModelDataObject) {
		this.resetData();
		try {
			final JSONArray states = dialogueModelDataObject.getJSONArray("states");
			final JSONArray transitions = dialogueModelDataObject.getJSONArray("transitions");
			final double locationX = dialogueModelDataObject.optDouble("locationX", -5000);
			final double locationY = dialogueModelDataObject.optDouble("locationY", -5000);
			final double scale = dialogueModelDataObject.optDouble("scale", 1.0);
			this.setDiagramScale(1);
			this.diagramElementsLayer.setTranslateX(0);
			this.diagramElementsLayer.setTranslateY(0);
			this.diagramElementsLayer.relocate(locationX, locationY);
			this.setDiagramScale(scale);
			this.setStatesGUIData(states);
			this.setTransitionsGUIData(transitions);
		} catch (final JSONException ex) {
			Util.showError("Error loading your file", ex.getLocalizedMessage());
		}

		this.hasChanged(false);
	}

	public JSONObject getGUIData() {
		final JSONObject data = new JSONObject();
		final double scale = this.diagramElementsLayer.getScaleX();
		this.setDiagramScale(1);
		data.put("states", this.getStatesGUIData());
		data.put("transitions", this.getTransitionsGUIData());
		data.put("locationX", this.diagramElementsLayer.getBoundsInParent().getMinX());
		data.put("locationY", this.diagramElementsLayer.getBoundsInParent().getMinY());
		data.put("scale", scale);
		this.setDiagramScale(scale);
		return data;

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("States: \n");
		for (final Entry<Integer, State> stateEntry : this.states.entrySet()) {
			final State state = stateEntry.getValue();
			sb.append("\t" + state.getTextArea().getText() + "\n");
		}
		sb.append("Transitions: \n");
		for (final Transition transition : this.transitions) {
			sb.append("\t" + transition.getSource().getTextArea().getText() + " - "
					+ transition.getTarget().getTextArea().getText() + " | Trigger: "
					+ transition.getTriggerTextField().getText() + "\n");
		}
		return sb.toString();
	}

	private String getStateName() {
		return "State" + this.states.size();
	}

	private int getStateId() {
		return this.states.size();
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
			transitionData.put("source", transition.getSource().getStateID());
			transitionData.put("target", transition.getTarget().getStateID());
			transitionData.put("trigger", transition.getTriggerTextField().getText());
			transitionsData.put(transitionData);
		}
		return transitionsData;
	}

	private void setStatesGUIData(JSONArray states) throws JSONException {

		for (int i = 0; i < states.length(); i++) {
			final JSONObject state = states.getJSONObject(i);
			this.addState(state.getInt("id"), state.getString("name"), state.getDouble("locationX"),
					state.getDouble("locationY"));
		}
	}

	private void setTransitionsGUIData(JSONArray transitions) {
		for (int i = 0; i < transitions.length(); i++) {
			final JSONObject transition = transitions.getJSONObject(i);
			final State sourceState = this.states.get(transition.getInt("source"));
			final State targetState = this.states.get(transition.getInt("target"));
			final String trigger = transition.getString("trigger");
			this.addTransition(sourceState, targetState, trigger);
		}
	}

	private void setDiagramScale(double scale) {
		this.diagramElementsLayer.setScaleX(scale);
		this.diagramElementsLayer.setScaleY(scale);
	}

}