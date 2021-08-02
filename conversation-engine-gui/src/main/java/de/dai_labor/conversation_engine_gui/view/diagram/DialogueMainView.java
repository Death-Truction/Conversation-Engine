package de.dai_labor.conversation_engine_gui.view.diagram;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class DialogueMainView implements FxmlView<DialogueMainViewModel>, Initializable {

	@InjectViewModel
	private DialogueMainViewModel viewModel;

	@FXML
	private BorderPane mainPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Node view = viewModel.getView();
		this.mainPane.setCenter(viewModel.getView());
		view.toBack();
	}

}
