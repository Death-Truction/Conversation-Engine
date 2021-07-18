package skills;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import interfaces.ISkill;
import interfaces.ISkillAnswer;
import interfaces_implementation.SkillAnswer;

/**
 * A {@link ISkill skill} that searches for a recipe by requested ingredients
 * 
 * @author Marcel Engelmann
 *
 */
public class RecipeSearchSkillEnglish implements ISkill {

	private RecipeBookEnglish recipes;

	public RecipeSearchSkillEnglish() {
		this.recipes = RecipeBookEnglish.getInstance();
	}

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState) {
		List<String> ingredients = getSearchIngredients(contextObject);
		List<String> answers = new ArrayList<>();
		if (!ingredients.isEmpty()) {
			List<Recipe> foundRecipes = this.recipes.getRecipesWithIngredients(ingredients.toArray(new String[0]));
			if (foundRecipes.isEmpty()) {
				answers.add("Unfortunately I couldn't find any suitable recipes!");
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
			answers.add(MessageFormat.format("I found the following recipes with {0}: {1}",
					String.join(", ", ingredients), recipeNames));
			JSONArray recipeSearchIngredients = contextObject.getJSONArray("recipeSearchIngredients");
			recipeSearchIngredients.put(recipeSearchIngredients.length() - 1, new JSONObject());
			return new SkillAnswer("SUCCESS", answers, false);

		}
		Map<String, String> openQuestions = new HashMap<>();
		openQuestions.put("recipeSearchIngredients", "Which ingredients should the recipe contain?");
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
	public void reset() {
		// nothing to do
	}
}
