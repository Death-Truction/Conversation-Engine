package de.dai_labor.conversation_engine_gui.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import eu.lestard.easydi.EasyDI;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

public class Util {
	private Util() {
	}

	public static void saveGUIDataToFile(boolean askFirst, boolean saveToNewFile) {
		EasyDI easyDI = App.easyDI;
		DialogueViewModel dialogueViewModel = easyDI.getInstance(DialogueViewModel.class);
		Settings settings = easyDI.getInstance(Settings.class);
		if (!saveToNewFile && !dialogueViewModel.hasChanged()) {
			return;
		}
		String filepath = "";
		if (!saveToNewFile) {
			filepath = settings.getOpenedFilePath();
		}
		// ask user if he wants to save the unsaved changes
		if (askFirst && !Util.saveDataBeforeExit()) {
			return;
		}
		JSONObject savedData = dialogueViewModel.getGUIData();
		String data = savedData.toString();
		// if the file has not been saved before -> ask for a save location
		if (filepath.isBlank())
			filepath = Util.pickFilepath(true);
		// if the user picked a file
		if (!filepath.isBlank()) {
			settings.setOpenedFilePath(filepath);
			Util.saveJSONStringToFile(filepath, data);
			dialogueViewModel.hasChanged(false);
		}

	}

	public static void loadGUIDataFromFile() {

		EasyDI easyDI = App.easyDI;
		Settings settings = easyDI.getInstance(Settings.class);
		String filepath = pickFilepath(false);
		settings.setOpenedFilePath(filepath);
		String jsonString = loadStringFromFile(filepath);
		if (jsonString.isBlank()) {
			return;
		}
		JSONObject guiData = new JSONObject(jsonString);
		if (guiData != null) {
			DialogueViewModel dialogueViewModel = easyDI.getInstance(DialogueViewModel.class);
			dialogueViewModel.setGUIData(guiData);
		}
	}

	public static String loadStringFromFile(String filepath) {
		String data = "";
		try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
			data = lines.collect(Collectors.joining("\n"));

		} catch (IOException e) {
			// TODO alert that an error occurred?
		}
		return data;
	}

	public static void showError(String title, String content) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText(title);
		errorAlert.setContentText(content);
		errorAlert.showAndWait();
	}

	private static boolean saveDataBeforeExit() {

		Alert saveBeforeExitConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"You have unsaved changes.\nWould you like to save them now?");
		saveBeforeExitConfirmation.setHeaderText("Save unsaved changes");
		saveBeforeExitConfirmation.setTitle("Unsaved changes");
		saveBeforeExitConfirmation.initModality(Modality.APPLICATION_MODAL);
		saveBeforeExitConfirmation.initOwner(App.mainStage);
		((Button) saveBeforeExitConfirmation.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
		((Button) saveBeforeExitConfirmation.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
		Optional<ButtonType> closeResponse = saveBeforeExitConfirmation.showAndWait();
		if (closeResponse.isPresent()) {
			return ButtonType.OK.equals(closeResponse.get());
		}
		return false;
	}

	private static String pickFilepath(boolean save) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CE-GUI File", "*.cegui"));
		File file = null;
		if (save) {
			fileChooser.setTitle("Save file");
			file = fileChooser.showSaveDialog(App.mainStage);
		} else {
			fileChooser.setTitle("Open file");
			file = fileChooser.showOpenDialog(App.mainStage);
		}
		if (file == null) {
			return "";
		}
		return file.getAbsolutePath();
	}

	private static void saveJSONStringToFile(String filepath, String data) {
		try (FileWriter fileWriter = new FileWriter(filepath)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException e) {
			// TODO alert that there was an error?
			e.printStackTrace();
		}

	}
}
