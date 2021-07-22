package conversations_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import interfaces.INLPComponent;
import interfaces_implementation.NLPComponent;
import skills.GreetingSkill;

class TimeoutTest {

	private final CountDownLatch waiter = new CountDownLatch(1);
	private INLPComponent nlp = new NLPComponent();
	private Locale defaultLanguage = new Locale("de", "DE");

	@Test
	@DisplayName("TimeoutState correctly reached")
	void correctTimeoutState() throws InterruptedException {
		ConversationsEngine conversationsEngine = createNewConversationsEngine();
		assertEquals("defaultState", conversationsEngine.getState());
		waiter.await(1200, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", conversationsEngine.getState());
	}

	@Test
	@DisplayName("TimeoutState correctly left")
	void backToDefaultState() throws InterruptedException {
		ConversationsEngine conversationsEngine = createNewConversationsEngine();
		assertEquals("defaultState", conversationsEngine.getState());
		waiter.await(1200, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", conversationsEngine.getState());
		List<String> answer = conversationsEngine.userInput("Rezept");
		assertEquals("defaultState", conversationsEngine.getState());
		assertEquals("Willkommen zurück!", answer.get(0));
	}

	private ConversationsEngine createNewConversationsEngine() {
		ConversationsEngine conversationsEngine = new ConversationsEngine(nlp, 1, defaultLanguage);
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		conversationsEngine.addSkill(greet, greetingSkillStateMachine);
		return conversationsEngine;
	}
}
