package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * The DialogueData collects the information required to create the skill's
 * state machine model
 *
 * @author Marcel Engelmann
 *
 */
public class DialogueDataView implements FxmlView<DialogueDataViewModel>, Initializable {

	@InjectViewModel
	private DialogueDataViewModel viewModel;
	@FXML
	private TextArea intents;
	@FXML
	private TextArea entities;
	@FXML
	private ComboBox<String> startState;
	@FXML
	private ComboBox<String> endState;
	@FXML
	private TextField skillName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bindData();
		this.addEventListener();
	}

	/**
	 * Binds the data of the GUI elements to the ViewModel properties
	 */
	private void bindData() {
		this.startState.setItems(this.viewModel.getAvailableState());
		this.startState.valueProperty().bindBidirectional(this.viewModel.getSelectedStartStateProperty());
		this.endState.valueProperty().bindBidirectional(this.viewModel.getSelectedEndStateProperty());
		this.endState.setItems(this.viewModel.getAvailableState());
		this.intents.textProperty().bindBidirectional(this.viewModel.getIntentsProperty());
		this.entities.textProperty().bindBidirectional(this.viewModel.getEntitiesProperty());
		this.skillName.textProperty().bindBidirectional(this.viewModel.getSkillNameProperty());
	}

	/**
	 * Sets up the event listeners for the GUI elements
	 */
	private void addEventListener() {
		this.startState.addEventHandler(MouseEvent.MOUSE_RELEASED, this.viewModel::updateAvailableStates);
		this.endState.addEventHandler(MouseEvent.MOUSE_RELEASED, this.viewModel::updateAvailableStates);
	}

}
