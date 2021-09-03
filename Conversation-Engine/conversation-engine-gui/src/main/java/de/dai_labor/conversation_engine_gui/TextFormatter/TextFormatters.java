package de.dai_labor.conversation_engine_gui.TextFormatter;

import javafx.scene.control.TextFormatter;

public class TextFormatters {

	public static TextFormatter<Integer> getIntegerTextFormatter(int minValue, int maxValue) {
		return new TextFormatter<>(c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			try {
				final int value = Integer.parseInt(c.getControlNewText());
				if (value < minValue) {
					c.setRange(0, c.getControlText().length());
					c.setText("" + minValue);
				} else if (value > maxValue) {
					c.setRange(0, c.getControlText().length());
					c.setText("" + maxValue);
				}
			} catch (final NumberFormatException nfe) {
				return null;
			}
			return c;
		});
	}

	public static TextFormatter<Double> getDoubleTextFormatter(double minValue, double maxValue) {
		return new TextFormatter<>(c -> {
			if (c.getControlNewText().isEmpty()) {
				return c;
			}

			try {
				final double value = Double.parseDouble(c.getControlNewText());
				if (value < minValue) {
					c.setRange(0, c.getControlText().length());
					c.setText("" + minValue);
				} else if (value > maxValue) {
					c.setRange(0, c.getControlText().length());
					c.setText("" + maxValue);
				}
			} catch (final NumberFormatException nfe) {
				return null;
			}
			return c;
		});
	}
}