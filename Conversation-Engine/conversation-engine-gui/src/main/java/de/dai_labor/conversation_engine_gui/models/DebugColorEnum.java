package de.dai_labor.conversation_engine_gui.models;

import java.util.Objects;

import javafx.scene.paint.Color;

/**
 * An enum that maps a color to each logging level
 *
 * @author Marcel Engelmann
 *
 */
public enum DebugColorEnum {
	/**
	 * The {@link Color} of the logging level TRACE
	 */
	TRACE(Color.BLUE),
	/**
	 * The {@link Color} of the logging level DEBUG
	 */
	DEBUG(Color.BLUE),
	/**
	 * The {@link Color} of the logging level INFO
	 */
	INFO(Color.CYAN),
	/**
	 * The {@link Color} of the logging level WARN
	 */
	WARN(Color.YELLOW),
	/**
	 * The {@link Color} of the logging level ERROR
	 */
	ERROR(Color.RED);

	private final Color color;

	DebugColorEnum(Color color) {
		this.color = Objects.requireNonNull(color);
	}

	/**
	 * Gets the {@link Color} mapped to the logging level
	 *
	 * @return the {@link Color} mapped to the logging level
	 */
	public Color getColor() {
		return this.color;
	}
}
