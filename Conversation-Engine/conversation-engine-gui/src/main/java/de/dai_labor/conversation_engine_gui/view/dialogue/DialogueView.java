package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class DialogueView implements FxmlView<DialogueViewModel>, Initializable {

	@InjectViewModel
	private DialogueViewModel viewModel;

	@FXML
	private BorderPane mainPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.mainPane.centerProperty().bindBidirectional(this.viewModel.getViewProperty());
	}

}
