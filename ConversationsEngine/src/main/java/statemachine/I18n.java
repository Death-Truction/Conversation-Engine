package statemachine;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class for handling the localization
 * 
 * @author Marcel Engelmann
 *
 */
class I18n {

	private static ResourceBundle bundle;

	private I18n() throws IllegalStateException {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sets the localization language to the given locale if possible. Otherwise
	 * uses the default language
	 * 
	 * @param language the new language
	 */
	static void setLanguage(Locale language) {
		bundle = ResourceBundle.getBundle("localization.localization", language);
		if (!bundle.getLocale().getDisplayName().equals(language.getDisplayName())) {
			Logging.error("The language {} is not supported.", language.toLanguageTag());
		}
	}

	/**
	 * Get a message by a given key
	 * 
	 * @param key the key to the message
	 * @return the message corresponding to the key
	 */
	static String getMessage(String key) {
		return bundle.getString(key);
	}

	/**
	 * Get a message by a given key and arguments
	 * 
	 * @param key       the key to the message
	 * @param arguments the arguments for the message
	 * @return the message corresponding to the key and arguments
	 */
	static String getMessage(String key, Object... arguments) {
		return MessageFormat.format(bundle.getString(key), arguments);
	}

}
