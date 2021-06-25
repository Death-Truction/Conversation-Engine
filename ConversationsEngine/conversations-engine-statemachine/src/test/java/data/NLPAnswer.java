package data;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import interfaces.INLPAnswer;

public class NLPAnswer implements INLPAnswer {

	private List<String> intents;
	private JSONObject newEntities;
	private Locale foundLanguage;

	public NLPAnswer(List<String> intents, JSONObject newEntities, Locale foundLanguage) {
		this.intents = intents;
		this.newEntities = newEntities;
		this.foundLanguage = foundLanguage;
	}

	@Override
	public List<String> getIntents() {
		return this.intents;
	}

	@Override
	public JSONObject getNewEntities() {
		return this.newEntities;
	}

	@Override
	public Locale getInputLanguage() {
		return this.foundLanguage;
	}

}
