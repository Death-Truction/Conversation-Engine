package de.dai_labor.conversation_engine_core.conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.NLPComponent;
import de.dai_labor.conversation_engine_core.skills.GreetingSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeCookingSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeSearchSkill;
import de.dai_labor.conversation_engine_core.skills.RecipeSelectSkill;
import de.dai_labor.conversation_engine_core.skills.WeatherSkill;

class MultipleSkillsTest {

	private ConversationEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();
	private Locale defaultLanguage = new Locale("de", "DE");

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationEngine(nlp, defaultLanguage);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		myStateMachine.addSkill(weather, weatherSkillStateMachine);
		RecipeSearchSkill recipe = new RecipeSearchSkill();
		String recipeSkillSM = TestHelperFunctions.loadJsonFileAsString("RecipeSearch.json");
		this.myStateMachine.addSkill(recipe, recipeSkillSM);
		RecipeSelectSkill recipeSelect = new RecipeSelectSkill();
		String recipeSelectSM = TestHelperFunctions.loadJsonFileAsString("RecipeSelect.json");
		this.myStateMachine.addSkill(recipeSelect, recipeSelectSM);
		RecipeCookingSkill recipeCooking = new RecipeCookingSkill();
		String recipeCookingSM = TestHelperFunctions.loadJsonFileAsString("RecipeCooking.json");
		this.myStateMachine.addSkill(recipeCooking, recipeCookingSM);
		GreetingSkill greeting = new GreetingSkill();
		String greetingSkillSM = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greeting, greetingSkillSM);
	}

	@Test
	@DisplayName("two intents at once")
	void twoIntents() {

		List<String> answer = sendUserInput("Wie ist das Wetter in Berlin, welche Rezepte gibt es mit Paprika?");
		assertTrue(answer.get(0).contains("In Berlin sind es ")
				&& answer.get(1).contains("Rezepte mit Paprika gefunden:"));

	}

	@Test
	@DisplayName("change Intent in the middle of a skill")
	void changeIntentMiddleOfSkill() {
		String answer = sendUserInput("Wie ist das Wetter?").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = this.myStateMachine.userInput("Rezepte mit Paprika").get(0);
		assertTrue(answer.contains("Paprika") && answer.contains("Rezepte"));

	}

	@Test
	@DisplayName("Greeting and weather skill")
	void greetingAndWeatherSkill() {
		List<String> answer = sendUserInput("Hallo, wie ist das Wetter in Berlin?");
		assertTrue(answer.get(0).contains(TestHelperFunctions.getDayTime()));
		assertTrue(answer.get(1).contains("In Berlin sind es"));
	}

	@Test
	@DisplayName("Abort a skill")
	void abortASkill() {
		String answer = sendUserInput("Wie ist das Wetter").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Abbruch").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
		answer = sendUserInput("Berlin").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	@Test
	@DisplayName("Abort a skill, but continue the last used skill")
	void abortASkillContinueLastUsedSkill() {
		List<String> answer = sendUserInput("Wie ist das Wetter");
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer.get(0));
		answer = sendUserInput("Welche Rezepte");
		assertEquals("Welche Zutaten soll das Rezept beinhalten?", answer.get(0));
		answer = sendUserInput("Abbruch");
		assertEquals("Möchten Sie den letzten Skill oder alle Skills abbrechen?", answer.get(0));
		answer = sendUserInput("Letzten");
		assertEquals("Sie sind wieder im Skill WeatherSkill", answer.get(0));
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer.get(1));
	}

	@Test
	@DisplayName("Abort a skill, but ignore the returned question")
	void abortASkillIgnoreQuestion() {
		List<String> answer = sendUserInput("Wie ist das Wetter");
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer.get(0));
		answer = sendUserInput("Welche Rezepte");
		assertEquals("Welche Zutaten soll das Rezept beinhalten?", answer.get(0));
		answer = sendUserInput("Abbruch");
		assertEquals("Möchten Sie den letzten Skill oder alle Skills abbrechen?", answer.get(0));
		answer = sendUserInput("Wie ist das Wetter");
		assertEquals("Möchten Sie den letzten Skill oder alle Skills abbrechen?", answer.get(0));
	}

	@Test
	@DisplayName("abort a skill and the last used skill")
	void abortASkillAndLastUsedSkill() {
		String answer = sendUserInput("Wie ist das Wetter").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Welche Rezepte").get(0);
		assertEquals("Welche Zutaten soll das Rezept beinhalten?", answer);
		answer = sendUserInput("Abbruch").get(0);
		assertEquals("Möchten Sie den letzten Skill oder alle Skills abbrechen?", answer);
		answer = sendUserInput("Alle").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
		answer = sendUserInput("Berlin").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
		answer = sendUserInput("Paprika").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	@Test
	@DisplayName("Continue last skill")
	void continueLastSkill() {
		List<String> answers = sendUserInput("Wie ist das Wetter");
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answers.get(0));
		answers = sendUserInput("Hi");
		assertEquals(TestHelperFunctions.getDayTime(), answers.get(0));
		answers = sendUserInput("Ja");
		assertEquals("Sie sind wieder im Skill WeatherSkill", answers.get(0));
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answers.get(1));
	}

	@Test
	@DisplayName("Don't continue last skill")
	void dontContinueLastSkill() {
		String answer = sendUserInput("Wie ist das Wetter").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Hi").get(0);
		assertEquals(TestHelperFunctions.getDayTime(), answer);
		answer = sendUserInput("Nein").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	@Test
	@DisplayName("Ignore the continue last skill question")
	void ignoreContinueLastSkill() {
		String answer = sendUserInput("Wie ist das Wetter").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Hi").get(0);
		assertEquals(TestHelperFunctions.getDayTime(), answer);
		answer = sendUserInput("Hi").get(0);
		assertEquals("Wollen Sie mit dem Skill WeatherSkill fortfahren?", answer);
	}

	@Test
	@DisplayName("Abort cooking request")
	void abortCookingRequest() {
		List<String> answer = sendUserInput("Koche das Rezept Paprika-Kartoffelsuppe");
		assertEquals("Das Rezept \"paprika-kartoffelsuppe\" wurde erfolgreich ausgewählt.", answer.get(0));
		assertEquals("Haben Sie die Zutat Kartoffeln zu Hause?", answer.get(1));
		answer = sendUserInput("abbruch");
		assertEquals("Was möchten Sie als nächstes machen?", answer.get(0));
	}

	@Test
	@DisplayName("Invalid input")
	void invalidInput() {
		List<String> answers = sendUserInput("asasas");
		assertEquals(5, answers.size());

	}

	private List<String> sendUserInput(String message) {
		return this.myStateMachine.userInput(message);
	}
}
