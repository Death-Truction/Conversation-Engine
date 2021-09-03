package de.dai_labor.conversation_engine_gui.models;

import java.util.List;

import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;

public class SimulationStep {
	private State source;
	private State target;
	private Transition transition;
	private String input;
	private List<String> outputs;

	public SimulationStep(State source, State target, Transition transition, String input, List<String> outputs) {
		this.source = source;
		this.target = target;
		this.transition = transition;
		this.input = input;
		this.outputs = outputs;
	}

	public State getSource() {
		return this.source;
	}

	public State getTarget() {
		return this.target;
	}

	public Transition getTransition() {
		return this.transition;
	}

	public String getInput() {
		return this.input;
	}

	public List<String> getOutput() {
		return this.outputs;
	}

}
