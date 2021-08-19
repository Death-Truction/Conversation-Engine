package de.dai_labor.conversation_engine_gui;

import java.io.IOException;

import de.dai_labor.conversation_engine_gui.util.SaveStateEnum;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.main.MainView;
import de.dai_labor.conversation_engine_gui.view.main.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import eu.lestard.easydi.EasyDI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

	public static EasyDI easyDI = new EasyDI();
	public static Stage mainStage;

	public static void main(String[] args) {
		Application.launch(App.class, args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		App.mainStage = stage;
		App.mainStage.setOnCloseRequest(this.saveBeforeExitConfirmationEventHandler);
		MvvmFX.setCustomDependencyInjector(easyDI::getInstance);
		// configure stage
		stage.setTitle("ConversationEngine - Dialog Modeling Tool");
		final Image icon = new Image(this.getClass().getResource("images/Icon.png").toExternalForm());
		stage.getIcons().add(icon);
		stage.minHeightProperty().set(480.0);
		stage.minWidthProperty().set(640.0);
		stage.setHeight(720);
		stage.setWidth(1280);
		final ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();
		final Parent mainScene = viewTuple.getView();
		mainScene.getStylesheets().add(this.getClass().getResource("styles/style.css").toExternalForm());
		stage.setScene(new Scene(mainScene));
		stage.show();
	}

	private final EventHandler<WindowEvent> saveBeforeExitConfirmationEventHandler = event -> {
		if (Util.saveGUIDataToFile(true, false, false) == SaveStateEnum.CANCEL) {
			event.consume();
		}
	};

}