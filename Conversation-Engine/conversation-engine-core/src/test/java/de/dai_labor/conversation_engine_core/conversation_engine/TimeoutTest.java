package de.dai_labor.conversation_engine_core.conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.NLPComponent;
import de.dai_labor.conversation_engine_core.skills.GreetingSkill;

class TimeoutTest {

	private final CountDownLatch waiter = new CountDownLatch(1);
	private INLPComponent nlp = new NLPComponent();
	private Locale defaultLanguage = new Locale("de", "DE");

	@Test
	@DisplayName("TimeoutState correctly reached")
	void correctTimeoutState() throws InterruptedException {
		ConversationEngine ConversationEngine = this.createNewConversationEngine(1);
		assertEquals("defaultState", ConversationEngine.getState());
		this.waiter.await(1200, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", ConversationEngine.getState());
	}

	@Test
	@DisplayName("TimeoutState correctly left")
	void backToDefaultState() throws InterruptedException {
		ConversationEngine ConversationEngine = this.createNewConversationEngine(1);
		assertEquals("defaultState", ConversationEngine.getState());
		this.waiter.await(1200, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", ConversationEngine.getState());
		List<String> answer = ConversationEngine.userInput("Rezept");
		assertEquals("defaultState", ConversationEngine.getState());
		assertEquals("Willkommen zurück!", answer.get(0));
	}

	@Test
	@DisplayName("No timeout")
	void noTimeout() throws InterruptedException {
		ConversationEngine ConversationEngine = this.createNewConversationEngine(0);
		assertEquals("defaultState", ConversationEngine.getState());
		this.waiter.await(1200, TimeUnit.MILLISECONDS);
		assertEquals("defaultState", ConversationEngine.getState());
	}

	private ConversationEngine createNewConversationEngine(int timeout) {
		ConversationEngine ConversationEngine = new ConversationEngine(this.nlp, timeout, this.defaultLanguage);
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		ConversationEngine.addSkill(greet, greetingSkillStateMachine);
		return ConversationEngine;
	}
}
