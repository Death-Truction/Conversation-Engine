package de.dai_labor.conversation_engine_gui.models;

import java.util.Objects;

import javafx.scene.paint.Color;

public enum DebugColorEnum {
	TRACE(Color.BLUE), DEBUG(Color.BLUE), INFO(Color.CYAN), WARN(Color.YELLOW), ERROR(Color.RED);

	private final Color color;

	DebugColorEnum(Color color) {
		this.color = Objects.requireNonNull(color);
	}

	public Color getColor() {
		return this.color;
	}
}
