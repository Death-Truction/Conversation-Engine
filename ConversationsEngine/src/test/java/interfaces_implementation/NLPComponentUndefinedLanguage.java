package interfaces_implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import interfaces.INLPAnswer;
import interfaces.INLPComponent;

/**
 * A simple implementation of a {@link INLPComponent} only used for test
 * coverage
 * 
 * @author Marcel Engelmann
 *
 */
public class NLPComponentUndefinedLanguage implements INLPComponent {

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

		return new NLPAnswer(intents, new Locale("el"), false);
	}

	@Override
	public INLPAnswer understandInput(String input, JSONObject contextObject) {
		List<String> intents = new ArrayList<>();
		intents.add("greeting");
		return new NLPAnswer(intents, new Locale("el"), false);
	}

}
