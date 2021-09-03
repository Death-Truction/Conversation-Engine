package de.dai_labor.conversation_engine_gui.view.main;

import java.util.HashMap;
import java.util.Map;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.SaveStateEnum;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataView;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataViewModel;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueView;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.dai_labor.conversation_engine_gui.view.settings.SettingsView;
import de.dai_labor.conversation_engine_gui.view.settings.SettingsViewModel;
import de.dai_labor.conversation_engine_gui.view.simulation.SimulationSettingsView;
import de.dai_labor.conversation_engine_gui.view.simulation.SimulationSettingsViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainViewModel implements ViewModel {

	private final Map<String, ViewTuple> views;
	private static final String DEFAULT_VIEW_ID = "dialogue";
	private final SimpleObjectProperty<Node> currentView;
	private ViewTuple currentViewTuple;

	public MainViewModel() {
		this.views = new HashMap<>();
		final ViewTuple<DialogueView, DialogueViewModel> dialogueViewTuple = FluentViewLoader
				.fxmlView(DialogueView.class).load();
		final ViewTuple<DialogueDataView, DialogueDataViewModel> dialogueDataViewTuple = FluentViewLoader
				.fxmlView(DialogueDataView.class).load();
		final ViewTuple<SettingsView, SettingsViewModel> settingsViewTuple = FluentViewLoader
				.fxmlView(SettingsView.class).load();
		final ViewTuple<SimulationSettingsView, SimulationSettingsViewModel> simulationSettingsViewTuple = FluentViewLoader
				.fxmlView(SimulationSettingsView.class).load();
		this.views.put(DEFAULT_VIEW_ID, dialogueViewTuple);
		this.views.put("dialogueData", dialogueDataViewTuple);
		this.views.put("settings", settingsViewTuple);
		this.views.put("simulationSettings", simulationSettingsViewTuple);
		this.currentView = new SimpleObjectProperty<>();
		this.currentView.set(this.views.get(DEFAULT_VIEW_ID).getView());
		this.currentViewTuple = this.views.get(DEFAULT_VIEW_ID);
	}

	public void newFile(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false, false) != SaveStateEnum.CANCEL) {
			App.easyDI.getInstance(Settings.class).setLastOpenedFile("");
			Util.resetGUIData();
		}
	}

	public void openFile(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false, false) != SaveStateEnum.CANCEL) {
			Util.loadGUIDataFromFile();
		}
	}

	public void saveFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, false, true);
	}

	public void saveAsFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, true, true);
	}

	public void exportFile(ActionEvent event) {
		String exportData = Util.getSkillStateMachineData().toString();
		String filePath = Util.fileChooser(false, new ExtensionFilter("JSON-Files", "*.json"));
	}

	public void closeApplication(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false, false) != SaveStateEnum.CANCEL) {
			Platform.exit();
		}
	}

	public SimpleObjectProperty<Node> getViewBinding() {
		return this.currentView;
	}

	public void setView(ActionEvent event) {
		final Button source = (Button) event.getSource();
		final ViewTuple view = this.views.get(source.getUserData());
		if (view != null) {
			this.currentViewTuple.getViewModel().publish("unload");
			this.currentViewTuple = view;
			this.currentView.set(view.getView());
		}
	}
}
