package de.dai_labor.conversation_engine_gui.view.simulation;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class SimulationView implements FxmlView<SimulationViewModel>, Initializable {

	@InjectViewModel
	private SimulationViewModel viewModel;
	@FXML
	private BorderPane mainView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Node view = this.viewModel.getDialoguePane();
		this.mainView.getChildren().add(view);
		view.toBack();
	}

}
