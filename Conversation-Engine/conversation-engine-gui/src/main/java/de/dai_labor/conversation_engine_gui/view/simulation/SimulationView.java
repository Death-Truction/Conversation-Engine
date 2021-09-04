package de.dai_labor.conversation_engine_gui.view.simulation;

import java.net.URL;
import java.util.ResourceBundle;

import de.dai_labor.conversation_engine_gui.TextFormatter.TextFormatters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SimulationView implements FxmlView<SimulationViewModel>, Initializable {

	@InjectViewModel
	private SimulationViewModel viewModel;
	@FXML
	private BorderPane mainView;
	@FXML
	private Button startButton;
	@FXML
	private Button stepBackButton;
	@FXML
	private Button playPauseButton;
	@FXML
	private Button stepForwardButton;
	@FXML
	private Button endButton;
	@FXML
	private TextField speedTextField;
	@FXML
	private VBox conversationVBox;
	@FXML
	private VBox loggingVBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Node view = this.viewModel.getDialoguePane();
		this.mainView.getChildren().add(view);
		view.toBack();
		this.setDisablePropertyBindings();
		this.speedTextField.setTextFormatter(TextFormatters.getDoubleTextFormatter());
		this.speedTextField.textProperty().bindBidirectional(this.viewModel.getSimulationSpeedProperty());
		this.playPauseButton.textProperty().bindBidirectional(this.viewModel.getPlayPauseButtonTextProperty());
		this.startButton.setOnAction(event -> this.viewModel.toStart());
		this.stepBackButton.setOnAction(event -> this.viewModel.stepBackwards());
		this.playPauseButton.setOnAction(event -> this.viewModel.playPause());
		this.stepForwardButton.setOnAction(event -> this.viewModel.stepForward());
		this.endButton.setOnAction(event -> this.viewModel.toEnd());
		this.viewModel.setConversationVBoxChildren(this.conversationVBox.getChildren());
		this.viewModel.setLoggingVBoxChildren(this.loggingVBox.getChildren());
	}

	private void setDisablePropertyBindings() {
		this.startButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.stepBackButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.stepForwardButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.endButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.speedTextField.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
	}

}
