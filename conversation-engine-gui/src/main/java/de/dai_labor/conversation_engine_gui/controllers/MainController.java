package de.dai_labor.conversation_engine_gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.dai_labor.conversation_engine_gui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

	private Map<String, Node> scenes = new HashMap<>();

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	private VBox contentArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		changeView("dialogModelView.fxml");
		// set the new view to the back
		contentArea.setViewOrder(1000);
	}

	@FXML
	private void navigateViewEvent(ActionEvent e) {
		String viewName = (String) ((Node) e.getSource()).getUserData();
		changeView(viewName);
	}

	private void changeView(String viewName) {
		if (scenes.containsKey(viewName)) {
			setScene(scenes.get(viewName));
		} else {
			String viewPath = "views/" + viewName;
			Node newView = loadFXML(App.class.getResource(viewPath));
			scenes.put(viewName, newView);
			this.setScene(newView);
		}
	}

	private Node loadFXML(URL url) {
		try {
			FXMLLoader loader = new FXMLLoader(url);
			return loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setScene(Node view) {
		if (view == null) {
			return;
		}
		this.contentArea.getChildren().clear();
		this.contentArea.getChildren().add(view);
		VBox.setVgrow(view, Priority.ALWAYS);
	}
}
