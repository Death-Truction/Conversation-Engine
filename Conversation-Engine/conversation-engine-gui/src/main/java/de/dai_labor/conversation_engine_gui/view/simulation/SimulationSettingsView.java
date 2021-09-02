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
	private TextField nlpComponent;
	@FXML
	private Button selectNLPComponentButton;
	@FXML
	private Button removeNLPComponentButton;
	@FXML
	private TextField skill;
	@FXML
	private Button selectSkillButton;
	@FXML
	private Button removeSkillButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.language.setItems(this.viewModel.getAvailableLanguages());
		this.language.valueProperty().bindBidirectional(this.viewModel.getSelectedLanguageProperty());

		this.conversationInputs.textProperty().bindBidirectional(this.viewModel.getConversationInputProperty());

		this.nlpComponent.textProperty().bindBidirectional(this.viewModel.getSelectedNLPComponentProperty());
		this.nlpComponent.textProperty().addListener(change -> this.moveCaretToEnd(this.nlpComponent));
		this.nlpComponent.focusedProperty().addListener(change -> this.moveCaretToEnd(this.nlpComponent));
		this.selectNLPComponentButton.setOnAction(event -> this.viewModel.pickNLPComponentFile());
		this.removeNLPComponentButton.setOnAction(event -> this.viewModel.removeNLPComponent());

		this.skill.textProperty().bindBidirectional(this.viewModel.getSelectedSkillProperty());
		this.skill.textProperty().addListener(change -> this.moveCaretToEnd(this.skill));
		this.skill.focusedProperty().addListener(change -> this.moveCaretToEnd(this.skill));
		this.selectSkillButton.setOnAction(event -> this.viewModel.pickSkillFile());
		this.removeSkillButton.setOnAction(event -> this.viewModel.removeSkill());

		this.startButton.setOnAction(event -> this.viewModel.startSimulation());
	}

	private void moveCaretToEnd(TextField target) {
		target.end();
	}

}
