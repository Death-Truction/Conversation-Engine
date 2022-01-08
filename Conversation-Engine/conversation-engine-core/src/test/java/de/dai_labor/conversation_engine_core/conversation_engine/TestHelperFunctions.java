package de.dai_labor.conversation_engine_core.conversation_engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Calendar;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * Utility class that provides multiple function for the tests
 * 
 * @author Marcel Engelmann
 *
 */
class TestHelperFunctions {

	private TestHelperFunctions() {
		throw new IllegalStateException("Static class");
	}

	/**
	 * Loads a JSON-File from the resources root
	 * 
	 * @param fileName the name of the JSON-File
	 * @return a {@link String} with the file content
	 */
	static String loadJsonFileAsString(String fileName) {
		return getJsonObjectFromPath(fileName).toString();
	}

	/**
	 * Loads a JSON-File from the given folder within the resources
	 * 
	 * @param fileName the name of the JSON-File
	 * @return a {@link String} with the file content
	 */
	static String loadJsonFileAsString(String fileName, String folderName) {
		String path = Paths.get(folderName).resolve(fileName).toString();
		return getJsonObjectFromPath(path).toString();
	}

	/**
	 * Loads a JSON-File from the resources root
	 * 
	 * @param fileName the name of the JSON-File
	 * @return a {@link JSONObject} with the file content
	 * @throws IOException
	 */
	static JSONObject loadJsonFileAsObject(String fileName) {
		return getJsonObjectFromPath(fileName);
	}

	/**
	 * Loads a JSON-File from the given folder within the resources
	 * 
	 * @param fileName the name of the JSON-File
	 * @return a {@link String} with the file content
	 */
	static JSONObject loadJsonFileAsObject(String fileName, String folderName) {
		String path = Paths.get(folderName).resolve(fileName).toString();
		return getJsonObjectFromPath(path);
	}

	/**
	 * Returns a message correlating to the current time
	 * 
	 * @return a message correlating to the current time
	 */
	static String getDayTime() {
		int currentHour = Calendar.HOUR_OF_DAY;
		if (currentHour <= 10) {
			return "Guten Morgen!";
		} else if (currentHour <= 16) {
			return "Guten Tag!";
		} else if (currentHour <= 19) {
			return "Guten Abend!";
		} else {
			return "Hallo!";
		}
	}

	/**
	 * Returns a new {@link MemoryLogger} for the debug messages of the
	 * "DeveloperLogger"
	 * 
	 * @return a new {@link MemoryLogger} for the debug messages of the
	 *         "DeveloperLogger"
	 */
	static MemoryLogger getNewLogAppender() {
		Logger logger = (Logger) LoggerFactory.getLogger("DeveloperLogger");
		MemoryLogger logs = new MemoryLogger();
		logs.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		logger.setLevel(Level.DEBUG);
		logger.addAppender(logs);
		logs.start();
		return logs;
	}

	/**
	 * Retrieves a {@link JSONObject} from a given path within the resources
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private static JSONObject getJsonObjectFromPath(String path) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		JSONObject newJsonObject = new JSONObject(new JSONTokener(inputStream));
		try {
			inputStream.close();
		} catch (IOException e) {
			return null;
		}
		return newJsonObject;
	}
}
