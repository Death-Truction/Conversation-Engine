package de.dai_labor.dialog_modeling_tool.view.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The DialogView is the view that allows the user to create the skill's state
 * machine model
 *
 * @author Marcel Engelmann
 *
 */
public class DialogView implements FxmlView<DialogViewModel>, Initializable {

	@InjectViewModel
	private DialogViewModel viewModel;
	@FXML
	private BorderPane mainPane;
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
		this.mainPane.centerProperty().bindBidirectional(this.viewModel.getViewProperty());
		this.mainPane.getCenter().toBack();
		this.viewModel.addToToggleGroup(this.stateButton);
		this.viewModel.addToToggleGroup(this.transitionButton);
		this.resetButton.setOnAction(this.viewModel::resetView);
	}

}
