package de.dai_labor.dialog_modeling_tool.view.settings;

import de.dai_labor.dialog_modeling_tool.gui_components.State;
import de.dai_labor.dialog_modeling_tool.gui_components.Transition;
import de.dai_labor.dialog_modeling_tool.models.Settings;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 * The ViewModel of the {@link SettingsView}
 *
 * @author Marcel Engelmann
 *
 */
public class SettingsViewModel implements ViewModel {
	private Settings settings = null;

	/**
	 * Creates a new {@link SettingsViewModel} instance
	 *
	 * @param settings The instance of the {@link Settings}
	 */
	public SettingsViewModel(Settings settings) {
		this.settings = settings;
	}

	/**
	 * Gets the {@link Property} of the {@link State} size
	 *
	 * @return the {@link Property} of the {@link State} size
	 */
	public SimpleIntegerProperty getStateSizeProperty() {
		return this.settings.getStateSizeProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link State} font size
	 *
	 * @return the {@link Property} of the {@link State} font size
	 */
	public SimpleIntegerProperty getStateFontSizeProperty() {
		return this.settings.getStateFontSizeProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link State} normal color
	 *
	 * @return the {@link Property} of the {@link State} normal color
	 */
	public SimpleObjectProperty<Color> getStateNormalColorProperty() {
		return this.settings.getStateNormalColorProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link State} selected color
	 *
	 * @return the {@link Property} of the {@link State} selected color
	 */
	public SimpleObjectProperty<Color> getStateSelectedColorProperty() {
		return this.settings.getStateSelectedColorProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link State} font color
	 *
	 * @return the {@link Property} of the {@link State} font color
	 */
	public SimpleObjectProperty<Color> getStateFontColorProperty() {
		return this.settings.getStateFontColorProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link Transition} size
	 *
	 * @return the {@link Property} of the {@link Transition} size
	 */
	public SimpleIntegerProperty getTransitionSizeProperty() {
		return this.settings.getTransitionSizeProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link Transition} font size
	 *
	 * @return the {@link Property} of the {@link Transition} font size
	 */
	public SimpleIntegerProperty getTransitionFontSizeProperty() {
		return this.settings.getTransitionFontSizeProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link Transition} normal color
	 *
	 * @return the {@link Property} of the {@link Transition} normal color
	 */
	public SimpleObjectProperty<Color> getTransitionNormalColorProperty() {
		return this.settings.getTransitionNormalColorProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link Transition} selected color
	 *
	 * @return the {@link Property} of the {@link Transition} selected color
	 */
	public SimpleObjectProperty<Color> getTransitionSelectedColorProperty() {
		return this.settings.getTransitionSelectedColorProperty();
	}

	/**
	 * Gets the {@link Property} of the {@link Transition} font color
	 *
	 * @return the {@link Property} of the {@link Transition} font color
	 */
	public SimpleObjectProperty<Color> getTransitionFontColorProperty() {
		return this.settings.getTransitionFontColorProperty();
	}

}
