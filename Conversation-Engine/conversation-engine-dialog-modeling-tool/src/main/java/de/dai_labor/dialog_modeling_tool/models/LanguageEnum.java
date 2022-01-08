package de.dai_labor.dialog_modeling_tool.models;

import java.util.Locale;
import java.util.Objects;

/**
 * An enum that maps a language name to a {@link Locale}
 *
 * @author Marcel Engelmann
 *
 */
public enum LanguageEnum {
	/**
	 * The locale de_DE
	 */
	GERMAN(new Locale("de", "DE")),
	/**
	 * the locale en_US
	 */
	ENGLISH(new Locale("en", "US"));

	private final Locale locale;

	/**
	 * Constructor for a new {@link LanguageEnum}
	 *
	 * @param locale the locale/language for the new enum
	 */
	LanguageEnum(Locale locale) {
		this.locale = Objects.requireNonNull(locale);
	}

	/**
	 * Gets the Locale for this enum
	 *
	 * @return the Locale for this enum
	 */
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public String toString() {
		// Capitalize first letter, lower case everything else
		return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
	}
}
