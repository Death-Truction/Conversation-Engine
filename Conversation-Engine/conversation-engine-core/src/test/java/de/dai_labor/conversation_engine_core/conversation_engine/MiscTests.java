package de.dai_labor.conversation_engine_core.conversation_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import de.dai_labor.conversation_engine_core.conversation_engine.GenerateSkillStateMachine;
import de.dai_labor.conversation_engine_core.conversation_engine.I18n;
import de.dai_labor.conversation_engine_core.conversation_engine.Logging;
import de.dai_labor.conversation_engine_core.conversation_engine.UserOutput;

@DisplayName("Multiple Tests for code coverage")
class MiscTests {

	@Test
	@DisplayName("UserOuput private constructors")
	void userOuputPrivateConstructor()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<UserOutput> constructor = UserOutput.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}

	@Test
	@DisplayName("I18n private constructors")
	void i18nPrivateConstructor()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<I18n> constructor = I18n.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}

	@Test
	@DisplayName("GenerateSkillStateMachine private constructors")
	void generateSkillStateMachinePrivateConstructor()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<GenerateSkillStateMachine> constructor = GenerateSkillStateMachine.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}

	@Test
	@DisplayName("GenerateSkillStateMachine JSONException")
	void generateSkillStateMachineJsonException() {
		MemoryLogger logs = TestHelperFunctions.getNewLogAppender();
		assertEquals(null, GenerateSkillStateMachine.fromJson(null, "", null));
		assertTrue(logs.contains("Invalid JSON-String: A JSONObject text must begin with '{' at 0 [character 1 line 1]",
				Level.ERROR));
	}

	@Test
	@DisplayName("Logging private constructors")
	void LoggingPrivateConstructor()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<Logging> constructor = Logging.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(InvocationTargetException.class, constructor::newInstance);
	}
}
