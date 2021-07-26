package interfaces;

import java.util.List;

import org.json.JSONObject;

import conversation_engine.ConversationsEngine;

/**
 * This is an interface for the INLPComponent, that is required for the
 * {@link ConversationsEngine}
 * 
 * @author Marcel Engelmann
 *
 */
public interface INLPComponent {

	/**
	 * Add entities that are used by the {@link ISkill skills} of the
	 * {@link ConversationsEngine}
	 * 
	 * @param entities the entities that are used by the {@link ISkill skills} of
	 *                 the {@link ConversationsEngine}
	 */
	void addUsedEntities(List<String> entities);

	/**
	 * Add intents the intents that are used by the {@link ISkill skills} or the
	 * {@link ConversationsEngine}
	 * 
	 * @param intents the intents that are used by the {@link ISkill skills} or the
	 *                {@link ConversationsEngine}
	 */
	void addUsedIntents(List<String> intents);

	/**
	 * Processes the given input to fill the context object with the the given
	 * entityName
	 * 
	 * @param input         the input to be processed
	 * @param entityName    the entity name to be filled
	 * @param contextObject a reference to the context object used by the
	 *                      {@link ConversationsEngine}
	 * @return a new {@link INLPAnswer}
	 */
	INLPAnswer understandInput(String input, String entityName, JSONObject contextObject);

	/**
	 * Processes the given input to fill the context object with the found
	 * information in the input
	 * 
	 * @param input         the input to be processed
	 * @param contextObject a reference to the context object used by the
	 *                      {@link ConversationsEngine}
	 * @return a new {@link INLPAnswer}
	 */
	INLPAnswer understandInput(String input, JSONObject contextObject);

}