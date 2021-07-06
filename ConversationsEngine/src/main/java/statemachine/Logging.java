package statemachine;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple logging utility class
 * 
 * @author Marcel Engelmann
 *
 */
class Logging {
	private static final Logger conversationLogger = LoggerFactory.getLogger("ConversationLogger");
	private static final Logger debugLogger = LoggerFactory.getLogger("DeveloperLogger");

	private Logging() throws IllegalStateException {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Log the user input with the ConversationLogger
	 * 
	 * @param message the message to be logged
	 */
	static void userInput(String message) {
		conversationLogger.info("Input:  {}", message);
	}

	/**
	 * Log the bot's output messages with the ConversationLogger. Each message will
	 * be logged separately
	 * 
	 * @param messages the messages to be logged
	 */
	static void conversationMessages(List<String> messages) {
		for (String message : messages) {
			conversationLogger.info("Output: {}", message);
		}
	}

	/**
	 * Log a message on DEBUG level with the DeveloperLogger
	 * 
	 * @param message the message to be logged
	 */
	static void debug(String message) {
		debugLogger.debug(message);
	}

	/**
	 * Log a message with arguments on DEBUG level with the DeveloperLogger
	 * 
	 * @param message the message to be logged
	 * @param args    the arguments for the message
	 */
	static void debug(String message, Object... args) {
		debugLogger.debug(message, args);
	}

	/**
	 * Log a message on WARN level with the DeveloperLogger
	 * 
	 * @param message the message to be logged
	 */
	static void warn(String message) {
		debugLogger.warn(message);
	}

	/**
	 * Log a message on ERROR level with the DeveloperLogger
	 * 
	 * @param message the message to be logged
	 */
	static void error(String message) {
		debugLogger.error(message);
	}

	/**
	 * Log a message with arguments on ERROR level with the DeveloperLogger
	 * 
	 * @param message the message to be logged
	 * @param args    the arguments for the message
	 */
	static void error(String message, Object... args) {
		debugLogger.error(message, args);
	}
}
