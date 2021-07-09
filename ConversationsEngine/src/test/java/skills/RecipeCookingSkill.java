package skills;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import interfaces.ISkill;
import interfaces.ISkillAnswer;
import interfaces_implementation.SkillAnswer;

/**
 * A {@link ISkill skill} that checks if all the required ingredients are
 * available and then provides the required instructions to cook the recipe
 * 
 * @author Marcel Engelmann
 *
 */
public class RecipeCookingSkill implements ISkill {

	private int currentInstruction;
	private String lastCookedRecipe = "";

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState) {
		String selectedRecipe = contextObject.optString("selectedRecipe");
		List<String> answers = new ArrayList<>();
		if (selectedRecipe == null || selectedRecipe.isEmpty()) {
			answers.add("Bitte wähle zuerst ein Rezept aus");
			return new SkillAnswer("FAILED", answers, false);
		}
		Recipe recipe = RecipeBook.getInstance().getRecipe(selectedRecipe);
		if (recipe == null) {
			answers.add("Ich konnte das ausgewählte Rezept leider nicht finden!");
			return new SkillAnswer("FAILED", answers, false);
		}

		if (lastCookedRecipe != recipe.getName()) {
			lastCookedRecipe = recipe.getName();
			currentInstruction = 0;
		}

		if (currentState.equalsIgnoreCase("Start") || currentState.equalsIgnoreCase("MissingIngredients")) {
			Map<String, String> questions = new HashMap<>();
			String availableIngredientsEntity = "availableIngredients";
			JSONObject availableIngredients = contextObject.optJSONObject(availableIngredientsEntity);
			for (String ingredient : recipe.getIngredients()) {
				if (availableIngredients == null || !availableIngredients.has(ingredient)) {
					questions.put(availableIngredientsEntity + "." + ingredient,
							"Haben Sie die Zutat " + ingredient + " zu Hause?");
				} else {
					JSONObject ingredientObject = availableIngredients.optJSONObject(ingredient);
					if (ingredientObject.getString("amount").equals("0g")) {
						answers.add(MessageFormat.format(
								"Sie können das Rezept {0} nicht kochen, da Sie die Zutat {1} nicht haben!",
								recipe.getName(), ingredient));
						return new SkillAnswer("FAILED", answers, false);
					}
				}

			}

			if (!questions.isEmpty()) {
				String transitionTrigger = currentState.equalsIgnoreCase("Start") ? "MISSING_ENTITIES" : "STAY";
				return new SkillAnswer(transitionTrigger, questions);
			}
			answers.add("Sie haben alle benötigten Zutaten");
			return new SkillAnswer("SUCCESS", answers, true);
		}

		if (currentState.equalsIgnoreCase("Cooking")) {

			String[] instructions = recipe.getInstructions();
			String stepNumber = (instructions.length - 1 == currentInstruction) ? "letzte"
					: (currentInstruction + 1) + ".";
			answers.add(MessageFormat.format("Der {0} Schritt: {1}", stepNumber, instructions[currentInstruction]));
			currentInstruction++;
			if (stepNumber.equals("letzte")) {
				lastCookedRecipe = "";
				currentInstruction = 0;
				answers.add("Guten Appetit!");
				return new SkillAnswer("SUCCESS", answers, false);
			}

			return new SkillAnswer("STAY", answers, false);
		}
		answers.add("Ich konnte die Anfrage leider nicht bearbeiten!");
		return new SkillAnswer("FAILED", answers, false);

	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return intent.equalsIgnoreCase("recipeCooking") || intent.equalsIgnoreCase("nextRecipeStep");
	}

	@Override
	public void reset() {
		this.lastCookedRecipe = "";
		this.currentInstruction = 0;
	}

}
