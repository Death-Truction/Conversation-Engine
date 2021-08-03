package de.dai_labor.conversation_engine_gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import de.dai_labor.conversation_engine_gui.gui_components.Edge;
import de.dai_labor.conversation_engine_gui.gui_components.State;

@Singleton
public class DialogueModelData {
	private List<State> states = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();

	public DialogueModelData() {
	}

	public State addNewState(double locationX, double locationY) {
		return this.addState(getStateName(), locationX, locationY);
	}

	public State addState(String name, double locationX, double locationY) {
		State newState = new State(name, locationX, locationY);
		this.states.add(newState);
		return newState;
	}

	private String getStateName() {
		return "State" + states.size();
	}

	public Edge addTransition(State source, State target) {
		Edge newEdge = new Edge(source, target, "Transition" + this.states.size());
		edges.add(newEdge);
		return newEdge;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("States: \n");
		for (State state : states) {
			sb.append("\t" + state.getName() + "\n");
		}
		sb.append("Transitions: \n");
		for (Edge edge : edges) {
			sb.append("\t" + edge.getSource() + " - " + edge.getTarget() + "\n");
		}
		return sb.toString();
	}
}
