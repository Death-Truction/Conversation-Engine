package conversations_engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestInstance(Lifecycle.PER_CLASS)
class SkillStateMachineSchemaTest {
	private Schema schema;
	private String resourceFolder;

	@BeforeAll
	void setUp() {
		JSONObject jsonSchema = TestHelperFunctions.loadJsonFileAsObject("SkillStateMachine_Schema.json");
		schema = SchemaLoader.load(jsonSchema);
		this.resourceFolder = "json_schema_test";
	}

	@Test
	@DisplayName("Add Valid SkillStateMachine")
	void valid() {
		JSONObject jsonInputString = loadJsonObject("Weather.json");
		assertDoesNotThrow(() -> schema.validate(jsonInputString));
	}

	@ParameterizedTest
	@DisplayName("Invalid Schemas")
	@CsvSource({ "MissingAttributes.json, 7, #: required key", "WrongType.json, 7, : expected type: ",
			"InvalidMinItems.json, 2, : expected minimum item count: ",
			"MissingStateAndTransitionAttributes.json, 4, required key",
			"NonUniqueEntries.json, 4, items are not unique"

	})
	void invalidSchemas(String fileName, int expectedValidations, String errorMessage) {
		JSONObject jsonInputString = loadJsonObject(fileName);
		ValidationException vEx = assertThrows(ValidationException.class, () -> schema.validate(jsonInputString));
		int validations = checkValidations(vEx, errorMessage);
		assertEquals(expectedValidations, validations);
	}

	private int checkValidations(ValidationException vEx, String exceptionMessageContains) {
		int validations = 0;
		if (!vEx.getCausingExceptions().isEmpty()) {
			for (ValidationException ex : vEx.getCausingExceptions()) {
				validations += checkValidations(ex, exceptionMessageContains);
			}
			return validations;
		}
		System.out.println(vEx.getMessage());
		assertTrue(vEx.getMessage().contains(exceptionMessageContains));
		validations++;
		return validations;
	}

	private JSONObject loadJsonObject(String fileName) {
		return TestHelperFunctions.loadJsonFileAsObject(fileName, resourceFolder);
	}

}
