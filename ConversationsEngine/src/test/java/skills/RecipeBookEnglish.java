package skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple recipe book singleton to store {@link Recipe recipes}
 * 
 * @author Marcel Engelmann
 *
 */
public class RecipeBookEnglish {
	private Map<String, Recipe> recipes;

	private static RecipeBookEnglish instance;

	/**
	 * Constructor that creates a new {@link RecipeBookEnglish} object with 3
	 * default recipes
	 */
	private RecipeBookEnglish() {
		this.recipes = new HashMap<>();
		this.addRecipe("Pepper with Potatoes and Peas", new String[] { "Pepper", "Potatoes", "Peas" },
				new String[] { "Peel and cook potatoes", "Wash and chop peppers", "Cook everything" });
		this.addRecipe("Bread with salami", new String[] { "Bread", "Salami" },
				new String[] { "Cut bread and slices of salami", "Put salami on the bread", "Eat" });
		this.addRecipe("Pepper-Potatoe-Soup", new String[] { "Pepper", "Potatoes" },
				new String[] { "Peel and cook potatoes", "add peppers", "eat" });
	}

	/**
	 * Returns the current instance of the {@link RecipeBookEnglish}
	 * 
	 * @return the current instance of the {@link RecipeBookEnglish}
	 */
	public static RecipeBookEnglish getInstance() {
		if (instance == null) {
			return new RecipeBookEnglish();
		}
		return instance;
	}

	/**
	 * Adds a new recipe
	 * 
	 * @param name         the name of the recipe
	 * @param ingredients  the required ingredients for the recipe
	 * @param instructions the instructions to cook the recipe
	 */
	public void addRecipe(String name, String[] ingredients, String[] instructions) {
		if (this.recipes.containsKey(name)) {
			throw new IllegalArgumentException("Recipe with the name " + name + " does already exist");
		}
		this.recipes.put(name, new Recipe(name, ingredients, instructions));
	}

	/**
	 *
	 * Adds a new recipe
	 * 
	 * @param recipe the new {@link Recipe recipe} to add
	 * @throws IllegalArgumentException if the recipe already exists
	 */
	public void addRecipe(Recipe recipe) throws IllegalArgumentException {
		if (this.recipes.containsKey(recipe.getName())) {
			throw new IllegalArgumentException("Recipe with the name " + recipe.getName() + " does already exist");
		}
		this.recipes.put(recipe.getName(), recipe);
	}

	/**
	 * Returns a {@link Recipe}
	 * 
	 * @param name the name of the recipe
	 * @return a {@link Recipe} or null, if the requested recipe could not be found
	 */
	public Recipe getRecipe(String name) {
		for (String recipeName : this.recipes.keySet()) {
			if (recipeName.equalsIgnoreCase(name)) {
				return this.recipes.get(recipeName);
			}
		}
		return null;
	}

	/**
	 * Returns a {@link List} of {@link Recipe recipes} that are including the given
	 * ingredients
	 * 
	 * @param ingredients the ingredients that the recipe must contain
	 * @return a {@link List} of {@link Recipe recipes} that are including the given
	 *         ingredients
	 */
	public List<Recipe> getRecipesWithIngredients(String[] ingredients) {
		List<Recipe> foundRecipes = new ArrayList<>();
		for (Entry<String, Recipe> key : this.recipes.entrySet()) {
			Recipe recipe = checkIngredients(ingredients, key.getValue());
			if (recipe != null) {
				foundRecipes.add(recipe);
			}
		}
		return foundRecipes;
	}

	/**
	 * Checks weather the recipe contains all the required ingredients
	 * 
	 * @param ingredients the ingredients that the recipe must contain
	 * @param recipe      the {@link Recipe recipe} to check
	 * @return the {@link Recipe recipe} if the recipe contains all required
	 *         ingredients, returns null otherwise
	 */
	private Recipe checkIngredients(String[] ingredients, Recipe recipe) {
		for (String ingredient : ingredients) {
			if (!Arrays.asList(recipe.getIngredients()).contains(ingredient)) {
				return null;
			}
		}

		return recipe;
	}
}
