package de.dai_labor.conversation_engine_gui.models;

import java.util.Locale;
import java.util.Objects;

public enum LanguageEnum {
	GERMAN(new Locale("de", "DE")), ENGLISH(new Locale("en", "US"));

	private final Locale locale;

	LanguageEnum(Locale locale) {
		this.locale = Objects.requireNonNull(locale);
	}

	public Locale getLocale() {
		return this.locale;
	}
}
