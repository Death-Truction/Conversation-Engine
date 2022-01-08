package de.dai_labor.dialog_modeling_tool.models;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * A simple logger, that stores all logging messages in a {@link List}
 *
 * @author Marcel Engelmann
 *
 */
public class MemoryLogger extends ListAppender<ILoggingEvent> {

	/**
	 * Resets the logging list
	 */
	public void reset() {
		this.list.clear();
	}

	/**
	 * Gets a copy of the current logging list
	 *
	 * @return a copy of the current logging list
	 */
	public List<ILoggingEvent> getMessagesCopy() {
		return new ArrayList<>(this.list);
	}

	/**
	 * Checks whether the logging list contains a logged message of the given
	 * logging level or not
	 *
	 * @param level The logging level to check
	 * @return true if the list contains a message with the given logging level
	 */
	public boolean contains(Level level) {
		for (ILoggingEvent event : this.list) {
			if (event.getLevel().equals(level)) {
				return true;
			}
		}
		return false;
	}
}
