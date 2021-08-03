package de.dai_labor.conversation_engine_gui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import javafx.scene.layout.Pane;

@Singleton
public class DialogueModelData {
	private Map<Integer, State> states;
	private List<Transition> transitions;
	private boolean hasChanged;

	public DialogueModelData() {
		this.hasChanged = false;
		this.transitions = new ArrayList<>();
		this.states = new HashMap<>();
	}

	public State addNewState(double locationX, double locationY) {
		return this.addState(getStateId(), getStateName(), locationX, locationY);
	}

	public State addState(int id, String name, double locationX, double locationY) {
		State newState = new State(id, name, locationX, locationY);
		newState.getTextArea().textProperty().addListener((s, y, z) -> this.hasChanged = true);
		this.states.put(id, newState);
		this.hasChanged = true;
		return newState;
	}

	public Transition addTransition(State source, State target) {
		Transition newTransition = new Transition(source, target, "Transition" + this.transitions.size());
		newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.hasChanged = true);
		transitions.add(newTransition);
		this.hasChanged = true;
		return newTransition;
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
		for (Transition transition : transitions) {
			((Pane) transition.getParent()).getChildren().remove(transition);
		}
		for (Entry<Integer, State> stateEntry : states.entrySet()) {
			State state = stateEntry.getValue();
			((Pane) state.getParent()).getChildren().remove(state);
		}
		this.transitions = new ArrayList<>();
		this.states = new HashMap<>();
	}

	public boolean hasChanged() {
		return this.hasChanged;
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
					+ transition.getTriggerTextField().getText());
		}
		return sb.toString();
	}

	private String getStateName() {
		return "State" + states.size();
	}

	private int getStateId() {
		return this.states.size();
	}

	public void setGUIData() {
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("states", getStatesGUIData());
		data.put("transitions", getTransitionsGUIData());
		return data;

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
		}
		return transitionsData;
	}
}
