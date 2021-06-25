
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Calendar;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

class TestHelperFunctions {

	private TestHelperFunctions() {
		throw new IllegalStateException("Utility class");
	}

	static String loadJsonFileAsString(String fileName) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		JSONObject json = new JSONObject(new JSONTokener(inputStream));
		return json.toString();
	}

	static String loadJsonFileAsString(String fileName, String folderName) {
		String path = Paths.get(folderName).resolve(fileName).toString();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		JSONObject json = new JSONObject(new JSONTokener(inputStream));
		return json.toString();
	}

	static JSONObject loadJsonFileAsObject(String fileName) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		return new JSONObject(new JSONTokener(inputStream));
	}

	static JSONObject loadJsonFileAsObject(String fileName, String folderName) {
		String path = Paths.get(folderName).resolve(fileName).toString();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		return new JSONObject(new JSONTokener(inputStream));
	}

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

	static MemoryLogger getNewLogAppender() {
		Logger logger = (Logger) LoggerFactory.getLogger("DeveloperLogger");
		MemoryLogger logs = new MemoryLogger();
		logs.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		logger.setLevel(Level.DEBUG);
		logger.addAppender(logs);
		logs.start();
		return logs;
	}
}
