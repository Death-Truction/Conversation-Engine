package conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import interfaces.INLPComponent;
import interfaces_implementation.NLPComponent;
import interfaces_implementation.NLPComponentEnglish;
import interfaces_implementation.NLPComponentUndefinedLanguage;
import interfaces_implementation.NLPComponentWithInvalidReturnedData;
import skills.GreetingSkill;
import skills.WeatherSkill;

class ConversationEngineTest {

	private ConversationEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();
	private String contextData;
	private MemoryLogger logs;
	private Locale defaultLanguage = new Locale("de", "DE");

	@BeforeEach
	void init() {
		this.contextData = "";
		createNewConversationEngine(this.nlp, "{}");
		logs = TestHelperFunctions.getNewLogAppender();
	}

	@Test
	@DisplayName("Get correct contextData after shutdown")
	void getCorrectContextDataOnShutdown() {
		Consumer<StringBuilder> shutdownConsumer = data -> {
			this.contextData = data.toString();
		};
		this.myStateMachine.userInput("Ich habe Kartoffeln");
		this.myStateMachine.shutdown(shutdownConsumer);
		assertTrue(contextData.contains("{\"availableIngredients\":{\"Kartoffeln\":{\"amount\":\""));
	}

	@Test
	@DisplayName("Get contextData and start new ConversationEngine with the same data")
	void restartConversationEngineWithOldContextObject() {
		Consumer<StringBuilder> shutdownConsumer = data -> {
			this.contextData = data.toString();
		};
		List<String> answers = this.myStateMachine.userInput("Wie ist das Wetter in Berlin?");
		System.out.println("---------------" + answers);
		assertTrue(answers.get(0).contains("In Berlin sind es"));
		this.myStateMachine.shutdown(shutdownConsumer);
		assertEquals("{\"weatherLocations\":[{\"Berlin\":{\"country\":\"Germany\"}}]}", this.contextData);
		createNewConversationEngine(this.nlp, this.contextData);
		answers = this.myStateMachine.userInput("Wie ist das Wetter?");
		assertTrue(answers.get(0).contains("In Berlin sind es"));
	}

	@Test
	@DisplayName("English NLPComponent")
	void englishNLPComponent() {
		INLPComponent newNlp = new NLPComponentEnglish();
		createNewConversationEngine(newNlp, "{}");
		String answer = this.myStateMachine.userInput("ggf").get(0);
		assertEquals("I'm sorry, but unfortunately I was unable to process your request.", answer);
	}

	// Tests for code coverage

	@Test
	@DisplayName("Undefined user language")
	void undefinedUserLanguage() {
		INLPComponent newNlp = new NLPComponentUndefinedLanguage();
		createNewConversationEngine(newNlp, "{}");
		List<String> answers = this.myStateMachine.userInput("Hi");
		assertEquals(TestHelperFunctions.getDayTime(), answers.get(0));
		logs.contains("The language el is not supported", Level.ERROR);
	}

	@Test
	@DisplayName("NLPComponent parameter is null")
	void nlpComponentIsNull() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationEngine(null, defaultLanguage));
	}

	@Test
	@DisplayName("timeoutInSeconds parameter is negative")
	void timeoutIsNegative() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationEngine(this.nlp, -1, defaultLanguage));
	}

	@Test
	@DisplayName("defaultLanguage parameter is negative")
	void defaultLanguageIsNegative() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationEngine(this.nlp, 200, null));
	}

	@Test
	@DisplayName("defaultLanguage parameter is not supported")
	void defaultLanguageIsNotSupported() {
		new ConversationEngine(nlp, new Locale("el"));
		assertTrue(this.logs.contains(
				"Default language was not set! The language el could not be found. Please make sure that the correct localization file exists.",
				Level.WARN));
	}

	@Test
	@DisplayName("add the same skill twice")
	void addSameSkillTwice() {
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greet, greetingSkillStateMachine);
		assertTrue(logs.contains("The skill GreetingSkill already exists", Level.ERROR));
	}

	@Test
	@DisplayName("Passing empty consumer to shutdown method")
	void emptyConsumer() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
	}

	@Test
	@DisplayName("Accessing shutdown() after shutdown")
	void accessingShutdownAfterShutdown() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The ConversationEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing getState() after shutdown")
	void accessingGetStateAfterShutdown() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		this.myStateMachine.getState();
		assertTrue(this.logs.contains("The ConversationEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing addSkill() after shutdown")
	void accessingAddSkillAfterShutdown() {
		myStateMachine = new ConversationEngine(nlp, defaultLanguage);
		myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		myStateMachine.addSkill(greet, greetingSkillStateMachine);
		assertTrue(this.logs.contains("The ConversationEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing userInput() after shutdown")
	void accessingUserInputAfterShutdown() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		this.myStateMachine.userInput("hi");
		assertTrue(this.logs.contains("The ConversationEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("User input is null")
	void userInputIsNull() {
		this.myStateMachine.userInput(null);
		assertTrue(this.logs.contains("The user input was null or blank", Level.WARN));
	}

	@Test
	@DisplayName("User input is blank")
	void userInputIsBlank() {
		this.myStateMachine.userInput(" ");
		assertTrue(this.logs.contains("The user input was null or blank", Level.WARN));
	}

	@Test
	@DisplayName("Invalid NLPAnswers")
	void invalidNLPAnswers() {
		INLPComponent nlp = new NLPComponentWithInvalidReturnedData();
		ConversationEngine statemachine = new ConversationEngine(nlp, new Locale("de", "DE"));
		statemachine.userInput("nullLanguage");
		assertTrue(logs.contains("NLPComponent did not return a language", Level.ERROR));
		statemachine.userInput("null");
		assertTrue(logs.contains("NLP Component's returned INLPAnswer is null", Level.ERROR));
	}

	@Test
	@DisplayName("add null skill")
	void addNullSkill() {
		this.myStateMachine.addSkill(null, "");
		assertTrue(logs.contains("The skill to add to the ConversationEngine is null", Level.ERROR));
		this.myStateMachine = new ConversationEngine(nlp, defaultLanguage);
		GreetingSkill greet = new GreetingSkill();
		this.myStateMachine.addSkill(greet, "");
		assertTrue(
				logs.contains("The JSON-String for the skill to add to the ConversationEngine is blank", Level.ERROR));
	}

	private void createNewConversationEngine(INLPComponent nlp, String contextObject) {
		this.myStateMachine = new ConversationEngine(nlp, contextObject, defaultLanguage);
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greet, greetingSkillStateMachine);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(weather, weatherSkillStateMachine);
	}
}
