package de.dai_labor.conversation_engine_gui.TextFormatter;

import javafx.scene.control.TextFormatter;

/**
 * Static class to create new {@link TextFormatter Textformatters}
 *
 * @author Marcel Engelmann
 *
 */
public class TextFormatters {

	/**
	 * Gets a new TextFormatter that only allows positive {@link Integer} values
	 *
	 * @return a new TextFormatter that only allows positive {@link Integer} values
	 */
	public static TextFormatter<Integer> getPositiveIntegerTextFormatter() {
		return new TextFormatter<>(c -> {
			if (c.getControlNewText().isEmpty() || c.isDeleted()) {
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

	/**
	 * Gets a new TextFormatter that only allows positive {@link Double} values
	 *
	 * @return a new TextFormatter that only allows positive {@link Double} values
	 */

	public static TextFormatter<Double> getPositiveDoubleTextFormatter() {
		return new TextFormatter<>(c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			try {
				final double value = Double.parseDouble(c.getControlNewText());
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