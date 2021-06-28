import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import data.NLPComponent;
import interfaces.INLPComponent;
import skills.GreetingSkill;
import statemachine.ConversationsEngine;

@Execution(ExecutionMode.CONCURRENT)
class TimeoutTest {

	private final CountDownLatch waiter = new CountDownLatch(1);
	private ConversationsEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationsEngine(nlp, 1);
		GreetingSkill greet = new GreetingSkill();
		String greetingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greet, greetingSkillStateMachine);
	}

	@Test
	@DisplayName("TimeoutState correctly reached")
	void correctTimeoutState() throws InterruptedException {
		assertEquals("defaultState", myStateMachine.getState());
		waiter.await(1100, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", myStateMachine.getState());
	}

	@Test
	@DisplayName("TimeoutState correctly left")
	void backToDefaultState() throws InterruptedException {
		assertEquals("defaultState", myStateMachine.getState());
		waiter.await(1100, TimeUnit.MILLISECONDS);
		assertEquals("sleepState", myStateMachine.getState());
		List<String> answer = myStateMachine.userInput("Rezept");
		assertEquals("defaultState", myStateMachine.getState());
		assertEquals("Willkommen zurück!", answer.get(0));
		assertEquals("Es tut mir leid, aber ich konnte Ihre Anfrage leider nicht bearbeiten.", answer.get(1));
	}
}
