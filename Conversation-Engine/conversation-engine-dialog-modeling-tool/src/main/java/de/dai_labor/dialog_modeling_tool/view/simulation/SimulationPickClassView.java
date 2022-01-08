package de.dai_labor.dialog_modeling_tool.view.simulation;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * The SimulationPickClassView is used to select a single class of multiple
 * possible classes
 *
 * @author Marcel Engelmann
 *
 */
public class SimulationPickClassView implements FxmlView<SimulationPickClassViewModel>, Initializable {

	@InjectViewModel
	private SimulationPickClassViewModel viewModel;
	@FXML
	private ListView<Class<?>> listView;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.listView.setItems(this.viewModel.getAllClasses());
		this.viewModel.getSelectedClassProperty().bind(this.listView.getSelectionModel().selectedItemProperty());
		this.okButton.setOnAction(event -> this.viewModel.okButtonPressed());
		this.cancelButton.setOnAction(event -> this.viewModel.cancelButtonPressed());
	}

}
