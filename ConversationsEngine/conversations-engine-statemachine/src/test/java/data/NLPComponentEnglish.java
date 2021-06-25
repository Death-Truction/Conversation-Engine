package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import interfaces.INLPAnswer;
import interfaces.INLPComponent;

//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;

public class NLPComponentEnglish implements INLPComponent {

	private String[] possibleCities = new String[] { "Berlin", "Dortmund", "München", "Hamburg" };
	private String[] possibleIngredients = new String[] { "Paprika", "Kartoffeln", "Salami", "Brot", "Erbsen",
			"Toast" };

	private String[] greetingSynonyms = new String[] { "Hi", "Hallo", "Guten Tag" };
	private String[] weatherSynonyms = new String[] { "Wetter", "Grad", "Temperatur" };
	private String[] recipeSynonyms = new String[] { "Rezepte mit", "Was für ein Rezept", "Welche Rezepte", "Essen" };
	private String[] cookingSynonyms = new String[] { "koche", "zubereiten" };

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
		List<String> intents = new ArrayList<>();
		JSONObject newEntities = new JSONObject();
		input = input.toLowerCase(Locale.GERMAN);

		if (!entityName.isEmpty()) {
			// TODO: use for cooking recipe, when asking if ingredient is available
			if (entityName.contains("availableIngredients")) {
				String[] entityPath = entityName.split("\\.");
				if (input.contains("ja")) {
					addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), newEntities, -1);
				} else if (input.contains("nein")) {
					addAvailableIngredients(entityPath[0], entityPath[1].toLowerCase(), newEntities, 0);
				}

			} else if (entityName.contains("recipeSearchIngredients")) {
				String[] entityPath = entityName.split("\\.");
				if (input.contains("ja")) {
					addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), newEntities, -1);
				} else if (input.contains("nein")) {
					addSearchIngredients(entityPath[0], entityPath[1].toLowerCase(), newEntities, 0);
				}
			} else if (entityName.equalsIgnoreCase("useLastSelectedRecipe")) {
				if (input.contains("ja")) {
					setUseLastSelectedRecipe(true, newEntities);
				} else if (input.contains("nein")) {
					setUseLastSelectedRecipe(false, newEntities);
				}
			}
		}

		addIntents(input, intents, newEntities);

		if (intents.isEmpty()) {
			if (entityName.isBlank()) {
				addLocations("locations", input, newEntities);
				addAvailableIngredients("availableIngredients", input, newEntities, -1);
			} else {
				addWeatherLocations("weatherLocations", input, newEntities);
				addSearchIngredients("recipeSearchIngredients", input, newEntities, -1);
			}
		}

		mergeJsonObjects(contextObject, newEntities);

		return new NLPAnswer(intents, newEntities, new Locale("en", "US"));
	}

	private void setUseLastSelectedRecipe(boolean b, JSONObject contextObject) {
		contextObject.put("useLastSelectedRecipe", b);
	}

	private void addIntents(String input, List<String> intents, JSONObject contextObject) {
		if (Arrays.stream(greetingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("greeting");
		}

		if (Arrays.stream(weatherSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("weather");
			addWeatherLocations("weatherLocations", input, contextObject);
		}

		if (Arrays.stream(recipeSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeSearch");
			addSearchIngredients("recipeSearchIngredients", input, contextObject, -1);
		}

		if (input.contains("das rezept")) {
			intents.add("recipeSelect");
			try {
				// get recipe name but ignore the word 'kochen' at the end of the input
				String recipeName = input.split("das rezept | kochen")[1].trim();
				addSelectedRecipe(recipeName, contextObject);
			} catch (ArrayIndexOutOfBoundsException ex) {
				// no recipe name found
			}
		}

		if (Arrays.stream(cookingSynonyms).anyMatch(element -> input.contains(element.toLowerCase()))) {
			intents.add("recipeCooking");
		}

		if (input.contains("nächster schritt")) {
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
				;
				JSONObject locations;
				if (weatherLocations.length() == 0) {
					locations = new JSONObject();
				} else {
					locations = weatherLocations.getJSONObject(weatherLocations.length() - 1);
				}
				locations.put(possibleCity, city);
				weatherLocations.put(locations);
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
	}

	private void mergeJsonObjects(JSONObject firstObject, JSONObject secondObject) {
		for (String key : secondObject.keySet()) {
			if (firstObject.optJSONObject(key) != null) {
				mergeJsonObjects(firstObject.getJSONObject(key), secondObject.getJSONObject(key));
			} else if (firstObject.optJSONArray(key) != null) {
				mergeJsonArrays(firstObject.getJSONArray(key), secondObject.getJSONArray(key));
			} else {
				firstObject.put(key, secondObject.get(key));
			}
		}
	}

	private void mergeJsonArrays(JSONArray firstArray, JSONArray secondArray) {
		for (Object element : secondArray) {
			firstArray.put(element);
		}
	}

}
