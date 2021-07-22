package conversations_engine;

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
	private static Locale defaultLocale;

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
			bundle = ResourceBundle.getBundle("localization.localization", defaultLocale);
		}
	}

	/**
	 * Returns the currently used language locale
	 * 
	 * @return the currently used language locale
	 */
	static Locale getLanguage() {
		return bundle.getLocale();
	}

	/**
	 * Sets the default language for the {@link ConversationsEngine}.
	 * 
	 * @param language the language locale to use as default
	 */
	static void setDefaultLanguage(Locale language) {
		// Check weather the locale exists or not
		if (ResourceBundle.getBundle("localization.localization", language).getLocale().getLanguage()
				.equals(language.getLanguage())) {
			defaultLocale = language;
			setLanguage(defaultLocale);
			return;
		}
		Logging.warn(
				"Default language was not set! The language {} could not be found. Please make sure that the correct localization file exists.",
				language.getLanguage());

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
