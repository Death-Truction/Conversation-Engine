package de.dai_labor.conversation_engine_gui.models;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import de.dai_labor.conversation_engine_gui.gui_components.State;

@Singleton
public class DialogueModelData {
	private Map<String, State> states = new HashMap<>();
	private Map<Integer, Edge> edges = new HashMap<>();

	public DialogueModelData() {
	}

	public State addNewState(double locationX, double locationY) {
		return this.addState(getFirstFreeStateId(), locationX, locationY);
	}

	public State addState(String name, double locationX, double locationY) {
		State newState = new State(name, locationX, locationY);
		this.states.put(name, newState);
		return newState;
	}

	private String getFirstFreeStateId() {
		for (int i = 0; i < this.states.size(); i++) {
			String name = "State" + i;
			if (!states.containsKey("State" + i)) {
				return name;
			}
		}
		return "State" + states.size();
	}
}
