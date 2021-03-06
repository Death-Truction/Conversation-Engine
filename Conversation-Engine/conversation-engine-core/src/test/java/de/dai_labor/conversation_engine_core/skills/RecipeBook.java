package de.dai_labor.conversation_engine_core.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * A simple recipe book singleton to store {@link Recipe recipes}
 * 
 * @author Marcel Engelmann
 *
 */
public class RecipeBook {
	private Map<String, Recipe> recipes;

	private static RecipeBook instance;

	/**
	 * Constructor that creates a new {@link RecipeBook} object with 3 default
	 * recipes
	 */
	private RecipeBook() {
		this.recipes = new HashMap<>();
		this.addRecipe("Paprika mit Kartoffeln und Erbsen", new String[] { "Paprika", "Kartoffeln", "Erbsen" },
				new String[] { "Kartoffeln schälen und kochen", "Paprika waschen und klein schneiden",
						"Alles kochen" });
		this.addRecipe("Brot mit Salami", new String[] { "Brot", "Salami" }, new String[] {
				"Brot aufschneiden und Salamischeiben abschneiden", "Salami auf das Brot legen", "Essen" });
		this.addRecipe("Paprika-Kartoffelsuppe", new String[] { "Paprika", "Kartoffeln" },
				new String[] { "Kartoffeln schälen und kochen", "Paprika hinzugeben", "Essen" });
	}

	/**
	 * Returns the current instance of the {@link RecipeBook}
	 * 
	 * @return the current instance of the {@link RecipeBook}
	 */
	public static RecipeBook getInstance() {
		if (instance == null) {
			return new RecipeBook();
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
	 * Returns the name of a randomly selected {@link Recipe}
	 * 
	 * @return the name of a randomly selected {@link Recipe}
	 */
	public String getRandomRecipeName() {
		if (this.recipes.isEmpty()) {
			return null;
		}
		return (String) this.recipes.keySet().toArray()[new Random().nextInt(this.recipes.size())];
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
	 * Checks whether the recipe contains all the required ingredients
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
