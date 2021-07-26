package conversations_engine;

/**
 * Represents the transition between two {@link State States}
 * 
 * @author Marcel Engelmann
 *
 */
class Transition {
	private State target;
	private String trigger;

	/**
	 * Creates a new Transition object
	 * 
	 * @param target  the target of the transition
	 * @param trigger to perform the transition
	 */
	Transition(State target, String trigger) {
		this.target = target;
		this.trigger = trigger;
	}

	/**
	 * Returns the trigger for the transition
	 * 
	 * @return the trigger for the transition
	 */
	String getTrigger() {
		return this.trigger;
	}

	/**
	 * Returns the target state of the transition
	 * 
	 * @return the target state of the transition
	 */
	State getTarget() {
		return this.target;
	}
}
