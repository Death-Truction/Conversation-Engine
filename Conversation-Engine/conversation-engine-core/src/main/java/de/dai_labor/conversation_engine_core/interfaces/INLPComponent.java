package de.dai_labor.conversation_engine_core.interfaces;

import java.util.List;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;

/**
 * This is an interface for the INLPComponent, that is required for the
 * {@link ConversationEngine}
 * 
 * @author Marcel Engelmann
 *
 */
public interface INLPComponent {

	/**
	 * Add entities that are used by the {@link ISkill skills} of the
	 * {@link ConversationEngine}
	 * 
	 * @param entities the entities that are used by the {@link ISkill skills} of
	 *                 the {@link ConversationEngine}
	 */
	void addUsedEntities(List<String> entities);

	/**
	 * Add intents the intents that are used by the {@link ISkill skills} or the
	 * {@link ConversationEngine}
	 * 
	 * @param intents the intents that are used by the {@link ISkill skills} or the
	 *                {@link ConversationEngine}
	 */
	void addUsedIntents(List<String> intents);

	/**
	 * Processes the given input to fill the context object with the the given
	 * entityName
	 * 
	 * @param input         the input to be processed
	 * @param entityName    the entity name to be filled
	 * @param contextObject a reference to the context object used by the
	 *                      {@link ConversationEngine}
	 * @return a new {@link INLPAnswer}
	 */
	INLPAnswer understandInput(String input, String entityName, JSONObject contextObject);

	/**
	 * Processes the given input to fill the context object with the found
	 * information in the input
	 * 
	 * @param input         the input to be processed
	 * @param contextObject a reference to the context object used by the
	 *                      {@link ConversationEngine}
	 * @return a new {@link INLPAnswer}
	 */
	INLPAnswer understandInput(String input, JSONObject contextObject);

}