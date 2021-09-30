package de.dai_labor.conversation_engine_gui.models;

import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;

/**
 * Stores the required data for a single step within the simulation
 *
 * @author Marcel Engelmann
 *
 */
public class SimulationStep {
	private State source;
	private State target;
	private Transition transition;
	private String input;
	private List<String> output;
	private List<ILoggingEvent> loggingOutput;

	/**
	 * Creates a new SimulationStep
	 *
	 * @param source        The source {@link State}
	 * @param target        The source {@link State}
	 * @param transition    The {@link Transition} between the source and target
	 *                      state
	 * @param input         The user input
	 * @param output        The output of the {@link ConversationEngine}
	 * @param loggingEvents The logged Events
	 */
	public SimulationStep(State source, State target, Transition transition, String input, List<String> output,
			List<ILoggingEvent> loggingEvents) {
		this.source = source;
		this.target = target;
		this.transition = transition;
		this.input = input;
		this.output = output;
		this.loggingOutput = loggingEvents;
	}

	/**
	 * Gets the source {@link State}
	 *
	 * @return the source {@link State}
	 */
	public State getSource() {
		return this.source;
	}

	/**
	 * Gets the target {@link State}
	 *
	 * @return the target {@link State}
	 */
	public State getTarget() {
		return this.target;
	}

	/**
	 * Gets the {@link Transition}
	 *
	 * @return the {@link Transition}
	 */
	public Transition getTransition() {
		return this.transition;
	}

	/**
	 * Gets the user input
	 *
	 * @return the user input
	 */
	public String getInput() {
		return this.input;
	}

	/**
	 * Gets the output of the {@link ConversationEngine}
	 *
	 * @return the output of the {@link ConversationEngine}
	 */
	public List<String> getOutput() {
		return this.output;
	}

	/**
	 * Gets the logged events from the {@link ConversationEngine}
	 *
	 * @return the logged events from the {@link ConversationEngine}
	 */
	public List<ILoggingEvent> getLoggingOutput() {
		return this.loggingOutput;
	}

}
