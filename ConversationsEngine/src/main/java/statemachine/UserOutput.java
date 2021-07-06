package statemachine;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle the user output messages
 * 
 * @author Marcel Engelmann
 *
 */
class UserOutput {

	private UserOutput() throws IllegalStateException {
		throw new IllegalStateException("Utility class");
	}

	private static List<String> nextOutput = new ArrayList<>();

	/**
	 * Adds the default error message declared in the localization file to the user
	 * output
	 */
	static void addDefaultErrorMessage() {
		addOutputMessageFromLocalizationKey("CouldNotProcessRequest");
	}

	/**
	 * Adds a message to the user output from the localization bundle
	 * 
	 * @param localizationKey the localization key for the message
	 */
	static void addOutputMessageFromLocalizationKey(String localizationKey) {
		String message = I18n.getMessage(localizationKey);
		nextOutput.add(message);
	}

	/**
	 * Adds a message to the user output from the localization bundle with the given
	 * arguments
	 * 
	 * @param localizationKey the localization key for the message
	 */
	static void addOutputMessageFromLocalizationKey(String localizationKey, Object... args) {
		String message = I18n.getMessage(localizationKey, args);
		nextOutput.add(message);
	}

	/**
	 * Adds a question to the user output
	 * 
	 * @param question the question to be asked of the user
	 */
	static void addOutputQuestion(String question) {
		nextOutput.add(question);
	}

	/**
	 * Adds a list of messages to the user output
	 * 
	 * @param messages
	 */
	static void addOutputMessages(List<String> messages) {
		nextOutput.addAll(messages);
	}

	/**
	 * Removes all messages of the next output and returns them
	 * 
	 * @return all messages for the next user output
	 */
	static List<String> popNextOutput() {
		if (nextOutput.isEmpty()) {
			nextOutput.add(I18n.getMessage("WhatToDoNext"));
		}
		Logging.conversationMessages(nextOutput);
		final List<String> returnedList = nextOutput;
		nextOutput = new ArrayList<>();
		return returnedList;
	}
}
