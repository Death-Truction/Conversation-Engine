package de.dai_labor.conversation_engine_core.interfaces;

import java.util.List;
import java.util.Locale;

/**
 * This is an interface for the INLPAnswer that is required for the
 * {@link INLPComponent}
 * 
 * @author Marcel Engelmann
 *
 */
public interface INLPAnswer {

	/**
	 * Returns a {@link List} with the intents found in the input
	 * 
	 * @return a {@link List} with the intents found in the input
	 */
	List<String> getIntents();

	/**
	 * Indicates whether the {@link INLPComponent} has added new entities to the
	 * context object
	 * 
	 * @return true if new entities were added to the context object
	 */
	boolean hasAddedEntities();

	/**
	 * Returns a {@link Locale} that defines the language found in the input
	 * 
	 * @return a {@link Locale} that defines the language found in the input
	 */
	Locale getInputLanguage();

}