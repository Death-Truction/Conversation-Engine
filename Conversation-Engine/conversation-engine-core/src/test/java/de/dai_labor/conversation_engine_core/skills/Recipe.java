package de.dai_labor.conversation_engine_core.skills;

/**
 * Stores a simple recipe for testing purposes
 * 
 * @author Marcel Engelmann
 *
 */
public class Recipe {
	private String name;
	private String[] ingredients;
	private String[] instructions;

	public Recipe(String name, String[] ingredients, String[] instructions) {
		this.name = name;
		this.ingredients = ingredients;
		this.instructions = instructions;
	}

	/**
	 * Returns the name of the {@link Recipe}
	 * 
	 * @return the name of the {@link Recipe}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns an array of the required ingredients for the {@link Recipe}
	 * 
	 * @return an array of the required ingredients for the {@link Recipe}
	 */
	public String[] getIngredients() {
		return ingredients;
	}

	/**
	 * Returns an array of the cooking instructions for the {@link Recipe}
	 * 
	 * @return an array of the cooking instructions for the {@link Recipe}
	 */
	public String[] getInstructions() {
		return instructions;
	}

}
