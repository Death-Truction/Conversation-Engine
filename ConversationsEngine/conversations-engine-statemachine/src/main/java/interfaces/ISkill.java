package interfaces;

import org.json.JSONObject;

import statemachine.ConversationsEngine;

/**
 * This is an Interface for the skills used by the {@link ConversationsEngine}
 * 
 * @author Marcel Engelmann
 *
 */
public interface ISkill {

	/**
	 * Evaluates weather the skill can process the given intent or not
	 * 
	 * @param intent       the intent to check
	 * @param currentState the current state of the skill
	 * @return true if the skill can execute the given intent
	 */
	public boolean canExecute(String intent, String currentState);

	/**
	 * Executes the given intent
	 * 
	 * @param intent        the intent to be executed
	 * @param contextObject the reference to the context object used by the
	 *                      {@link ConversationsEngine}
	 * @param newEntities   the new entities found by the {@link INLPComponent}
	 * @param currentState  the current state of the skill
	 * @return
	 */
	public ISkillAnswer execute(String intent, JSONObject contextObject, JSONObject newEntities, String currentState);

	/**
	 * Resets the skill. This is usually required when the user aborts a request
	 */
	public void reset();

}
