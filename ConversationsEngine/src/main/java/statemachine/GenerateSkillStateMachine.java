package statemachine;

import java.util.ArrayList;
import java.util.List;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import interfaces.INLPComponent;
import interfaces.ISkill;

/**
 * Utility class for generating {@link SkillStateMachine SkillStateMachines}
 * 
 * @author Marcel Engelmann
 *
 */
class GenerateSkillStateMachine {

	private GenerateSkillStateMachine() throws IllegalStateException {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Generate a new skill state machine from a JSON file
	 * 
	 * @param skill                 new skill to generate a state machine for
	 * @param jsonSkillStateMachine the JSON-String for the skill's state machine.
	 *                              For the correct syntax check out the <a href=
	 *                              "file:../../resources/SkillStateMachine_Schema.json">Schema.json</a>
	 *                              file
	 * @param nlpComponent          the used NLP-Component to add the skill's used
	 *                              intents and entities
	 * @return a new SkillStateMachine object
	 */
	static SkillStateMachine fromJson(ISkill skill, String jsonSkillStateMachine, INLPComponent nlpComponent) {
		JSONObject skillStateMachine;
		try {
			skillStateMachine = new JSONObject(new JSONTokener(jsonSkillStateMachine));
		} catch (JSONException ex) {
			Logging.error("Invalid JSON-String: {}", ex.getLocalizedMessage());
			return null;
		}

		if (!validJsonSkillStateMachine(skillStateMachine)) {
			return null;
		}

		List<State> states = getStates(skillStateMachine);
		boolean hasErrors = false;
		String startStateName = skillStateMachine.getString("startAt");
		String endStateName = skillStateMachine.getString("endAt");
		String name = skillStateMachine.getString("name");

		State startState = getStateByName(startStateName, states);
		State endState = getStateByName(endStateName, states);

		if (!successfullyAddedTransitions(skillStateMachine, states)) {
			hasErrors = true;
		}
		if (startState == null) {
			Logging.error("Could not find the defined startAt state {} in the list of all defined states",
					startStateName);
			hasErrors = true;
		}
		if (endState == null) {
			Logging.error("Could not find the defined endAt state {} in the list of all defined states", endStateName);
			hasErrors = true;
		}

		if (name.isBlank()) {
			Logging.error("The name of the skill is empty");
			hasErrors = true;
		}

		if (hasErrors) {
			return null;
		}

		List<String> usedEntities = getUsedEntities(skillStateMachine);
		List<String> usedIntents = getUsedIntents(skillStateMachine);
		nlpComponent.addUsedEntities(usedEntities);
		nlpComponent.addUsedIntents(usedIntents);

		return new SkillStateMachine(name, startState, endState, skill);
	}

	/**
	 * Get the skill's used entities from the skill's {@link JSONObject}
	 * 
	 * @param skillStateMachine the {@link JSONObject} of the skill's new state
	 *                          machine
	 * @return a list of entities
	 */
	private static List<String> getUsedEntities(JSONObject skillStateMachine) {
		JSONArray entitiesArray = skillStateMachine.getJSONArray("usedEntities");
		List<String> usedEntities = new ArrayList<>();
		for (int i = 0; i < entitiesArray.length(); i++) {
			usedEntities.add(entitiesArray.getString(i));
		}
		return usedEntities;
	}

	/**
	 * Get the skill's used intents from the skill's {@link JSONObject}
	 * 
	 * @param skillStateMachine the {@link JSONObject} of the skill's new state
	 *                          machine
	 * @return a list of intents
	 */
	private static List<String> getUsedIntents(JSONObject skillStateMachine) {
		JSONArray intentsArray = skillStateMachine.getJSONArray("usedIntents");
		List<String> usedIntents = new ArrayList<>();
		for (int i = 0; i < intentsArray.length(); i++) {
			usedIntents.add(intentsArray.getString(i));
		}
		return usedIntents;
	}

	/**
	 * Tries to add all defined transitions to the correct states of the skill's new
	 * state machine
	 * 
	 * @param skillStateMachine the {@link JSONObject} of the skill's new state
	 *                          machine
	 * @param states            the list of {@link State States} found in the
	 *                          skill's {@link JSONObject}
	 * @return true if all transitions were added successfully
	 */
	private static boolean successfullyAddedTransitions(JSONObject skillStateMachine, List<State> states) {
		int index = 0;
		boolean noErrors = true;
		for (Object t : skillStateMachine.getJSONArray("transitions")) {
			JSONObject transition = (JSONObject) t;
			State source = getStateByName(transition.getString("source"), states);
			if (source == null) {
				Logging.error("Cannot find the source state {} in transition #{}", transition.getString("source"),
						index);
				noErrors = false;
			}
			State target = getStateByName(transition.getString("target"), states);
			if (target == null) {
				Logging.error("Cannot find the target state {} in transition #{}", transition.getString("target"),
						index);
				noErrors = false;
			}
			String trigger = transition.getString("trigger");
			index++;
			if (source != null && target != null) {
				source.addTransition(new Transition(target, trigger));
			}
		}
		return noErrors;
	}

	/**
	 * Returns the list of {@link State States} found in the skill's
	 * {@link JSONObject}
	 * 
	 * @param skillStateMachine the {@link JSONObject} of the skill's new state
	 *                          machine
	 * @return the list of {@link State States} found in the skill's
	 *         {@link JSONObject}
	 */
	private static List<State> getStates(JSONObject skillStateMachine) {
		List<State> states = new ArrayList<>();

		for (Object state : skillStateMachine.getJSONArray("states")) {
			states.add(new State(((JSONObject) state).getString("name")));
		}

		return states;

	}

	/**
	 * Finds a {@link State} in a given list by a given name
	 * 
	 * @param name   the name of the state to be found
	 * @param states the list of {@link State States} to search for the wanted
	 *               {@link State}
	 * @return the {@link State} corresponding to the given name or null if the
	 *         {@link State} was not found
	 */
	private static State getStateByName(String name, List<State> states) {
		for (State state : states) {
			if (state.getName().equals(name)) {
				return state;
			}
		}
		return null;
	}

	/**
	 * Validates the {@link JSONObject} of the skill's new state machine with this
	 * <a href= "file:../../resources/SkillStateMachine_Schema.json">JSON-Schema</a>
	 * 
	 * @param skillStateMachine the {@link JSONObject} of the skill's new state
	 *                          machine
	 * @return true if the given {@link JSONObject} is valid
	 */
	private static boolean validJsonSkillStateMachine(JSONObject skillStateMachine) {

		// load JSON file to use as validation schema
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		JSONObject jsonSchema = new JSONObject(
				new JSONTokener(classLoader.getResourceAsStream("SkillStateMachine_Schema.json")));

		Schema schema = SchemaLoader.load(jsonSchema);
		try {
			schema.validate(skillStateMachine);
		} catch (ValidationException ex) {
			Logging.error("Invalid SkillStateMachine JSON format: {}", buildExceptionString(ex));
			return false;
		}

		return true;

	}

	/**
	 * Creates a String with the exception message of all nested
	 * {@link ValidationException ValidationExceptions}
	 * 
	 * @param validationException the occurred exception to build a message from
	 * @return a String with the exception message of all nested
	 *         {@link ValidationException ValidationExceptions}
	 */
	private static String buildExceptionString(ValidationException validationException) {
		StringBuilder errorMessage = new StringBuilder();
		for (ValidationException ex : validationException.getCausingExceptions()) {
			errorMessage.append(buildExceptionString(ex));
		}
		if (validationException.getCausingExceptions().isEmpty()) {
			errorMessage.append("\n");
			errorMessage.append(validationException.getMessage());
		}
		return errorMessage.toString();
	}
}
