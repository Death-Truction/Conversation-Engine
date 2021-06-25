package skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RecipeBook {
	private Map<String, Recipe> recipes;

	private static RecipeBook instance;

	private RecipeBook() {
		this.recipes = new HashMap<>();
		this.addRecipe("Paprika mit Kartoffeln und Erbsen", new String[] { "Paprika", "Kartoffeln", "Erbsen" },
				new String[] { "Kartoffeln schälen und kochen", "Paprika waschen und klein schneiden",
						"Alles kochen" });
		this.addRecipe("Brot mit Salami", new String[] { "Brot", "Salami" }, new String[] {
				"Brot aufschneiden und Salamischeiben abschneiden", "Salami auf das Brot legen", "Essen" });
		this.addRecipe("Paprika-Kartoffelsuppe", new String[] { "Paprika", "Kartoffeln" },
				new String[] { "Kartoffeln schälen und kochen", "Paprika hinzugeben", "futtern" });
	}

	public static RecipeBook getInstance() {
		if (instance == null) {
			return new RecipeBook();
		}
		return instance;
	}

	public void addRecipe(String name, String[] ingredients, String[] instructions) {
		if (this.recipes.containsKey(name)) {
			throw new IllegalArgumentException("Recipe with the name " + name + " does already exist");
		}
		this.recipes.put(name, new Recipe(name, ingredients, instructions));
	}

	public Recipe getRecipe(String name) {
		for (String recipeName : this.recipes.keySet()) {
			if (recipeName.equalsIgnoreCase(name)) {
				return this.recipes.get(recipeName);
			}
		}
		return null;
	}

	public List<Recipe> getRecipes(String[] ingredients) {
		List<Recipe> foundRecipes = new ArrayList<>();
		for (Entry<String, Recipe> key : this.recipes.entrySet()) {
			Recipe recipe = checkIngredients(ingredients, key.getValue());
			if (recipe != null) {
				foundRecipes.add(recipe);
			}
		}
		return foundRecipes;
	}

	private Recipe checkIngredients(String[] ingredients, Recipe recipe) {
		for (String ingredient : ingredients) {
			if (!Arrays.asList(recipe.getIngredients()).contains(ingredient)) {
				return null;
			}
		}

		return recipe;
	}
}
