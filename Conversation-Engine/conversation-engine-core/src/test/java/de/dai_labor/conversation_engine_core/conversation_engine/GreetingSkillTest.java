package de.dai_labor.conversation_engine_core.conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.NLPComponent;
import de.dai_labor.conversation_engine_core.skills.GreetingSkill;

class GreetingSkillTest {

	private ConversationEngine myStateMachine;
	NLPComponent nlp = new NLPComponent();
	private Locale defaultLanguage = new Locale("de", "DE");

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationEngine(nlp, defaultLanguage);
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
