
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.NLPComponent;
import interfaces.INLPComponent;
import skills.GreetingSkill;
import skills.RecipeSearchSkill;
import skills.WeatherSkill;
import statemachine.ConversationsEngineStateMachine;

class MultipleSkillsTest {

	private ConversationsEngineStateMachine myStateMachine;
	private INLPComponent nlp = new NLPComponent();

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationsEngineStateMachine(nlp);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		myStateMachine.addSkill(weather, weatherSkillStateMachine);
		RecipeSearchSkill recipe = new RecipeSearchSkill();
		String recipeSkillSM = TestHelperFunctions.loadJsonFileAsString("RecipeSearch.json");
		this.myStateMachine.addSkill(recipe, recipeSkillSM);
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
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer.get(0));
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

	private List<String> sendUserInput(String message) {
		return this.myStateMachine.userInput(message);
	}
}
