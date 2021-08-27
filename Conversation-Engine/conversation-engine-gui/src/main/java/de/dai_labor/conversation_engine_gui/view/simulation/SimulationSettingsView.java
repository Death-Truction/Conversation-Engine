package de.dai_labor.conversation_engine_gui.view.simulation;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SimulationSettingsView implements Initializable, FxmlView<SimulationSettingsViewModel> {

	@InjectViewModel
	private SimulationSettingsViewModel viewModel;
	@FXML
	private ComboBox<String> language;
	@FXML
	private TextArea conversationInputs;
	@FXML
	private Button startButton;
	@FXML
	private TextField nlpComponentPath;
	@FXML
	private Button nlpComponentPathButton;
	@FXML
	private Button selectSkillFilePath;
	@FXML
	private TextField skillFilePath;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.language.setItems(this.viewModel.getAvailableLanguages());
		this.language.valueProperty().bindBidirectional(this.viewModel.getSelectedLanguageProperty());
		this.conversationInputs.textProperty().bindBidirectional(this.viewModel.getConversationInputProperty());
		this.skillFilePath.textProperty().bindBidirectional(this.viewModel.getSkillFilePathProperty());
		this.nlpComponentPath.textProperty().bindBidirectional(this.viewModel.getNLPComponentPathProperty());
		this.nlpComponentPathButton.setOnAction(event -> this.viewModel.pickNLPComponentFilePath());
		this.selectSkillFilePath.setOnAction(event -> this.viewModel.pickSkillFilePath());
		this.startButton.setOnAction(event -> this.viewModel.startSimulation());
	}

}
