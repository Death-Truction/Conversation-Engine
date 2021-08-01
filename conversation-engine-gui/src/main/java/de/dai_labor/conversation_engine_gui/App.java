package de.dai_labor.conversation_engine_gui;

import java.io.IOException;

import de.dai_labor.conversation_engine_gui.view.main.MainView;
import de.dai_labor.conversation_engine_gui.view.main.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import eu.lestard.easydi.EasyDI;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		EasyDI easyDI = new EasyDI();
		MvvmFX.setCustomDependencyInjector(easyDI::getInstance);
		// configure stage
		stage.setTitle("ConversationEngine - Dialog Modeling Tool");
		Image icon = new Image(getClass().getResource("images/Icon.png").toExternalForm());
		stage.getIcons().add(icon);
		stage.minHeightProperty().set(480.0);
		stage.minWidthProperty().set(640.0);
		stage.setHeight(720);
		stage.setWidth(1280);
		ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();
		Parent mainScene = viewTuple.getView();
		mainScene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
		stage.setScene(new Scene(mainScene));
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(App.class, args);
	}

}