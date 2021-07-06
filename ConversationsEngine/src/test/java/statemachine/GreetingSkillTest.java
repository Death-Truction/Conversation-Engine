package statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import data.NLPComponent;
import skills.GreetingSkill;

class GreetingSkillTest {

	private ConversationsEngine myStateMachine;
	NLPComponent nlp = new NLPComponent();

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationsEngine(nlp);
		GreetingSkill greeting = new GreetingSkill();
		String greetingSkillSM = TestHelperFunctions.loadJsonFileAsString("Greeting.json");
		this.myStateMachine.addSkill(greeting, greetingSkillSM);
	}

	@ParameterizedTest
	@DisplayName("Simple greeting")
	@ValueSource(strings = { "Hi", "Hallo", "Guten Tag" })
	void simpleGreeting(String greeting) {
		List<String> answer = this.myStateMachine.userInput(greeting);
		String dayTime = TestHelperFunctions.getDayTime();
		assertEquals(answer.get(0), dayTime);

	}
}
