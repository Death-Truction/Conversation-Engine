package de.dai_labor.conversation_engine_gui.TextFormatter;

import javafx.scene.control.TextFormatter;

public class TextFormatters {

	public static TextFormatter<Integer> getPositiveIntegerTextFormatter() {
		return new TextFormatter<Integer>(c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			try {
				final int value = Integer.parseInt(c.getControlNewText());
				if (value < 0) {
					return null;
				}
			} catch (final NumberFormatException nfe) {
				return null;
			}
			return c;
		});
	}

}
