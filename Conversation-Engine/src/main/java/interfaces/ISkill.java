package interfaces;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import conversation_engine.ConversationsEngine;

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
	boolean canExecute(String intent, String currentState);

	/**
	 * Executes the given intent
	 * 
	 * @param intent        the intent to be executed
	 * @param contextObject the reference to the context object used by the
	 *                      {@link ConversationsEngine}
	 * @param currentState  the current state of the skill
	 * @param language      the user's language
	 * @return a new {@link ISkillAnswer}
	 */
	ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language);

	/**
	 * Resets the skill. This is usually required when the user aborts a request
	 */
	void reset();

	/**
	 * Returns a list of possible user request for the current state
	 * 
	 * @param currentState The current state of the skill
	 * @param language     the user's language
	 * @return a list of possible user request for the current state
	 */
	List<String> getExampleRequests(String currentState, Locale language);

}
