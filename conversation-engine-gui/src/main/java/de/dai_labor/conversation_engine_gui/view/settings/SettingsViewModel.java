package de.dai_labor.conversation_engine_gui.view.settings;

import de.dai_labor.conversation_engine_gui.models.Settings;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class SettingsViewModel implements ViewModel {
	private Settings settings = null;
	private static final int MIN_STATE_SIZE = 10;
	private static final int MAX_STATE_SIZE = 200;

	public SettingsViewModel(Settings settings) {
		this.settings = settings;
	}

	public SimpleIntegerProperty getStateSizeProperty() {
		return this.settings.getStateSizeProperty();
	}

	public SimpleIntegerProperty getStateFontSizeProperty() {
		return this.settings.getStateFontSizeProperty();
	}

	public SimpleObjectProperty<Color> getStateNormalColorProperty() {
		return this.settings.getStateNormalColorProperty();
	}

	public SimpleObjectProperty<Color> getStateSelectedColorProperty() {
		return this.settings.getStateSelectedColorProperty();
	}

	public SimpleObjectProperty<Color> getStateFontColorProperty() {
		return this.settings.getStateFontColorProperty();
	}

	public SimpleIntegerProperty getTransitionSizeProperty() {
		return this.settings.getTransitionSizeProperty();
	}

	public SimpleIntegerProperty getTransitionFontSizeProperty() {
		return this.settings.getTransitionFontSizeProperty();
	}

	public SimpleObjectProperty<Color> getTransitionNormalColorProperty() {
		return this.settings.getTransitionNormalColorProperty();
	}

	public SimpleObjectProperty<Color> getTransitionSelectedColorProperty() {
		return this.settings.getTransitionSelectedColorProperty();
	}

	public SimpleObjectProperty<Color> getTransitionFontColorProperty() {
		return this.settings.getTransitionFontColorProperty();
	}

}
