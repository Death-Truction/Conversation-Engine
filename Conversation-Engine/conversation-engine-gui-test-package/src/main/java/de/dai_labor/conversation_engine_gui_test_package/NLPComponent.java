package de.dai_labor.conversation_engine_gui_test_package;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.INLPAnswer;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;

/**
 * A simple implementation of a {@link INLPComponent} for testing purposes
 *
 * @author Marcel Engelmann
 *
 */
public class NLPComponent implements INLPComponent {

	private String[] possibleCities = new String[] { "Berlin", "Dortmund", "München", "Hamburg" };
	private String[] possibleIngredients = new String[] { "Paprika", "Kartoffeln", "Salami", "Brot", "Erbsen",
			"Toast" };

	private String[] greetingSynonyms = new String[] { "Hi", "Hallo", "Guten Tag" };
	private String[] weatherSynonyms = new String[] { "Wetter", "Grad", "Temperatur" };
	private String[] recipeSynonyms = new String[] { "Rezepte mit", "Was für ein Rezept", "Welche Rezepte", "Essen" };
	private String[] cookingSynonyms = new String[] { "koche", "zubereiten" };

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
			if (input.contains("ja")) {
				this.addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, -1);
			} else if (input.contains("nein")) {
				this.addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, 0);
			}

		} else if (entityName.contains("recipeSearchIngredients")) {
			String[] entityPath = entityName.split("\\.");
			if (input.contains("ja")) {
				this.addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, -1);
			} else if (input.contains("nein")) {
				this.addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), contextObject, 0);
			}
		} else if (entityName.equalsIgnoreCase("useLastSelectedRecipe")) {
			if (input.contains("ja")) {
				this.setUseLastSelectedRecipe(true, contextObject);
			} else if (input.contains("nein")) {
				this.setUseLastSelectedRecipe(false, contextObject);
			}
		}

		this.addIntentsAndEntities(input, intents, contextObject);

		if (intents.isEmpty()) {
			if (entityName.isBlank()) {
				this.addLocations("locations", input, contextObject);
				this.addAvailableIngredients("availableIngredients", input, contextObject, -1);
			} else {
				this.addWeatherLocations("weatherLocations", input, contextObject);
				this.addSearchIngredients("recipeSearchIngredients", input, contextObject, -1);
			}
		}

		return new NLPAnswer(intents, new Locale("de", "DE"), this.addedNewEntities);
	}

	@Override
	public INLPAnswer understandInput(String input, JSONObject contextObject) {
		this.addedNewEntities = false;
		List<String> intents = new ArrayList<>();
		input = input.toLowerCase(Locale.GERMAN);

		this.addIntentsAndEntities(input, intents, contextObject);

		if (intents.isEmpty()) {
			this.addLocations("locations", input, contextObject);
			this.addAvailableIngredients("availableIngredients", input, contextObject, -1);
		}

		return new NLPAnswer(intents, new Locale("de", "DE"), this.addedNewEntities);
	}

	private void setUseLastSelectedRecipe(boolean b, JSONObject contextObject) {
		contextObject.put("useLastSelectedRecipe", b);
		this.addedNewEntities = true;

	}

	private void addIntentsAndEntities(String input, List<String> intents, JSONObject contextObject) {
		// if no entities were added -> try to match the trigger intents
		if (!this.addedNewEntities) {
			if (input.equalsIgnoreCase("abbruch")) {
				intents.add("abort");
				return;
			}
			if (input.equalsIgnoreCase("ja")) {
				intents.add("yes");
				return;
			}
			if (input.equalsIgnoreCase("nein")) {
				intents.add("no");
				return;
			}
			if (input.equalsIgnoreCase("letzten")) {
				intents.add("last");
				return;
			}
			if (input.equalsIgnoreCase("alle")) {
				intents.add("all");
				return;
			}
		}
		if (Arrays.stream(this.greetingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("greeting");
		}

		if (Arrays.stream(this.weatherSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("weather");
			this.addWeatherLocations("weatherLocations", input, contextObject);
		}

		if (Arrays.stream(this.recipeSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeSearch");
			this.addSearchIngredients("recipeSearchIngredients", input, contextObject, -1);
		}

		if (input.contains("das rezept")) {
			intents.add("recipeSelect");
			try {
				// get recipe name but ignore the word 'kochen' at the end of the input
				String recipeName = input.split("das rezept | kochen")[1].trim();
				this.addSelectedRecipe(recipeName, contextObject);
			} catch (ArrayIndexOutOfBoundsException ex) {
				// no recipe name found
			}
		}

		if (Arrays.stream(this.cookingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeCooking");
		}

		if (input.contains("nächster schritt")) {
			intents.add("nextRecipeStep");
		}
	}

	private void addWeatherLocations(String entityName, String input, JSONObject contextObject) {

		Arrays.stream(this.possibleCities).forEach(possibleCity -> {
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
				this.addedNewEntities = true;
			}
		});

	}

	private void addLocations(String entityName, String input, JSONObject contextObject) {

		Arrays.stream(this.possibleCities).forEach(possibleCity -> {
			if (input.contains(possibleCity.toLowerCase())) {
				JSONObject city = new JSONObject();

				city.put("country", "Germany");
				if (contextObject.optJSONObject(entityName) == null) {
					contextObject.put(entityName, new JSONObject());
				}
				contextObject.getJSONObject(entityName).put(possibleCity, city);
				this.addedNewEntities = true;
			}
		});

	}

	private void addSearchIngredients(String entityName, String input, JSONObject contextObject, int amount) {
		Arrays.stream(this.possibleIngredients).forEach(availableIngredient -> {
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
				this.addedNewEntities = true;
			}
		});

	}

	private void addAvailableIngredients(String entityName, String input, JSONObject contextObject, int amount) {
		Arrays.stream(this.possibleIngredients).forEach(availableIngredient -> {
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
				this.addedNewEntities = true;
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
		this.addedNewEntities = true;
	}

}
