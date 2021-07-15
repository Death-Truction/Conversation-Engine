package de.dai_labor.conversations_engine_gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		Scene mainScene = new Scene(loadFXML("views/main"), 1280, 720);
		mainScene.getStylesheets().add(getClass().getResource("styles/test.css").toExternalForm());
		stage.setScene(mainScene);
		stage.show();
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	/**
	 * Launches the user interface application
	 * 
	 * @param args These arguments are ignored
	 */
	public static void main(String[] args) {
		launch();
	}

}