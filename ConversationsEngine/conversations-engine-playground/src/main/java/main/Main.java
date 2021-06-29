package main;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONTokener;

import data.NLPComponent;
import skills.GreetingSkill;
import skills.RecipeCookingSkill;
import skills.RecipeSearchSkill;
import skills.RecipeSelectSkill;
import skills.WeatherSkill;
import statemachine.ConversationsEngine;

public class Main {

	public static void main(String[] args) {
		ConversationsEngine stateMachine = setUp();
		Scanner scanner = new Scanner(System.in);
		System.out.println("\u001B[36mWillkommen zum Playground der ConversationsEngine!");
		System.out.println("Geben sie quit ein, um den Playground zu verlassen.");
		System.out.println("Sie können jetzt Ihre Anfragen stellen.\u001B[0m");
		while (true) {
			String userInput = scanner.nextLine();
			if (userInput.strip().equalsIgnoreCase("quit")) {
				break;
			}
			List<String> answers = stateMachine.userInput(userInput);
			System.out.println("\u001B[32m" + String.join("\n", answers) + "\u001B[0m");
		}

		scanner.close();
		System.exit(0);
	}

	private static ConversationsEngine setUp() {
		NLPComponent nlp = new NLPComponent();
		ConversationsEngine stateMachine = new ConversationsEngine(nlp);
		GreetingSkill greeting = new GreetingSkill();
		String greetingSkillSM = loadJsonFileAsString("Greeting.json");
		stateMachine.addSkill(greeting, greetingSkillSM);
		RecipeCookingSkill recipeCooking = new RecipeCookingSkill();
		String recipeCookingSkillSM = loadJsonFileAsString("RecipeCooking.json");
		stateMachine.addSkill(recipeCooking, recipeCookingSkillSM);
		RecipeSearchSkill recipeSearch = new RecipeSearchSkill();
		String recipeSearchSkillSM = loadJsonFileAsString("RecipeSearch.json");
		stateMachine.addSkill(recipeSearch, recipeSearchSkillSM);
		RecipeSelectSkill recipeSelect = new RecipeSelectSkill();
		String recipeSelectSkillSM = loadJsonFileAsString("RecipeSelect.json");
		stateMachine.addSkill(recipeSelect, recipeSelectSkillSM);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillSM = loadJsonFileAsString("Weather.json");
		stateMachine.addSkill(weather, weatherSkillSM);
		WeatherSkill weather2 = new WeatherSkill();
		String weatherSkillSM2 = loadJsonFileAsString("Weather2.json");
		stateMachine.addSkill(weather2, weatherSkillSM2);
		return stateMachine;
	}

	static String loadJsonFileAsString(String fileName) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		JSONObject json = new JSONObject(new JSONTokener(inputStream));
		return json.toString();
	}

}
