package de.dai_labor.conversation_engine_gui;

import java.io.IOException;

import de.dai_labor.conversation_engine_gui.models.SaveStateEnum;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.help.HelpStage;
import de.dai_labor.conversation_engine_gui.view.main.MainView;
import de.dai_labor.conversation_engine_gui.view.main.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import eu.lestard.easydi.EasyDI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The GUI Application entry point class
 *
 * @author Marcel Engelmann
 *
 */
public class App extends Application {

	/**
	 * the global {@link EasyDI} dependency injection object
	 */
	public static final EasyDI easyDI = new EasyDI();
	/**
	 * the main {@link Stage} of the application
	 */
	public static Stage mainStage;

	@Override
	public void start(Stage stage) throws IOException {
		mainStage = stage;
		mainStage.setOnCloseRequest(this.saveBeforeExitConfirmationEventHandler);
		MvvmFX.setCustomDependencyInjector(easyDI::getInstance);
		stage.setTitle("ConversationEngine - Dialogue Modeling Tool");
		final Image icon = Util.getIcon();
		stage.getIcons().add(icon);
		stage.minHeightProperty().set(480.0);
		stage.minWidthProperty().set(640.0);
		stage.setHeight(720);
		stage.setWidth(1280);
		final ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();
		final Parent mainScene = viewTuple.getView();
		mainScene.getStylesheets().add(Util.getStyleSheetPath());
		stage.setScene(new Scene(mainScene));
		// preload help panel for faster access
		Platform.runLater(HelpStage::create);
		stage.show();
	}

	/**
	 * {@link EventHandler} for the onCloseRequest event <br>
	 * It checks for unsaved changes and prompts the user to save or discard them
	 * The close request will be canceled if the user cancels the saving progress
	 */
	private final EventHandler<WindowEvent> saveBeforeExitConfirmationEventHandler = event -> {
		if (Util.saveGUIDataToFile(true, false) == SaveStateEnum.CANCEL) {
			event.consume();
		}
		easyDI.getInstance(Settings.class).savePrefs();
	};

}