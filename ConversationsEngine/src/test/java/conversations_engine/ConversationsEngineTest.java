package conversations_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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

class ConversationsEngineTest {

	private ConversationsEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();
	private String contextData;
	private MemoryLogger logs;

	@BeforeEach
	void init() {
		this.contextData = "";
		createNewConversationsEngine(this.nlp, "{}");
		logs = TestHelperFunctions.getNewLogAppender();
	}

	@Test
	@DisplayName("Get correct contextData after shutdown")
	void getCorrectContextDataOnShutdown() {
		Consumer<StringBuffer> shutdownConsumer = data -> {
			this.contextData = data.toString();
		};
		this.myStateMachine.userInput("Ich habe Kartoffeln");
		this.myStateMachine.shutdown(shutdownConsumer);
		assertTrue(contextData.contains("{\"availableIngredients\":{\"Kartoffeln\":{\"amount\":\""));
	}

	@Test
	@DisplayName("Get contextData and start new ConversationsEngine with the same data")
	void restartConversationsEngineWithOldContextObject() {
		Consumer<StringBuffer> shutdownConsumer = data -> {
			this.contextData = data.toString();
		};
		List<String> answers = this.myStateMachine.userInput("Wie ist das Wetter in Berlin?");
		System.out.println("---------------" + answers);
		assertTrue(answers.get(0).contains("In Berlin sind es"));
		this.myStateMachine.shutdown(shutdownConsumer);
		assertEquals("{\"weatherLocations\":[{\"Berlin\":{\"country\":\"Germany\"}}]}", this.contextData);
		createNewConversationsEngine(this.nlp, this.contextData);
		answers = this.myStateMachine.userInput("Wie ist das Wetter?");
		assertTrue(answers.get(0).contains("In Berlin sind es"));
	}

	@Test
	@DisplayName("English NLPComponent")
	void englishNLPComponent() {
		INLPComponent newNlp = new NLPComponentEnglish();
		createNewConversationsEngine(newNlp, "{}");
		String answer = this.myStateMachine.userInput("ggf").get(0);
		assertEquals("I'm sorry, but unfortunately I was unable to process your request.", answer);
	}

	// Tests for code coverage

	@Test
	@DisplayName("Undefined user language")
	void undefinedUserLanguage() {
		INLPComponent newNlp = new NLPComponentUndefinedLanguage();
		createNewConversationsEngine(newNlp, "{}");
		List<String> answers = this.myStateMachine.userInput("Hi");
		assertEquals("Es tut mir leid, aber ich konnte Ihre Anfrage leider nicht bearbeiten.", answers.get(0));
		logs.contains("The language el is not supported", Level.ERROR);
	}

	@Test
	@DisplayName("NLPComponent parameter is null")
	void nlpComponentIsNull() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationsEngine(null));
	}

	@Test
	@DisplayName("timeoutInSeconds parameter is negative")
	void timeoutIsNegative() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationsEngine(this.nlp, -1));
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
		assertTrue(this.logs.contains("The ConversationsEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing getState() after shutdown")
	void accessingGetStateAfterShutdown() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		this.myStateMachine.getState();
		assertTrue(this.logs.contains("The ConversationsEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing addSkill() after shutdown")
	void accessingAddSkillAfterShutdown() {
		myStateMachine = new ConversationsEngine(nlp);
		myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		myStateMachine.addSkill(greet, greetingSkillStateMachine);
		assertTrue(this.logs.contains("The ConversationsEngine was invoked after it has been shut down", Level.ERROR));
	}

	@Test
	@DisplayName("Accessing userInput() after shutdown")
	void accessingUserInputAfterShutdown() {
		this.myStateMachine.shutdown(null);
		assertTrue(this.logs.contains("The consumer passed to the shutdown function was null", Level.WARN));
		this.myStateMachine.userInput("hi");
		assertTrue(this.logs.contains("The ConversationsEngine was invoked after it has been shut down", Level.ERROR));
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
		ConversationsEngine statemachine = new ConversationsEngine(nlp);
		statemachine.userInput("nullLanguage");
		assertTrue(logs.contains("NLPComponent did not return a language", Level.ERROR));
		statemachine.userInput("null");
		assertTrue(logs.contains("NLP Component's returned INLPAnswer is null", Level.ERROR));
	}

	@Test
	@DisplayName("add null skill")
	void addNullSkill() {
		this.myStateMachine.addSkill(null, "");
		assertTrue(logs.contains("The skill to add to the ConversationsEngine is null", Level.ERROR));
		this.myStateMachine = new ConversationsEngine(nlp);
		GreetingSkill greet = new GreetingSkill();
		this.myStateMachine.addSkill(greet, "");
		assertTrue(
				logs.contains("The JSON-String for the skill to add to the ConversationsEngine is blank", Level.ERROR));
	}

	private void createNewConversationsEngine(INLPComponent nlp, String contextObject) {
		this.myStateMachine = new ConversationsEngine(nlp, contextObject);
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greet, greetingSkillStateMachine);
		WeatherSkill weather = new WeatherSkill();
		String weatherSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		this.myStateMachine.addSkill(weather, weatherSkillStateMachine);
	}
}
