package interfaces_implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import interfaces.INLPAnswer;
import interfaces.INLPComponent;

/**
 * A simple implementation of a {@link INLPComponent} for testing purposes
 * 
 * @author Marcel Engelmann
 *
 */
public class NLPComponentEnglish implements INLPComponent {

	private String[] possibleCities = new String[] { "Berlin", "Dortmund", "Munich", "Hamburg" };
	private String[] possibleIngredients = new String[] { "Pepper", "Potatoes", "Salami", "Bread", "Peas", "Toast" };

	private String[] greetingSynonyms = new String[] { "Hi", "Hello", "Good Evening" };
	private String[] weatherSynonyms = new String[] { "Weather", "Degree", "Temperature" };
	private String[] recipeSearchSynonyms = new String[] { "Recipes with", "Recipe with", "Which recipe", "What recipe",
			"Food" };
	private String[] cookingSynonyms = new String[] { "cook" };

	private boolean addedNewEntities;

	@Override
	public void addUsedEntities(List<String> entities) {
		// ignored in this emulated NLPComponent
	}

	@Override
	public void addUsedIntents(List<String> intentNames) {
		// ignored in this emulated NLPComponent
	}

	@Override
	public INLPAnswer understandInput(String input, String entityName, JSONObject contextObject) {
		this.addedNewEntities = false;
		List<String> intents = new ArrayList<>();
		input = input.toLowerCase(Locale.GERMAN);

		if (entityName.contains("availableIngredients")) {
			String[] entityPath = entityName.split("\\.");
			if (input.contains("yes")) {
				addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, -1);
			} else if (input.contains("no")) {
				addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, 0);
			}

		} else if (entityName.contains("recipeSearchIngredients")) {
			String[] entityPath = entityName.split("\\.");
			if (input.contains("yes")) {
				addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, -1);
			} else if (input.contains("no")) {
				addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, 0);
			}
		} else if (entityName.equalsIgnoreCase("useLastSelectedRecipe")) {
			if (input.contains("yes")) {
				setUseLastSelectedRecipe(true, contextObject);
			} else if (input.contains("no")) {
				setUseLastSelectedRecipe(false, contextObject);
			}
		}

		addIntentsAndEntities(input, intents, contextObject);

		if (intents.isEmpty()) {
			if (entityName.isBlank()) {
				addLocations("locations", input, contextObject);
				addAvailableIngredients("availableIngredients", input, contextObject, -1);
			} else {
				addWeatherLocations("weatherLocations", input, contextObject);
				addSearchIngredients("recipeSearchIngredients", input, contextObject, -1);
			}
		}

		return new NLPAnswer(intents, new Locale("en", "US"), this.addedNewEntities);
	}

	@Override
	public INLPAnswer understandInput(String input, JSONObject contextObject) {
		this.addedNewEntities = false;
		List<String> intents = new ArrayList<>();
		input = input.toLowerCase(Locale.GERMAN);

		addIntentsAndEntities(input, intents, contextObject);

		if (intents.isEmpty()) {
			addLocations("locations", input, contextObject);
			addAvailableIngredients("availableIngredients", input, contextObject, -1);
		}

		return new NLPAnswer(intents, new Locale("en", "US"), this.addedNewEntities);
	}

	private void setUseLastSelectedRecipe(boolean b, JSONObject contextObject) {
		contextObject.put("useLastSelectedRecipe", b);
		addedNewEntities = true;

	}

	private void addIntentsAndEntities(String input, List<String> intents, JSONObject contextObject) {
		// if no entities were added -> try to match the trigger intents
		if (!this.addedNewEntities) {
			if (input.equalsIgnoreCase("abort")) {
				intents.add("abort");
				return;
			}
			if (input.equalsIgnoreCase("yes")) {
				intents.add("yes");
				return;
			}
			if (input.equalsIgnoreCase("no")) {
				intents.add("no");
				return;
			}
			if (input.equalsIgnoreCase("last")) {
				intents.add("last");
				return;
			}
			if (input.equalsIgnoreCase("all")) {
				intents.add("all");
				return;
			}
		}
		if (Arrays.stream(greetingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("greeting");
		}

		if (Arrays.stream(weatherSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("weather");
			addWeatherLocations("weatherLocations", input, contextObject);
		}

		if (Arrays.stream(recipeSearchSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeSearch");
			addSearchIngredients("recipeSearchIngredients", input, contextObject, -1);
		}

		if (input.contains("the recipe")) {
			intents.add("recipeSelect");
			try {
				String recipeName = input.split("the recipe ")[1].trim();
				addSelectedRecipe(recipeName, contextObject);
			} catch (ArrayIndexOutOfBoundsException ex) {
				// no recipe name found
			}
		}

		if (Arrays.stream(cookingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeCooking");
		}

		if (input.contains("next step")) {
			intents.add("nextRecipeStep");
		}
	}

	private void addWeatherLocations(String entityName, String input, JSONObject contextObject) {

		Arrays.stream(possibleCities).forEach(possibleCity -> {
			if (input.contains(possibleCity.toLowerCase())) {
				JSONObject city = new JSONObject();
				city.put("country", "Germany");

				if (contextObject.optJSONArray(entityName) == null) {
					contextObject.put(entityName, new JSONArray());
				}

				JSONArray weatherLocations = contextObject.getJSONArray(entityName);
				JSONObject locations;
				if (weatherLocations.length() == 0) {
					locations = new JSONObject();
				} else {
					locations = weatherLocations.getJSONObject(weatherLocations.length() - 1);
				}
				locations.put(possibleCity, city);
				weatherLocations.put(locations);
				addedNewEntities = true;
			}
		});

	}

	private void addLocations(String entityName, String input, JSONObject contextObject) {

		Arrays.stream(possibleCities).forEach(possibleCity -> {
			if (input.contains(possibleCity.toLowerCase())) {
				JSONObject city = new JSONObject();

				city.put("country", "Germany");
				if (contextObject.optJSONObject(entityName) == null) {
					contextObject.put(entityName, new JSONObject());
				}
				contextObject.getJSONObject(entityName).put(possibleCity, city);
				addedNewEntities = true;
			}
		});

	}

	private void addSearchIngredients(String entityName, String input, JSONObject contextObject, int amount) {
		Arrays.stream(possibleIngredients).forEach(availableIngredient -> {
			if (input.contains(availableIngredient.toLowerCase())) {
				JSONObject ingredient = new JSONObject();
				String amountString;
				if (amount == -1) {
					amountString = Integer.toString(ThreadLocalRandom.current().nextInt(1, 5000));
				} else {
					amountString = Integer.toString(amount);
				}

				ingredient.put("amount", amountString + "g");
				if (contextObject.optJSONArray(entityName) == null) {
					contextObject.put(entityName, new JSONArray());
				}
				// add ingredients to last search (if the last search was completed, an empty
				// object is placed at the end of the array)
				JSONArray searchIngredientsArray = contextObject.getJSONArray(entityName);
				JSONObject ingredients;
				if (searchIngredientsArray.length() == 0) {
					ingredients = new JSONObject();
				} else {
					ingredients = searchIngredientsArray.getJSONObject(searchIngredientsArray.length() - 1);
				}
				ingredients.put(availableIngredient, ingredient);
				searchIngredientsArray.put(ingredients);
				addedNewEntities = true;
			}
		});

	}

	private void addAvailableIngredients(String entityName, String input, JSONObject contextObject, int amount) {
		Arrays.stream(possibleIngredients).forEach(availableIngredient -> {
			if (input.contains(availableIngredient.toLowerCase())) {
				JSONObject ingredient = new JSONObject();
				String amountString;
				if (amount == -1) {
					amountString = Integer.toString(ThreadLocalRandom.current().nextInt(1, 5000));
				} else {
					amountString = Integer.toString(amount);
				}

				ingredient.put("amount", amountString + "g");
				if (contextObject.optJSONObject(entityName) == null) {
					contextObject.put(entityName, new JSONObject());
				}
				contextObject.getJSONObject(entityName).put(availableIngredient, ingredient);
				addedNewEntities = true;
			}
		});

	}

	private void addSelectedRecipe(String recipeName, JSONObject contextObject) {
		String entityName = "recipeSelect";
		JSONArray recipeSelect;
		if (contextObject.optJSONArray(entityName) == null) {
			recipeSelect = new JSONArray();
			contextObject.put(entityName, recipeSelect);
		} else {
			recipeSelect = contextObject.getJSONArray(entityName);
		}
		if (recipeSelect.length() > 0 && recipeSelect.get(recipeSelect.length() - 1).getClass().equals(String.class)) {
			recipeSelect.remove(recipeSelect.length() - 1);
		}
		recipeSelect.put(recipeName);
		addedNewEntities = true;
	}

}
