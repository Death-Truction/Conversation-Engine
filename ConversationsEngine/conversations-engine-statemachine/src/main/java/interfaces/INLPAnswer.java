package interfaces;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

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
	 * Returns a {@link JSONObject} with all the new entities found in the input
	 * 
	 * @return a {@link JSONObject} with all the new entities found in the input
	 */
	JSONObject getNewEntities();

	/**
	 * Returns a {@link Locale} that defines the language found in the input
	 * 
	 * @return a {@link Locale} that defines the language found in the input
	 */
	Locale getInputLanguage();

}