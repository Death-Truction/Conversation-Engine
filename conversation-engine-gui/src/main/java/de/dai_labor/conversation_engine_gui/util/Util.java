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
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataViewModel;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import eu.lestard.easydi.EasyDI;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

public class Util {
	private Util() {
	}

	public static SaveStateEnum saveGUIDataToFile(boolean askFirst, boolean saveToNewFile, boolean forceSave) {
		EasyDI easyDI = App.easyDI;
		DialogueViewModel dialogueViewModel = easyDI.getInstance(DialogueViewModel.class);
		DialogueDataViewModel dialogueDataViewModel = easyDI.getInstance(DialogueDataViewModel.class);
		Settings settings = easyDI.getInstance(Settings.class);
		if (!saveToNewFile && !dialogueViewModel.hasChanged() && !dialogueDataViewModel.hasChanged() && !forceSave) {
			return SaveStateEnum.NO;
		}
		String filepath = "";
		if (!saveToNewFile) {
			filepath = settings.getLastOpenedFile();
		}
		// ask user if he wants to save the unsaved changes
		if (askFirst) {
			SaveStateEnum saveState = Util.saveDataBeforeExit();
			if (saveState != SaveStateEnum.YES) {
				return saveState;
			}
		}
		JSONObject savedData = new JSONObject();
		savedData.put("dialogueView", dialogueViewModel.getGUIData());
		savedData.put("dialogueData", dialogueDataViewModel.getGUIData());
		String data = savedData.toString();
		// if the file has not been saved before -> ask for a save location
		if (filepath.isBlank()) {
			filepath = Util.fileChooser(true, new ExtensionFilter("CEGUI File", "*.cegui", "*.CEGUI"));
		}
		// if the user picked a file
		if (!filepath.isBlank()) {
			if (!filepath.endsWith(".cegui")) {
				filepath += ".cegui";
			}
			settings.setLastOpenedFile(filepath);
			Util.saveJSONStringToFile(filepath, data);
			dialogueViewModel.setUnchanged();
			dialogueDataViewModel.setUnchanged();
			return SaveStateEnum.YES;
		}
		// if the user pressed cancel on the fileChooser
		return SaveStateEnum.CANCEL;
	}

	public static void loadGUIDataFromFile() {

		EasyDI easyDI = App.easyDI;
		Settings settings = easyDI.getInstance(Settings.class);
		String filepath = fileChooser(false, new ExtensionFilter("CEGUI File", "*.cegui", "*.CEGUI"));
		settings.setLastOpenedFile(filepath);
		String jsonString = loadStringFromFile(filepath);
		if (jsonString.isBlank()) {
			return;
		}
		JSONObject guiData = new JSONObject(jsonString);
		// TODO: display error on json exception?
//		savedData.put("dialogueView", dialogueViewModel.getGUIData());
//		savedData.put("dialogueData", dialogueDataViewModel.getGUIData());
		if (guiData != null) {
			JSONObject dialogueView = guiData.optJSONObject("dialogueView");
			if (dialogueView != null) {
				DialogueViewModel dialogueViewModel = easyDI.getInstance(DialogueViewModel.class);
				dialogueViewModel.setGUIData(dialogueView);
			}
			JSONObject dialogueViewData = guiData.optJSONObject("dialogueData");
			if (dialogueViewData != null) {
				DialogueDataViewModel dialogueViewModel = easyDI.getInstance(DialogueDataViewModel.class);
				dialogueViewModel.setGUIData(dialogueViewData);
			}
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

	public static SaveStateEnum saveDataBeforeExit() {

		Alert saveBeforeExitConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"You have unsaved changes.\nWould you like to save them now?");
		saveBeforeExitConfirmation.setHeaderText("Save unsaved changes");
		saveBeforeExitConfirmation.setTitle("Unsaved changes");
		saveBeforeExitConfirmation.initModality(Modality.APPLICATION_MODAL);
		saveBeforeExitConfirmation.initOwner(App.mainStage);
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");
		ButtonType cancelButton = new ButtonType("Cancel");
		saveBeforeExitConfirmation.getButtonTypes().clear();
		saveBeforeExitConfirmation.getButtonTypes().addAll(yesButton, noButton, cancelButton);
		Optional<ButtonType> response = saveBeforeExitConfirmation.showAndWait();
		if (response.isPresent()) {
			if (response.get() == yesButton) {
				return SaveStateEnum.YES;
			}
			if (response.get() == noButton) {
				return SaveStateEnum.NO;
			}
		}
		return SaveStateEnum.CANCEL;
	}

	public static String fileChooser(boolean save, ExtensionFilter extensions) {
		Settings settings = App.easyDI.getInstance(Settings.class);
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(extensions);
		File file = null;
		File folder = new File(settings.getLastFileChooserPath());
		if (folder.isDirectory()) {
			fileChooser.setInitialDirectory(folder);
		}
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
		settings.setLastFileChooserPath(file.getParent());
		return file.getAbsolutePath();
	}

	public static void saveJSONStringToFile(String filepath, String data) {
		try (FileWriter fileWriter = new FileWriter(filepath)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException e) {
			// TODO alert that there was an error?
		}

	}
}
