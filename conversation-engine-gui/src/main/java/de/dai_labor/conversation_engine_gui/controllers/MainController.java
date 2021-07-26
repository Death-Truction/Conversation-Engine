package de.dai_labor.conversation_engine_gui.controllers;

import java.io.IOException;
import java.net.URL;

import de.dai_labor.conversation_engine_gui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainController {

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	private void handleShowView(ActionEvent e) {
		String view = "views/" + (String) ((Node) e.getSource()).getUserData();
		loadFXML(App.class.getResource(view));
	}

	private void loadFXML(URL url) {
		try {
			FXMLLoader loader = new FXMLLoader(url);
			mainBorderPane.setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
