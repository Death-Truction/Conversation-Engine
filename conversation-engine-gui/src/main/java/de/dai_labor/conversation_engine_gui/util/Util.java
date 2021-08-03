package de.dai_labor.conversation_engine_gui.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Util {
	private Util() {
	}

	public static boolean saveDataBeforeExit(Stage owner) {

		Alert saveBeforeExitConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"You have unsaved changes.\nWould you like to save them now?");
		saveBeforeExitConfirmation.setHeaderText("Save unsaved changes");
		saveBeforeExitConfirmation.setTitle("Unsaved changes");
		saveBeforeExitConfirmation.initModality(Modality.APPLICATION_MODAL);
		saveBeforeExitConfirmation.initOwner(owner);
		((Button) saveBeforeExitConfirmation.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
		((Button) saveBeforeExitConfirmation.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
		Optional<ButtonType> closeResponse = saveBeforeExitConfirmation.showAndWait();
		if (closeResponse.isPresent()) {
			return ButtonType.OK.equals(closeResponse.get());
		}
		return false;
	}

	public static String pickSaveFilepath(String title, Stage owner) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CE-GUI File", "*.cegui"));
		File file = fileChooser.showSaveDialog(owner);
		return file.getAbsolutePath();
	}

	public static String pickOpenFilepath(String title, Stage owner) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CE-GUI File", "*.cegui"));
		File file = fileChooser.showOpenDialog(owner);
		return file.getAbsolutePath();
	}

	public static void saveJSONStringToFile(String filepath, String data) {
		try (FileWriter fileWriter = new FileWriter(filepath)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
