package de.dai_labor.conversation_engine_gui.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DialogModelData {
	private Map<Integer, State> states = new HashMap<>();
	private Map<Integer, Edge> edges = new HashMap<>();

	public State addState() {
		int id = new Random().nextInt(1000);
		State newState = new State("State" + id, new Random().nextInt(1000), new Random().nextInt(700));
		states.put(id, newState);
		return newState;
	}
}
