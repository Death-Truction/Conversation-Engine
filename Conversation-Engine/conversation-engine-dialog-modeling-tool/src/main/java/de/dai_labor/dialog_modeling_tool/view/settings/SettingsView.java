package de.dai_labor.dialog_modeling_tool.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import de.dai_labor.dialog_modeling_tool.TextFormatter.TextFormatters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * The SettingsView handles all the application settings
 *
 * @author Marcel Engelmann
 *
 */
public class SettingsView implements FxmlView<SettingsViewModel>, Initializable {

	@InjectViewModel
	private SettingsViewModel viewModel;
	@FXML
	private TextField stateSize;
	@FXML
	private ColorPicker stateNormalColor;
	@FXML
	private ColorPicker stateSelectedColor;
	@FXML
	private TextField stateFontSize;
	@FXML
	private ColorPicker stateFontColor;
	@FXML
	private TextField transitionSize;
	@FXML
	private ColorPicker transitionNormalColor;
	@FXML
	private TextField transitionFontSize;
	@FXML
	private ColorPicker transitionFontColor;
	@FXML
	private ColorPicker transitionSelectedColor;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		StringConverter<? extends Number> converter = new IntegerStringConverter();

		this.stateSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.stateSize.textProperty().bindBidirectional(this.viewModel.getStateSizeProperty(),
				(StringConverter<Number>) converter);
		this.addinputBoundsValidator(10, 200, this.stateSize);

		this.stateFontSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.stateFontSize.textProperty().bindBidirectional(this.viewModel.getStateFontSizeProperty(),
				(StringConverter<Number>) converter);
		this.addinputBoundsValidator(8, 72, this.stateFontSize);

		this.stateFontColor.valueProperty().bindBidirectional(this.viewModel.getStateFontColorProperty());

		this.stateNormalColor.valueProperty().bindBidirectional(this.viewModel.getStateNormalColorProperty());

		this.stateSelectedColor.valueProperty().bindBidirectional(this.viewModel.getStateSelectedColorProperty());

		this.transitionSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.transitionSize.textProperty().bindBidirectional(this.viewModel.getTransitionSizeProperty(),
				(StringConverter<Number>) converter);
		this.addinputBoundsValidator(2, 30, this.transitionSize);

		this.transitionFontSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.transitionFontSize.textProperty().bindBidirectional(this.viewModel.getTransitionFontSizeProperty(),
				(StringConverter<Number>) converter);
		this.addinputBoundsValidator(8, 72, this.transitionFontSize);

		this.transitionFontColor.valueProperty().bindBidirectional(this.viewModel.getTransitionFontColorProperty());

		this.transitionNormalColor.valueProperty().bindBidirectional(this.viewModel.getTransitionNormalColorProperty());

		this.transitionSelectedColor.valueProperty()
				.bindBidirectional(this.viewModel.getTransitionSelectedColorProperty());
	}

	/**
	 * * Adds a listener to the {@link TextField} that checks whether the value of
	 * the {@link TextField} is within the given bounds. If the value is outside of
	 * the given bounds than the value will be replaced with the nearest bound value
	 *
	 * @param minValue  The minimum value bound
	 * @param maxValue  The maximum value bound
	 * @param textField The {@link TextField} to add the listener to
	 * @throws IllegalArgumentException if the minValue is greater than the maxValue
	 */
	private void addinputBoundsValidator(int minValue, int maxValue, TextField textField)
			throws IllegalArgumentException {
		if (minValue > maxValue) {
			throw new IllegalArgumentException("The min value must not be greater than the max value");
		}
		textField.focusedProperty().addListener(change -> {
			if (!textField.isFocused()) {
				String text = textField.getText();
				if (text == null || text.isBlank()) {
					textField.setText("" + minValue);
				}
				Double value = Double.parseDouble(textField.getText());
				if (value < minValue) {
					textField.setText("" + minValue);
				} else if (value > maxValue) {
					textField.setText("" + maxValue);
				}
			}
		});
	}

}
