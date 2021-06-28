
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import data.NLPComponent;
import data.NLPComponentEnglish;
import data.NLPComponentUndefinedLanguage;
import interfaces.INLPComponent;
import skills.GreetingSkill;
import skills.WeatherSkill;
import statemachine.ConversationsEngine;

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
		assertEquals("I'm sorry, but unfortunately I was unable to process your request.", answers.get(0));
		logs.contains("The language el is not supported", Level.ERROR);
	}

	@Test
	@DisplayName("Null parameters")
	void nlpComponentIsNull() {
		assertThrows(IllegalArgumentException.class, () -> new ConversationsEngine(null));
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
