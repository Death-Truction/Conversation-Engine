package conversation_engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ch.qos.logback.classic.Level;
import interfaces_implementation.NLPComponent;
import skills.WeatherSkill;

@TestInstance(Lifecycle.PER_CLASS)
class GenerateSkillStateMachineTest {
	private ConversationEngine myStateMachine;
	private String resourceFolder;
	private WeatherSkill weather;
	private MemoryLogger logs;
	private NLPComponent nlp = new NLPComponent();
	private Locale defaultLanguage = new Locale("de", "DE");

	@BeforeAll
	void setUp() {
		weather = new WeatherSkill();
		this.resourceFolder = "generate_skill_state_machine";
		this.logs = TestHelperFunctions.getNewLogAppender();
	}

	@BeforeEach
	void init() {
		this.logs.reset();
		this.myStateMachine = new ConversationEngine(nlp, defaultLanguage);
	}

	@Test
	@DisplayName("Correct SkillStateMachine")
	void correctSkillStateMachine() {
		String jsonInput = TestHelperFunctions.loadJsonFileAsString("Weather.json");
		myStateMachine.addSkill(weather, jsonInput);
		assertFalse(logs.contains(Level.ERROR));
	}

	@Test
	@DisplayName("Failed schema validation")
	void failedSchemaValidation() {
		String jsonInput = loadJsonObject("SchemaValidation.json");
		myStateMachine.addSkill(weather, jsonInput);
		assertTrue(logs.contains(
				"Invalid SkillStateMachine JSON format: \n" + "#: required key [states] not found\n"
						+ "#: required key [transitions] not found\n" + "#: required key [startAt] not found\n"
						+ "#: required key [endAt] not found\n" + "#: required key [name] not found\n"
						+ "#: required key [usedEntities] not found\n" + "#: required key [usedIntents] not found",
				Level.ERROR));
		assertTrue(logs.contains("Could not add the skill from the jsonString {}", Level.ERROR));
	}

	@ParameterizedTest
	@CsvSource({ "WrongStateNameTransitionSource.json,Cannot find the source state,Could not add the skill",
			"WrongStateNameTransitionTarget.json,Cannot find the target state,Could not add the skill",
			"EmptyStartAndEndState.json,Could not find the defined startAt state  in the list of all defined states,Could not find the defined endAt state  in the list of all defined states" })
	@DisplayName("Wrong or emoty state names")
	void t(String file, String error1, String error2) {
		String jsonInput = loadJsonObject(file);
		myStateMachine.addSkill(weather, jsonInput);
		assertTrue(logs.contains(error1, Level.ERROR));
		assertTrue(logs.contains(error2, Level.ERROR));
	}

	@Test
	@DisplayName("Empty skill name")
	void emptySkillName() {
		String jsonInput = loadJsonObject("EmptySkillName.json");
		myStateMachine.addSkill(weather, jsonInput);
		assertTrue(logs.contains("The name of the skill is empty", Level.ERROR));
	}

	// Tests for code coverage

	@Test
	@DisplayName("Including a 'FAILED'-Trigger")
	void includingFailedTrigger() {
		String jsonInput = loadJsonObject("FailedTrigger.json");
		myStateMachine.addSkill(weather, jsonInput);
		assertFalse(logs.contains(Level.ERROR));
	}

	private String loadJsonObject(String fileName) {
		return TestHelperFunctions.loadJsonFileAsString(fileName, resourceFolder);
	}

}
