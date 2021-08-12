package de.dai_labor.conversation_engine_gui.view.main;

import java.util.HashMap;
import java.util.Map;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataView;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataViewModel;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueView;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.dai_labor.conversation_engine_gui.view.settings.SettingsView;
import de.dai_labor.conversation_engine_gui.view.settings.SettingsViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class MainViewModel implements ViewModel {

	private DialogueViewModel dialogueViewModel;
	private Map<String, ViewTuple> views;
	private final String defaultViewID = "dialogue";
	private SimpleObjectProperty<Node> currentView;

	public MainViewModel(DialogueViewModel dialogueViewModel) {
		this.dialogueViewModel = dialogueViewModel;
		this.views = new HashMap<>();
		ViewTuple<DialogueView, DialogueViewModel> dialogueViewTuple = FluentViewLoader.fxmlView(DialogueView.class)
				.load();
		ViewTuple<DialogueDataView, DialogueDataViewModel> dialogueDataViewTuple = FluentViewLoader
				.fxmlView(DialogueDataView.class).load();
		ViewTuple<SettingsView, SettingsViewModel> settingsViewTuple = FluentViewLoader.fxmlView(SettingsView.class)
				.load();
		this.views.put(defaultViewID, dialogueViewTuple);
		this.views.put("dialogueData", dialogueDataViewTuple);
		this.views.put("settings", settingsViewTuple);
		this.currentView = new SimpleObjectProperty<>();
		this.currentView.set(this.views.get(defaultViewID).getView());
	}

	public void newFile(ActionEvent event) {
		Util.saveGUIDataToFile(true, false, false);
		dialogueViewModel.resetData();
		App.easyDI.getInstance(Settings.class).setLastOpenedFilePath("");
	}

	public void openFile(ActionEvent event) {
		Util.saveGUIDataToFile(true, false, false);
		Util.loadGUIDataFromFile();
	}

	public void saveFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, false, true);
	}

	public void saveAsFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, true, true);
	}

	public void exportFile(ActionEvent event) {

	}

	public void closeApplication(ActionEvent event) {
		Util.saveGUIDataToFile(true, false, false);
		Platform.exit();
	}

	public SimpleObjectProperty<Node> getViewBinding() {
		return this.currentView;
	}

	public void setView(ActionEvent event) {
		Button source = (Button) event.getSource();
		ViewTuple view = this.views.get((String) source.getUserData());
		if (view != null) {
			this.currentView.set(view.getView());
		}
	}
}
