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
	private ColorPicker transitionColor;
	@FXML
	private TextField transitionFontSize;
	@FXML
	private ColorPicker transitionFontColor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.stateSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.stateSize.textProperty().bindBidirectional(this.viewModel.getStateSizeProperty());
		this.stateFontSize.setTextFormatter(TextFormatters.getPositiveIntegerTextFormatter());
		this.stateFontSize.textProperty().bindBidirectional(this.viewModel.getStateFontSizeProperty());
		this.stateFontColor.valueProperty().bindBidirectional(this.viewModel.getStateFontColorProperty());
		this.stateNormalColor.valueProperty().bindBidirectional(this.viewModel.getStateNormalColorProperty());
		this.stateSelectedColor.valueProperty().bindBidirectional(this.viewModel.getStateSelectedColorProperty());
	}

}
