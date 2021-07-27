package de.dai_labor.conversation_engine_gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import de.dai_labor.conversation_engine_gui.models.DialogModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class DialogModelController implements Initializable {

	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button addButton;

	private DialogModel dialogModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dialogModel = new DialogModel();
		Node view = this.dialogModel.getView();
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
		AnchorPane.setBottomAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		this.mainPane.getChildren().add(0, view);

	}

	@FXML
	private void addState(ActionEvent e) {
		this.dialogModel.addState();
	}

}
