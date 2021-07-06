package data;

import java.util.List;
import java.util.Locale;

import interfaces.INLPAnswer;

/**
 * A simple {@link INLPAnswer} implementation for testing purposes
 * 
 * @author Marcel Engelmann
 *
 */
public class NLPAnswer implements INLPAnswer {

	private List<String> intents;
	private Locale foundLanguage;
	private boolean hasAddedEntities;

	public NLPAnswer(List<String> intents, Locale foundLanguage, boolean hasAddedEntities) {
		this.intents = intents;
		this.foundLanguage = foundLanguage;
		this.hasAddedEntities = hasAddedEntities;
	}

	@Override
	public List<String> getIntents() {
		return this.intents;
	}

	@Override
	public Locale getInputLanguage() {
		return this.foundLanguage;
	}

	@Override
	public boolean hasAddedEntities() {
		return this.hasAddedEntities;
	}

}
