package de.dai_labor.conversation_engine_core.skills;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_core.interfaces.ISkillAnswer;
import de.dai_labor.conversation_engine_core.interfaces.SkillAnswer;

/**
 * A {@link ISkill skill} that selects a requested recipe by it's name
 * 
 * @author Marcel Engelmann
 *
 */
public class RecipeSelectSkill implements ISkill {

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language) {
		List<String> answers = new ArrayList<>();
		if (!contextObject.has("recipeSelect")) {
			answers.add("Ich konnte das Rezept leider nicht finden!");
			contextObject.put("selectedRecipe", "");
			return new SkillAnswer("FAILED", answers, false);
		}
		JSONArray recipeSelect = contextObject.getJSONArray("recipeSelect");
		String lastRecipe = recipeSelect.optString(recipeSelect.length() - 1);
		if (lastRecipe == null || lastRecipe.isEmpty()) {
			if (contextObject.has("useLastSelectedRecipe")) {
				boolean useLastRecipe = contextObject.getBoolean("useLastSelectedRecipe");
				contextObject.remove("useLastSelectedRecipe");
				if (useLastRecipe) {
					recipeSelect.remove(recipeSelect.length() - 1);
					return execute(intent, contextObject, currentState, language);
				}
				answers.add("Das zuletzt genutzte Rezept wurde NICHT ausgewählt!");
				return new SkillAnswer("FAILED", answers, false);

			}
			Map<String, String> questions = new HashMap<>();
			questions.put("useLastSelectedRecipe", "Wollen Sie das zuletzt genutzte Rezept nutzen?");
			return new SkillAnswer("MISSING_ENTITIES", questions);
		}
		String recipeName = recipeSelect.getString(recipeSelect.length() - 1);
		Recipe recipe = RecipeBook.getInstance().getRecipe(recipeName);
		if (recipe == null) {
			answers.add("Ich konnte das Rezept leider nicht finden!");
			return new SkillAnswer("FAILED", answers, false);
		}
		recipeSelect.put("");
		contextObject.put("selectedRecipe", recipeName);
		answers.add(MessageFormat.format("Das Rezept \"{0}\" wurde erfolgreich ausgewählt.", recipeName));
		return new SkillAnswer("SUCCESS", answers, false);
	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return intent.equalsIgnoreCase("recipeSelect");
	}

	@Override
	public void reset() {
		// nothing to do
	}

	@Override
	public List<String> getExampleRequests(String currentState, Locale locale) {
		ArrayList<String> possibleRequests = new ArrayList<>();
		if ("Mid".equalsIgnoreCase(currentState)) {
			possibleRequests.add("Bitte antworten Sie mit ja oder nein.");
		} else if ("Start".equalsIgnoreCase(currentState)) {
			possibleRequests.add("Bitte wähle das Rezept " + RecipeBook.getInstance().getRandomRecipeName() + " aus.");
		}
		return possibleRequests;
	}
}
