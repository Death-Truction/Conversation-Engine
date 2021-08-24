package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

public class DialogueToolbarView implements FxmlView<DialogueToolbarViewModel>, Initializable {

	@InjectViewModel
	private DialogueToolbarViewModel viewModel;

	@FXML
	private ToggleButton stateButton;
	@FXML
	private ToggleButton transitionButton;
	@FXML
	private Button resetButton;
	@FXML
	private Pane stateButtonPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		viewModel.addToggleGroup(stateButton);
		viewModel.addToggleGroup(transitionButton);
		resetButton.setOnAction(viewModel::resetView);
	}
}
