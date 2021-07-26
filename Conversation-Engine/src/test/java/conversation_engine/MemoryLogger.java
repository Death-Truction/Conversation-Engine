package conversation_engine;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * A simple logger, that stores all logging messages in a {@link List}
 * 
 * @author Marcel Engelmann
 *
 */
class MemoryLogger extends ListAppender<ILoggingEvent> {

	void reset() {
		this.list.clear();
	}

	boolean contains(String logMessage, Level level) {
		for (ILoggingEvent event : this.list) {
			if (event.getFormattedMessage().toString().contains(logMessage) && event.getLevel().equals(level)) {
				return true;
			}
		}
		return false;
	}

	boolean contains(Level level) {
		for (ILoggingEvent event : this.list) {
			if (event.getLevel().equals(level)) {
				return true;
			}
		}
		return false;
	}
}
