package de.dai_labor.conversation_engine_gui.view.main;

import de.dai_labor.conversation_engine_gui.models.DialogueModelData;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainViewModel implements ViewModel {

	private DialogueModelData dialogueModelData;

	public MainViewModel(DialogueModelData dialogueModelData) {
		this.dialogueModelData = dialogueModelData;
	}

	public void newFile(ActionEvent event) {
		saveFileDialog();
		dialogueModelData.resetData();
	}

	public void openFile(ActionEvent event) {
		Platform.exit();
	}

	public void saveFile(ActionEvent event) {
		// TODO: if data was imported or saved to a specific file before (must be saved
		// in settings or so), then do not prompt for a file pick location
		saveFileDialog();
	}

	public void saveAsFile(ActionEvent event) {
		saveFileDialog();
	}

	public void exportFile(ActionEvent event) {
		Platform.exit();
	}

	public void closeApplication(ActionEvent event) {
		Platform.exit();
	}

	private void saveFileDialog() {
		if (this.dialogueModelData.hasChanged() && Util.saveDataBeforeExit(null)) {
			String filepath = Util.pickSaveFilepath("Save file to", null);
			Util.saveJSONStringToFile(filepath, this.dialogueModelData.getGUIData().toString());
		}
	}

	private void saveFileDialog(String filepath) {
		if (this.dialogueModelData.hasChanged() && Util.saveDataBeforeExit(null)) {
			Util.saveJSONStringToFile(filepath, this.dialogueModelData.getGUIData().toString());
		}
	}
}
