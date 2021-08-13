package de.dai_labor.conversation_engine_gui.view.settings;

import de.dai_labor.conversation_engine_gui.models.Settings;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;

public class SettingsViewModel implements ViewModel {

	private final SimpleStringProperty stateSizeProperty = new SimpleStringProperty();
	private final SimpleStringProperty stateFontSizeProperty = new SimpleStringProperty();
	private final SimpleObjectProperty<Color> stateFontColorProperty = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Color> stateNormalColorProperty = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Color> stateSelectedColorProperty = new SimpleObjectProperty<>();
	private Settings settings = null;
	private static final int MIN_STATE_SIZE = 10;
	private static final int MAX_STATE_SIZE = 200;

	public SettingsViewModel(Settings settings) {
		this.settings = settings;
		this.setInitValues();
		this.addListeners();
	}

	public SimpleStringProperty getStateSizeProperty() {
		return this.stateSizeProperty;
	}

	public SimpleStringProperty getStateFontSizeProperty() {
		return this.stateFontSizeProperty;
	}

	public SimpleObjectProperty<Color> getStateNormalColorProperty() {
		return this.stateNormalColorProperty;
	}

	public SimpleObjectProperty<Color> getStateSelectedColorProperty() {
		return this.stateSelectedColorProperty;
	}

	public SimpleObjectProperty<Color> getStateFontColorProperty() {
		return this.stateFontColorProperty;
	}

	private void setInitValues() {
		this.stateSizeProperty.set(String.valueOf(this.settings.getStateSizeProperty().get()));
		this.stateFontSizeProperty.set(String.valueOf(this.settings.getStateFontSizeProperty().get()));
		this.stateFontColorProperty.set(this.settings.getStateFontColorProperty().get());
		this.stateNormalColorProperty.set(this.settings.getStateNormalColorProperty().get());
		this.stateSelectedColorProperty.set(this.settings.getStateSelectedColorProperty().get());
	}

	private void addListeners() {
		this.stateSizeProperty.addListener(this.stateSizeListener);
		this.stateFontSizeProperty.addListener(this.stateFontSizeListener);
		this.stateFontColorProperty.addListener(this.stateFontColorListener);
		this.stateNormalColorProperty.addListener(this.stateNormalColorListener);
		this.stateSelectedColorProperty.addListener(this.stateSelectedColorListener);
	}

	private final ChangeListener<String> stateSizeListener = (observable, oldVal, newVal) -> {
		this.setIntegerPropertyValue(this.settings.getStateSizeProperty(), newVal);
	};

	private final ChangeListener<String> stateFontSizeListener = (observable, oldVal, newVal) -> {
		this.setIntegerPropertyValue(this.settings.getStateFontSizeProperty(), newVal);
	};

	private void setIntegerPropertyValue(SimpleIntegerProperty settingsProperty, String value) {
		if (value.isEmpty()) {
			settingsProperty.set(10);
			return;
		}
		int val = Integer.parseInt(value);
		if (val < MIN_STATE_SIZE) {
			val = MIN_STATE_SIZE;
		} else if (val > MAX_STATE_SIZE) {
			val = MAX_STATE_SIZE;
		}
		settingsProperty.set(val);
	}

	private final ChangeListener<Color> stateNormalColorListener = (observable, oldVal, newVal) -> {
		this.settings.getStateNormalColorProperty().set(newVal);
	};

	private final ChangeListener<Color> stateSelectedColorListener = (observable, oldVal, newVal) -> {
		this.settings.getStateSelectedColorProperty().set(newVal);
	};
	private final ChangeListener<Color> stateFontColorListener = (observable, oldVal, newVal) -> {
		this.settings.getStateFontColorProperty().set(newVal);
	};

}
