package conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ch.qos.logback.classic.Level;
import interfaces.INLPComponent;
import interfaces_implementation.NLPComponent;
import skills.WeatherSkill;
import skills.WeatherSkillWithEmptyResponseAndQuestions;
import skills.WeatherSkillWithNullTransitionTrigger;
import skills.WeatherSkillWithWrongTransitionTrigger;

@TestInstance(Lifecycle.PER_CLASS)
class WeatherSkillTest {

	private ConversationsEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();
	private MemoryLogger logs;
	private Locale defaultLanguage = new Locale("de", "DE");

	@BeforeAll
	void setUp() {
		this.logs = TestHelperFunctions.getNewLogAppender();
	}

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationsEngine(nlp, defaultLanguage);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		myStateMachine.addSkill(weather, weatherSkillStateMachine);
		logs.reset();
	}

	@Test
	@DisplayName("Weather request for 'München'")
	void weatherSkillSimpleCheck() {
		String answer = this.sendUserInput("Wie ist das Wetter in München?").get(0);
		assertTrue(answer.contains("München") && answer.contains("Grad"));
		assertEquals("defaultState", myStateMachine.getState());
	}

	@Test
	@DisplayName("Weather request for 'München' and 'Berlin'")
	void weatherSkillTwoCities() {
		List<String> answer = this.sendUserInput("Wie ist das Wetter in Berlin und in München?");
		assertTrue(answer.get(0).contains("Berlin") && answer.get(1).contains("München")
				&& answer.get(0).contains("Grad") && answer.get(1).contains("Grad"));
		assertEquals("defaultState", myStateMachine.getState());
	}

	@Test
	@DisplayName("Weather Missing Entities")
	void weatherSkillMissingEntities() {
		String answer = sendUserInput("Wie ist das Wetter?").get(0);
		assertEquals("Mid", myStateMachine.getState());
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
	}

	@Test
	@DisplayName("Weather with response to missing entities feedback")
	void weatherSkillMissingEntitiesWithResponse() {
		String answer = sendUserInput("Wie ist das Wetter?").get(0);

		assertEquals("Mid", myStateMachine.getState());
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Berlin").get(0);
		assertTrue(answer.contains("Berlin") && answer.contains("Grad"));
		assertEquals("defaultState", myStateMachine.getState());
	}

	@Test
	@DisplayName("Two possible Skills and choosing one")
	void chooseOneOfTwoSkills() {
		WeatherSkill w2 = new WeatherSkill();
		String weatherSkillSM2 = TestHelperFunctions.loadJsonFileAsString("Weather2.json");
		this.myStateMachine.addSkill(w2, weatherSkillSM2);
		String answer = sendUserInput("Wie ist das Wetter?").get(0);
		assertTrue(answer.contains("Für Ihre Anfrage stehen folgende Skills zur Verfügung:"));
		answer = sendUserInput("WeatherSkill2").get(0);
		assertEquals("Von welchem Ort möchten Sie das Wetter wissen?", answer);
		answer = sendUserInput("Berlin").get(0);
		assertTrue(answer.contains("Berlin") && answer.contains("Grad"));
		assertEquals("defaultState", myStateMachine.getState());
	}

	@Test
	@DisplayName("Two possible Skills but not choosing one")
	void doNotChooseOneOfTwoSkills() {
		WeatherSkill w2 = new WeatherSkill();
		String weatherSkillSM2 = TestHelperFunctions.loadJsonFileAsString("Weather2.json");
		this.myStateMachine.addSkill(w2, weatherSkillSM2);
		String answer = sendUserInput("Wie ist das Wetter?").get(0);
		assertTrue(answer.contains("Für Ihre Anfrage stehen folgende Skills zur Verfügung:"));
		answer = sendUserInput("Wie ist das Wetter?").get(0);
		assertTrue(answer.contains("Für Ihre Anfrage stehen folgende Skills zur Verfügung:"));

	}

	@Test
	@DisplayName("unreconizable input")
	void unreconizableInput() {
		String answer = sendUserInput("asasasa").get(0);
		assertTrue(answer.contains("Es tut mir leid, aber ich konnte Ihre Anfrage leider nicht bearbeiten."));
	}

	@Test
	@DisplayName("no skill for the request available")
	void noFittingSkillAvailable() {
		String answer = sendUserInput("Welche Rezepte gibt es mit Paprika?").get(0);
		assertEquals("Ich konnte keinen passenden Skill für Ihre Anfrage finden.", answer);

	}

	@Test
	@DisplayName("Get example requests on wrong input")
	void getExampleRequests() {
		List<String> answers = sendUserInput("Wie ist das Wetter?");
		answers = sendUserInput("sdsdasd");
		assertEquals("Es tut mir leid, aber ich konnte Ihre Anfrage leider nicht bearbeiten.", answers.get(0));
		assertEquals("Bitte gib einen Ort an, wie Berlin, Dortmund, Hamburg oder München", answers.get(1));

	}

	// tests for code coverage

	@Test
	@DisplayName("Adding location without weather request")
	void addingLocation() {
		String answer = sendUserInput("Ich bin in Berlin").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	@Test
	@DisplayName("return wrong trigger")
	void wrongTrigger() {
		WeatherSkillWithWrongTransitionTrigger wt = new WeatherSkillWithWrongTransitionTrigger();
		this.myStateMachine = new ConversationsEngine(this.nlp, defaultLanguage);
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(wt, weatherSkillStateMachine);
		String answer = sendUserInput("Wetter").get(0);
		assertTrue(logs.contains("Cannot find Transition STAY in WeatherSkill's State Start", Level.ERROR));
		assertEquals("Es tut mir leid, aber ich konnte Ihre Anfrage leider nicht bearbeiten.", answer);
	}

	@Test
	@DisplayName("return null as ISkillAnswer")
	void returnNull() {
		WeatherSkillWithWrongTransitionTrigger wt = new WeatherSkillWithWrongTransitionTrigger();
		this.myStateMachine = new ConversationsEngine(this.nlp, defaultLanguage);
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(wt, weatherSkillStateMachine);
		sendUserInput("Welche Rezepte");
		assertTrue(logs.contains("Returned answer of the skill WeatherSkill for the intent recipeSearch is null",
				Level.ERROR));
	}

	@Test
	@DisplayName("return null trigger in ISkillAnswer")
	void nullTrigger() {
		WeatherSkillWithNullTransitionTrigger wt = new WeatherSkillWithNullTransitionTrigger();
		this.myStateMachine = new ConversationsEngine(this.nlp, defaultLanguage);
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(wt, weatherSkillStateMachine);
		sendUserInput("Wetter");
		assertTrue(logs.contains(
				"The trigger in the answer of the skill WeatherSkill, for the intent weather, is null or empty",
				Level.ERROR));
	}

	@Test
	@DisplayName("return wrong json syntax")
	void wrongJsonSyntax() {
		WeatherSkillWithNullTransitionTrigger wt = new WeatherSkillWithNullTransitionTrigger();
		this.myStateMachine = new ConversationsEngine(this.nlp, defaultLanguage);
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(wt, weatherSkillStateMachine);
		String answer = sendUserInput("welche Rezepte").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	@Test
	@DisplayName("return empty answer and questions")
	void emptyAnswerAndQuestions() {
		WeatherSkillWithEmptyResponseAndQuestions wt = new WeatherSkillWithEmptyResponseAndQuestions();
		this.myStateMachine = new ConversationsEngine(this.nlp, defaultLanguage);
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(wt, weatherSkillStateMachine);
		String answer = sendUserInput("Wetter").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
		answer = sendUserInput("welche Rezepte").get(0);
		assertEquals("Was möchten Sie als nächstes machen?", answer);
	}

	private List<String> sendUserInput(String input) {
		return this.myStateMachine.userInput(input);
	}

}
