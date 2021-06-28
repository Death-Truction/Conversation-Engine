package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import interfaces.INLPAnswer;
import interfaces.INLPComponent;

/**
 * A simple implementation of a {@link INLPComponent} purely for test coverage
 * 
 * @author Marcel Engelmann
 *
 */

// TODO: komplett in englisch? eventuell mit der anderen nlp komponente verbinden?
public class NLPComponentEnglish implements INLPComponent {

	@Override
	public void addUsedEntities(List<String> entities) {
		// ignored in this emulated NLPComponent
	}

	@Override
	public void addUsedIntents(List<String> intentNames) {
		// ignored in this emulated NLPComponent
	}

	@Override
	public INLPAnswer understandInput(String input, String entityName, JSONObject contextObject) {
		List<String> intents = new ArrayList<>();
		JSONObject newEntities = new JSONObject();
		return new NLPAnswer(intents, newEntities, new Locale("en", "US"));
	}

	@Override
	public INLPAnswer understandInput(String input, JSONObject contextObject) {
		List<String> intents = new ArrayList<>();
		JSONObject newEntities = new JSONObject();
		return new NLPAnswer(intents, newEntities, new Locale("en", "US"));
	}

}
