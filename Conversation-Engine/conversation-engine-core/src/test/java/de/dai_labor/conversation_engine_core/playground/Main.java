package de.dai_labor.conversation_engine_core.playground;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.NLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.NLPComponentEnglish;
import de.dai_labor.conversation_engine_core.skills.GreetingSkill;
import de.dai_labor.conversation_engine_core.skills.GreetingSkillEnglish;
import de.dai_labor.conversation_engine_core.skills.RecipeCookingSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeCookingSkillEnglish;
import de.dai_labor.conversation_engine_core.skills.RecipeSearchSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeSearchSkillEnglish;
import de.dai_labor.conversation_engine_core.skills.RecipeSelectSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeSelectSkillEnglish;
import de.dai_labor.conversation_engine_core.skills.WeatherSkill;
import de.dai_labor.conversation_engine_core.skills.WeatherSkillEnglish;

public class Main {

	private static final String menuString = "\u001B[36mWelcome to the Conversation Engine''s playground!\u001B[0m\n"
			+ "\u001B[36m\033[3mType the corresponding number to select an action from the menu\033[0m\u001B[0m\n\n"
			+ "\u001B[36m---- Menu ----\u001B[0m\n"
			+ "\t\u001B[36m[1] Select language  || currently selected language: {0}\u001B[0m\n"
			+ "\t\u001B[36m[2] Change the logging level  || currently selected logging level: {1}\u001B[0m\n"
			+ "\t\u001B[36m[3] Enter the playground\u001B[0m\n" + "\t\u001B[36m[4] quit\u001B[0m";
	private static final String languageMenuString = "\u001B[36m---- Language-Menu ----\u001B[0m\n"
			+ "\u001B[36mCurrently selected language: {0}\u001B[0m\n" + "\t\u001B[36m[1] German | Deutsch\n"
			+ "\t\u001B[36m[2] English | Englisch\n" + "\t\u001B[36m[3] quit\u001B[0m";
	private static final String loggingMenuString = "\u001B[36m---- Logging-Menu ----\u001B[0m\n"
			+ "\u001B[36mCurrently selected logging level: {0}\u001B[0m\n" + "\t\u001B[36m[1] Debug\n"
			+ "\t\u001B[36m[2] Info\n" + "\t\u001B[36m[3] Warn\u001B[0m\n" + "\t\u001B[36m[4] Error\u001B[0m\n"
			+ "\t\u001B[36m[5] quit\u001B[0m";
	private static String selectedLanguage = "German";
	private static String loggingLevel = "Debug";
	private static Scanner scanner;

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		startMenu();
		scanner.close();
		System.exit(0);
	}

	private static void startMenu() {
		while (true) {
			System.out.println(MessageFormat.format(menuString, selectedLanguage, loggingLevel));
			String userInput = getNextUserInput();
			switch (userInput) {
			case "1":
				languageMenu();
				break;
			case "2":
				changeLoggingLevelMenu();
				break;
			case "3":
				setLoggingLevel();
				if ("German".equalsIgnoreCase(selectedLanguage)) {
					playgroundGerman();
				} else {
					playgroundEnglish();
				}
				return;
			case "4":
				return;
			}
		}
	}

	private static void setLoggingLevel() {
		((Logger) LoggerFactory.getLogger("DeveloperLogger")).setLevel(Level.valueOf(loggingLevel));
		((Logger) LoggerFactory.getLogger("ConversationLogger")).setLevel(Level.valueOf(loggingLevel));
	}

	private static void changeLoggingLevelMenu() {
		while (true) {
			System.out.println(MessageFormat.format(loggingMenuString, loggingLevel));
			String userInput = getNextUserInput();
			switch (userInput) {
			case "1":
				loggingLevel = "Debug";
				return;
			case "2":
				loggingLevel = "Info";
				return;
			case "3":
				loggingLevel = "Warn";
				return;
			case "4":
				loggingLevel = "Error";
				return;
			case "5":
				return;
			}

		}
	}

	private static void languageMenu() {
		while (true) {
			System.out.println(MessageFormat.format(languageMenuString, selectedLanguage));
			String userInput = getNextUserInput();
			switch (userInput) {
			case "1":
				selectedLanguage = "German";
				return;
			case "2":
				selectedLanguage = "English";
				return;
			case "3":
				return;
			}

		}
	}

	private static void playgroundGerman() {
		ConversationEngine CE = setUp();
		System.out.println("\u001B[36mGeben Sie quit ein, um den Playground zu verlassen.\u001B[0m");
		System.out.println("\u001B[36mSie können jetzt Ihre Anfragen stellen.\u001B[0m");
		while (true) {
			String userInput = getNextUserInput();
			if ("quit".equalsIgnoreCase(userInput)) {
				break;
			}
			List<String> answers = CE.userInput(userInput);
			System.out.println("\u001B[32m" + String.join("\n", answers) + "\u001B[0m");
		}
	}

	private static void playgroundEnglish() {
		ConversationEngine CE = setUp();
		System.out.println("\u001B[36mEnter quit to leave the playground.\u001B[0m");
		System.out.println("\u001B[36mYou can now enter your request.\u001B[0m");
		while (true) {
			String userInput = getNextUserInput();
			if ("quit".equalsIgnoreCase(userInput)) {
				break;
			}
			List<String> answers = CE.userInput(userInput);
			System.out.println("\u001B[32m" + String.join("\n", answers) + "\u001B[0m");
		}
	}

	private static String getNextUserInput() {
		while (true) {
			String userInput = scanner.nextLine().strip();
			if (userInput.isBlank()) {
				continue;
			}
			return userInput;
		}
	}

	private static ConversationEngine setUp() {
		if ("German".equalsIgnoreCase(selectedLanguage)) {
			INLPComponent nlp = new NLPComponent();
			ConversationEngine germanConversationEngine = new ConversationEngine(nlp, new Locale("de", "DE"));
			GreetingSkill greeting = new GreetingSkill();
			String greetingSkillSM = loadJsonFileAsString("Greeting.json");
			germanConversationEngine.addSkill(greeting, greetingSkillSM);
			RecipeCookingSkill recipeCooking = new RecipeCookingSkill();
			String recipeCookingSkillSM = loadJsonFileAsString("RecipeCooking.json");
			germanConversationEngine.addSkill(recipeCooking, recipeCookingSkillSM);
			RecipeSearchSkill recipeSearch = new RecipeSearchSkill();
			String recipeSearchSkillSM = loadJsonFileAsString("RecipeSearch.json");
			germanConversationEngine.addSkill(recipeSearch, recipeSearchSkillSM);
			RecipeSelectSkill recipeSelect = new RecipeSelectSkill();
			String recipeSelectSkillSM = loadJsonFileAsString("RecipeSelect.json");
			germanConversationEngine.addSkill(recipeSelect, recipeSelectSkillSM);
			WeatherSkill weather = new WeatherSkill();
			String weatherSkillSM = loadJsonFileAsString("Weather.json");
			germanConversationEngine.addSkill(weather, weatherSkillSM);
			WeatherSkill weather2 = new WeatherSkill();
			String weatherSkillSM2 = loadJsonFileAsString("Weather2.json");
			germanConversationEngine.addSkill(weather2, weatherSkillSM2);
			return germanConversationEngine;
		}
		INLPComponent nlp = new NLPComponentEnglish();
		ConversationEngine englishConversationEngine = new ConversationEngine(nlp, new Locale("en", "US"));
		GreetingSkillEnglish greeting = new GreetingSkillEnglish();
		String greetingSkillSM = loadJsonFileAsString("Greeting.json");
		englishConversationEngine.addSkill(greeting, greetingSkillSM);
		RecipeCookingSkillEnglish recipeCooking = new RecipeCookingSkillEnglish();
		String recipeCookingSkillSM = loadJsonFileAsString("RecipeCooking.json");
		englishConversationEngine.addSkill(recipeCooking, recipeCookingSkillSM);
		RecipeSearchSkillEnglish recipeSearch = new RecipeSearchSkillEnglish();
		String recipeSearchSkillSM = loadJsonFileAsString("RecipeSearch.json");
		englishConversationEngine.addSkill(recipeSearch, recipeSearchSkillSM);
		RecipeSelectSkillEnglish recipeSelect = new RecipeSelectSkillEnglish();
		String recipeSelectSkillSM = loadJsonFileAsString("RecipeSelect.json");
		englishConversationEngine.addSkill(recipeSelect, recipeSelectSkillSM);
		WeatherSkillEnglish weather = new WeatherSkillEnglish();
		String weatherSkillSM = loadJsonFileAsString("Weather.json");
		englishConversationEngine.addSkill(weather, weatherSkillSM);
		WeatherSkillEnglish weather2 = new WeatherSkillEnglish();
		String weatherSkillSM2 = loadJsonFileAsString("Weather2.json");
		englishConversationEngine.addSkill(weather2, weatherSkillSM2);
		return englishConversationEngine;

	}

	static String loadJsonFileAsString(String fileName) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		JSONObject json = new JSONObject(new JSONTokener(inputStream));
		return json.toString();
	}

}
