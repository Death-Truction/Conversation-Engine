package de.dai_labor.conversation_engine_gui_test_package;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_core.interfaces.ISkillAnswer;

/**
 * A {@link ISkill skill} that provides a random generated weather information
 * for a given city
 *
 * @author Marcel Engelmann
 *
 */
public class WeatherSkill implements ISkill {

	private String[] weatherDescriptions = new String[] { "bei klarem Himmel", "bei starkem Regen",
			"bei schönstem Sonnenschein" };

	public WeatherSkill() {
	}

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language) {
		try {
			// simulate a longer process
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
		List<String> locationNames = this.getLocationNames(contextObject);
		List<String> answers = new ArrayList<>();
		if (!locationNames.isEmpty()) {
			for (int i = 0; i < locationNames.size(); i++) {
				int randomIndex = new Random().nextInt(this.weatherDescriptions.length);
				answers.add(MessageFormat.format("In {0} sind es {1} Grad {2}.", locationNames.get(i),
						ThreadLocalRandom.current().nextInt(-20, 40), this.weatherDescriptions[randomIndex]));
			}
			return new SkillAnswer("SUCCESS", answers, false);
		}
		Map<String, String> openQuestions = new HashMap<>();
		openQuestions.put("weatherLocations", "Von welchem Ort möchten Sie das Wetter wissen?");
		return new SkillAnswer("MISSING_ENTITIES", openQuestions);
	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return intent.toLowerCase().contains("weather");
	}

	@Override
	public void reset() {
		// nothing to do
	}

	private List<String> getLocationNames(JSONObject contextObject) {
		List<String> locationNames = new ArrayList<>();
		try {
			JSONArray weatherLocations = contextObject.getJSONArray("weatherLocations");
			JSONObject locations = weatherLocations.getJSONObject(weatherLocations.length() - 1);
			locationNames.addAll(locations.keySet());
			return locationNames;
		} catch (JSONException e) {
			return locationNames;
		}
	}

	@Override
	public List<String> getExampleRequests(String currentState, Locale locale) {
		ArrayList<String> possibleRequests = new ArrayList<>();
		if ("Mid".equalsIgnoreCase(currentState)) {
			possibleRequests.add("Bitte gib einen Ort an, wie Berlin, Dortmund, Hamburg oder München");
		} else if ("Start".equalsIgnoreCase(currentState)) {
			possibleRequests.add("Wie ist das Wetter in Berlin?");
		}
		return possibleRequests;
	}

}
