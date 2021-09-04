package de.dai_labor.conversation_engine_gui.models;

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

	public void reset() {
		this.list.clear();
	}

	public List<ILoggingEvent> getMessagesCopy() {
		return new ArrayList<>(this.list);
	}

	public boolean contains(Level level) {
		for (ILoggingEvent event : this.list) {
			if (event.getLevel().equals(level)) {
				return true;
			}
		}
		return false;
	}
}
