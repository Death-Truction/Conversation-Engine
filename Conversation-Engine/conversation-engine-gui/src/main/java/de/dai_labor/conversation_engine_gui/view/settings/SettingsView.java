package de.dai_labor.conversation_engine_gui.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import de.dai_labor.conversation_engine_gui.TextFormatter.TextFormatters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Converters
		StringConverter<? extends Number> converter = new IntegerStringConverter();
		this.stateSize.setTextFormatter(TextFormatters.getIntegerTextFormatter(10, 200));
		this.stateSize.textProperty().bindBidirectional(this.viewModel.getStateSizeProperty(),
				(StringConverter<Number>) converter);
		this.stateFontSize.setTextFormatter(TextFormatters.getIntegerTextFormatter(0, 72));
		this.stateFontSize.textProperty().bindBidirectional(this.viewModel.getStateFontSizeProperty(),
				(StringConverter<Number>) converter);
		this.stateFontColor.valueProperty().bindBidirectional(this.viewModel.getStateFontColorProperty());
		this.stateNormalColor.valueProperty().bindBidirectional(this.viewModel.getStateNormalColorProperty());
		this.stateSelectedColor.valueProperty().bindBidirectional(this.viewModel.getStateSelectedColorProperty());
		this.transitionSize.setTextFormatter(TextFormatters.getIntegerTextFormatter(0, 25));
		this.transitionSize.textProperty().bindBidirectional(this.viewModel.getTransitionSizeProperty(),
				(StringConverter<Number>) converter);
		this.transitionFontSize.setTextFormatter(TextFormatters.getIntegerTextFormatter(0, 72));
		this.transitionFontSize.textProperty().bindBidirectional(this.viewModel.getTransitionFontSizeProperty(),
				(StringConverter<Number>) converter);
		this.transitionFontColor.valueProperty().bindBidirectional(this.viewModel.getTransitionFontColorProperty());
		this.transitionNormalColor.valueProperty().bindBidirectional(this.viewModel.getTransitionNormalColorProperty());
		this.transitionSelectedColor.valueProperty()
				.bindBidirectional(this.viewModel.getTransitionSelectedColorProperty());
	}

}
