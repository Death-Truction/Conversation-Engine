package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;

@Singleton
public class DialogueViewModel implements ViewModel {
	private DialoguePane dialoguePane;
	private Pane diagramElementsLayer;
	private SimpleStringProperty insertMode = new SimpleStringProperty("");
	private Toggle toggleButton;
	private Map<Integer, State> states;
	private List<Transition> transitions;
	private boolean hasDataChanged;

	public DialogueViewModel() {
		this.diagramElementsLayer = new Pane();
		this.dialoguePane = new DialoguePane(diagramElementsLayer, this.insertMode, this::addState, this::addTransition,
				this::removeState, this::removeTransition);
		this.diagramElementsLayer.setMinWidth(10000);
		this.diagramElementsLayer.setMinHeight(10000);
		this.diagramElementsLayer.relocate(-5000, -5000);
		this.diagramElementsLayer.toBack();
		this.dialoguePane.toBack();
		this.hasDataChanged = false;
		this.transitions = new ArrayList<>();
		this.states = new HashMap<>();
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
		locationX -= State.INITIAL_SIZE;
		locationY -= State.INITIAL_SIZE;
		this.addState(getStateId(), getStateName(), locationX, locationY);
	}

	public void addState(int id, String name, double locationX, double locationY) {
		State newState = new State(id, name, locationX, locationY);
		newState.getTextArea().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
		this.states.put(id, newState);
		this.hasDataChanged = true;
		this.diagramElementsLayer.getChildren().add(newState);
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	public void removeState(State state) {
		for (Iterator<Transition> iterator = this.transitions.iterator(); iterator.hasNext();) {
			Transition transition = iterator.next();
			if (transition.getSource().equals(state) || transition.getTarget().equals(state)) {
				this.hasDataChanged = true;
				iterator.remove();
				this.diagramElementsLayer.getChildren().remove(transition);
			}
		}
		for (Iterator<Entry<Integer, State>> iterator = this.states.entrySet().iterator(); iterator.hasNext();) {
			State tmpState = iterator.next().getValue();
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
		boolean transitionExist = this.transitionExists(source, target);
		if (!transitionExist) {
			Transition newTransition = new Transition(source, target, "Transition" + this.transitions.size());
			newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
			transitions.add(newTransition);
			this.hasDataChanged = true;
			this.diagramElementsLayer.getChildren().add(newTransition);
			newTransition.toBack();
		}
		this.toggleButton.setSelected(false);
	}

	public void addTransition(State source, State target, String trigger) {
		Transition newTransition = new Transition(source, target, trigger);
		newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.hasDataChanged = true);
		transitions.add(newTransition);
		this.hasDataChanged = true;
		this.diagramElementsLayer.getChildren().add(newTransition);
		newTransition.toBack();
	}

	public boolean transitionExists(State source, State target) {
		if (source == target) {
			return true;
		}
		for (Transition transition : this.transitions) {
			if (transition.getSource().equals(source) && transition.getTarget().equals(target)) {
				return true;
			}
		}
		return false;
	}

	public void resetData() {
		// TODO: better algo?
		for (Transition transition : transitions) {
			((Pane) transition.getParent()).getChildren().remove(transition);
		}
		for (Entry<Integer, State> stateEntry : states.entrySet()) {
			State state = stateEntry.getValue();
			((Pane) state.getParent()).getChildren().remove(state);
		}
		this.transitions = new ArrayList<>();
		this.states = new HashMap<>();
		this.hasDataChanged = false;
	}

	public boolean hasChanged() {
		return this.hasDataChanged;
	}

	public void hasChanged(boolean value) {
		this.hasDataChanged = value;
	}

	public void setGUIData(JSONObject dialogueModelDataObject) {
		resetData();
		try {
			JSONArray states = dialogueModelDataObject.getJSONArray("states");
			JSONArray transitions = dialogueModelDataObject.getJSONArray("transitions");
			double locationX = dialogueModelDataObject.optDouble("locationX", -5000);
			double locationY = dialogueModelDataObject.optDouble("locationY", -5000);
			double scale = dialogueModelDataObject.optDouble("scale", 1.0);
			this.setDiagramScale(1);
			this.diagramElementsLayer.setTranslateX(0);
			this.diagramElementsLayer.setTranslateY(0);
			this.diagramElementsLayer.relocate(locationX, locationY);
			this.setDiagramScale(scale);
			setStatesGUIData(states);
			setTransitionsGUIData(transitions);
		} catch (JSONException ex) {
			Util.showError("Error loading your file", ex.getLocalizedMessage());
		}

		this.hasChanged(false);
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		double scale = this.diagramElementsLayer.getScaleX();
		this.setDiagramScale(1);
		data.put("states", getStatesGUIData());
		data.put("transitions", getTransitionsGUIData());
		data.put("locationX", this.diagramElementsLayer.getBoundsInParent().getMinX());
		data.put("locationY", this.diagramElementsLayer.getBoundsInParent().getMinY());
		data.put("scale", scale);
		this.setDiagramScale(scale);
		return data;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("States: \n");
		for (Entry<Integer, State> stateEntry : states.entrySet()) {
			State state = stateEntry.getValue();
			sb.append("\t" + state.getTextArea().getText() + "\n");
		}
		sb.append("Transitions: \n");
		for (Transition transition : transitions) {
			sb.append("\t" + transition.getSource().getTextArea().getText() + " - "
					+ transition.getTarget().getTextArea().getText() + " | Trigger: "
					+ transition.getTriggerTextField().getText() + "\n");
		}
		return sb.toString();
	}

	private String getStateName() {
		return "State" + states.size();
	}

	private int getStateId() {
		return this.states.size();
	}

	private JSONArray getStatesGUIData() {
		JSONArray statesData = new JSONArray();
		for (Entry<Integer, State> stateEntry : states.entrySet()) {
			State state = stateEntry.getValue();
			int id = stateEntry.getKey();
			JSONObject stateData = new JSONObject();
			stateData.put("locationX", state.getBoundsInParent().getMinX());
			stateData.put("locationY", state.getBoundsInParent().getMinY());
			stateData.put("name", state.getTextArea().getText());
			stateData.put("id", id);
			statesData.put(stateData);
		}
		return statesData;
	}

	private JSONArray getTransitionsGUIData() {
		JSONArray transitionsData = new JSONArray();
		for (Transition transition : transitions) {
			JSONObject transitionData = new JSONObject();
			transitionData.put("source", transition.getSource().getStateID());
			transitionData.put("target", transition.getTarget().getStateID());
			transitionData.put("trigger", transition.getTriggerTextField().getText());
			transitionsData.put(transitionData);
		}
		return transitionsData;
	}

	private void setStatesGUIData(JSONArray states) throws JSONException {

		for (int i = 0; i < states.length(); i++) {
			JSONObject state = states.getJSONObject(i);
			this.addState(state.getInt("id"), state.getString("name"), state.getDouble("locationX"),
					state.getDouble("locationY"));
		}
	}

	private void setTransitionsGUIData(JSONArray transitions) {
		for (int i = 0; i < transitions.length(); i++) {
			JSONObject transition = transitions.getJSONObject(i);
			State sourceState = states.get(transition.getInt("source"));
			State targetState = states.get(transition.getInt("target"));
			String trigger = transition.getString("trigger");
			this.addTransition(sourceState, targetState, trigger);
		}
	}

	private void setDiagramScale(double scale) {
		this.diagramElementsLayer.setScaleX(scale);
		this.diagramElementsLayer.setScaleY(scale);
	}

}