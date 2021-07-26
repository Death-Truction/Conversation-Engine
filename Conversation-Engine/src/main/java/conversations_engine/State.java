package conversations_engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a State of a state machine
 * 
 * @author Marcel Engelmann
 *
 */
class State {
	private List<Transition> transitions;
	private String name;

	/**
	 * Create a new State object
	 * 
	 * @param name the name of the state
	 */
	State(String name) {
		this.name = name;
		this.transitions = new ArrayList<>();
	}

	/**
	 * Returns the name of the state
	 * 
	 * @return the name of the state
	 */
	String getName() {
		return this.name;
	}

	/**
	 * Adds a new transition to the State
	 * 
	 * @param transition the transition to add
	 */
	void addTransition(Transition transition) {
		this.transitions.add(transition);
	}

	/**
	 * Returns the target state of the transition that has the @param trigger
	 * 
	 * @param trigger the trigger for the next transition
	 * @return the target state of the next transition or null if the transition
	 *         does not exist
	 */
	State getNextState(String trigger) {
		for (Transition transition : transitions) {
			if (transition.getTrigger().equals(trigger)) {
				return transition.getTarget();
			}
		}
		return null;
	}
}
