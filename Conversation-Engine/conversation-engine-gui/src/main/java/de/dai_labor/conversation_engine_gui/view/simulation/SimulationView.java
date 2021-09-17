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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * The SimulationView displays the simulation for the skill's state machine
 *
 * @author Marcel Engelmann
 *
 */
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
	@FXML
	private SplitPane messagesSplitPane;
	@FXML
	private BorderPane loadingScreen;
	@FXML
	private Label loadingProgressLabel;
	@FXML
	private ProgressBar loadingProgressBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TextFormatter<Double> positiveDoubleTextFormatter = TextFormatters.getPositiveDoubleTextFormatter();
		this.setDisablePropertyBindings();
		this.speedTextField.setTextFormatter(positiveDoubleTextFormatter);
		this.speedTextField.textProperty().bindBidirectional(this.viewModel.getSimulationSpeedProperty());
		this.playPauseButton.textProperty().bindBidirectional(this.viewModel.getPlayPauseButtonTextProperty());
		this.startButton.setOnAction(event -> this.viewModel.toStart());
		this.stepBackButton.setOnAction(event -> this.viewModel.stepBackwards());
		this.playPauseButton.setOnAction(event -> this.viewModel.playPause());
		this.stepForwardButton.setOnAction(event -> this.viewModel.stepForward());
		this.endButton.setOnAction(event -> this.viewModel.toEnd());
		this.viewModel.setConversationVBoxChildren(this.conversationVBox.getChildren());
		this.viewModel.setLoggingVBoxChildren(this.loggingVBox.getChildren());
		Node view = this.viewModel.getDialoguePane();
		this.mainView.getChildren().add(view);
		view.toBack();
		if (!this.viewModel.showLoggingVBox()) {
			this.messagesSplitPane.getItems().remove(1);
		}

		this.loadingProgressBar.progressProperty().bindBidirectional(this.viewModel.getLoadingProgressValueProperty());
		this.loadingProgressLabel.textProperty().bindBidirectional(this.viewModel.getLoadingProgressLabelProperty());
		this.viewModel.getLoadingProgressValueProperty().addListener(change -> {
			if (this.viewModel.getLoadingProgressValueProperty().get() >= 1) {
				((AnchorPane) this.loadingScreen.getParent()).getChildren().remove(this.loadingScreen);
				this.mainView.setVisible(true);
			}
		});
	}

	/**
	 * Sets the buttons disabled properties accordingly after the simulation
	 * play/pause button has been pressed
	 */
	private void setDisablePropertyBindings() {
		this.startButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.stepBackButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.stepForwardButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.endButton.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
		this.speedTextField.disableProperty().bind(this.viewModel.getSimulationIsRunningProperty());
	}

}
