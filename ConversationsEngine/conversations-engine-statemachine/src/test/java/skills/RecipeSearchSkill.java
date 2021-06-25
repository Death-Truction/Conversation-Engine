package skills;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.SkillAnswer;
import interfaces.ISkill;
import interfaces.ISkillAnswer;

public class RecipeSearchSkill implements ISkill {

	private RecipeBook recipes;

	public RecipeSearchSkill() {
		this.recipes = RecipeBook.getInstance();
	}

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, JSONObject newEntities, String currentState) {
		List<String> ingredients = getSearchIngredients(contextObject);
		List<String> answers = new ArrayList<>();
		if (!ingredients.isEmpty()) {
			List<Recipe> foundRecipes = this.recipes.getRecipes(ingredients.toArray(new String[0]));
			if (foundRecipes.isEmpty()) {
				answers.add("Ich konnte leider keine passende Rezepte finden!");
				return new SkillAnswer("FAILED", answers, false);
			}
			StringBuilder bld = new StringBuilder();
			for (Recipe recipe : foundRecipes) {
				if (bld.length() == 0) {
					bld.append(recipe.getName());
				} else {
					bld.append(", ");
					bld.append(recipe.getName());
				}
			}
			String recipeNames = bld.toString();
			answers.add(MessageFormat.format("Ich habe folgende Rezepte mit {0} gefunden: {1}",
					String.join(", ", ingredients), recipeNames));
			JSONArray recipeSearchIngredients = contextObject.getJSONArray("recipeSearchIngredients");
			recipeSearchIngredients.put(recipeSearchIngredients.length() - 1, new JSONObject());
			return new SkillAnswer("SUCCESS", answers, false);

		}
		Map<String, String> openQuestions = new HashMap<>();
		openQuestions.put("recipeSearchIngredients", "Welche Zutaten soll das Rezept beinhalten?");
		return new SkillAnswer("MISSING_ENTITIES", openQuestions);
	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return intent.equalsIgnoreCase("recipeSearch");
	}

	private List<String> getSearchIngredients(JSONObject contextObject) {
		List<String> ingredientNames = new ArrayList<>();
		try {
			JSONArray recipeSearchIngredients = contextObject.getJSONArray("recipeSearchIngredients");
			JSONObject ingredients = recipeSearchIngredients.getJSONObject(recipeSearchIngredients.length() - 1);
			for (String ingredientName : ingredients.keySet()) {
				ingredientNames.add(ingredientName);
			}
			return ingredientNames;
		} catch (JSONException e) {
			return ingredientNames;
		}
	}

	@Override
	public void abort() {
		// nothing to do
	}
}
