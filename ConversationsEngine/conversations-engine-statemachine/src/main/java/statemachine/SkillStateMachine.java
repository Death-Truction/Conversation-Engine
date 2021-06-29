package statemachine;

import org.json.JSONObject;

import interfaces.ISkill;
import interfaces.ISkillAnswer;

/**
 * Represents the state machine of a skill
 * 
 * @author Marcel Engelmann
 *
 */
class SkillStateMachine {
	private String name;
	private State currentState;
	private State startState;
	private State endState;
	private ISkill skill;

	/**
	 * Create a new SkillStateMachine object
	 * 
	 * @param name       the name of the skill
	 * @param startState the start state of the skill
	 * @param endState   the end state of the skill
	 * @param skill      the corresponding skill to the state machine
	 */
	SkillStateMachine(String name, State startState, State endState, ISkill skill) {
		this.name = name;
		this.startState = startState;
		this.currentState = startState;
		this.endState = endState;
		this.skill = skill;
	}

	/**
	 * Returns the current {@link State} of the skill's state machine
	 * 
	 * @return the current {@link State} of the skill's state machine
	 */
	State getCurrentState() {
		return this.currentState;
	}

	/**
	 * Returns the name of the skill
	 * 
	 * @return the name of the skill
	 */
	String getName() {
		return this.name;
	}

	/**
	 * Returns true if the state machine ended
	 * 
	 * @return true if the state machine ended
	 */
	boolean hasEnded() {
		return this.currentState.equals(endState);
	}

	/**
	 * Resets the skill's state machine and sends an abort signal to the
	 * corresponding skill
	 */
	void reset() {
		this.currentState = this.startState;
		this.skill.reset();
	}

	/**
	 * Checks if the corresponding skill can execute a given intent
	 * 
	 * @param intent the intent that's supposed to be executed
	 * @return true if the skill can execute the intent
	 */
	boolean canExecute(String intent) {
		return this.skill.canExecute(intent, currentState.getName());
	}

	/**
	 * Sends the to executing intent to the skill
	 * 
	 * @param intent        the intent to execute
	 * @param contextObject the current context object of the conversations engine
	 * @param newEntities   the new entities returned by the NLP-Component
	 * @return a {@link ISkillAnswer} from the corresponding skill or null if an
	 *         error occurred
	 */
	ISkillAnswer execute(String intent, JSONObject contextObject, JSONObject newEntities) {
		if (this.currentState.equals(this.startState)) {
			Logging.debug("Starting the Skill {}", this.getName());
		}
		ISkillAnswer answer = this.skill.execute(intent, contextObject, newEntities, this.currentState.getName());
		if (answer == null) {
			Logging.error("Returned answer of the skill {} for the intent {} is null", this.name, intent);
			return null;
		}
		if (answer.getTransitionTrigger() == null || answer.getTransitionTrigger().isBlank()) {
			Logging.error("The trigger in the answer of the skill {}, for the intent {}, is null or empty",
					this.name, intent);
			return null;
		}
		if (!this.enteredNextStateSuccessfully(answer.getTransitionTrigger())) {
			return null;
		}
		return answer;
	}

	/**
	 * Enters the next state corresponding to the trigger for the transition
	 * 
	 * @param trigger the trigger for the next transition
	 * @return true if the next state was entered correctly
	 */
	private boolean enteredNextStateSuccessfully(String trigger) {
		State nextState = this.currentState.getNextState(trigger);
		if (nextState == null) {
			Logging.error("Cannot find Transition {} in {}'s State {}", trigger, this.name,
					this.currentState.getName());
			return false;
		}
		String oldState = this.currentState.getName();
		this.currentState = nextState;
		Logging.debug("Transition from State {} to the State {} within the skill {}", oldState,
				this.currentState.getName(), this.name);
		return true;
	}

}
