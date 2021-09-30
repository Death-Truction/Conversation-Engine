package de.dai_labor.conversation_engine_gui.view.main;

import java.util.HashMap;
import java.util.Map;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
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
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * The ViewModel for the {@link MainView}
 *
 * @author Marcel Engelmann
 *
 */
public class MainViewModel implements ViewModel {

	private final Map<String, ViewTuple<?, ?>> views;
	private static final String DEFAULT_VIEW_ID = "dialogue";
	private final SimpleObjectProperty<Node> currentViewProperty;
	private ViewTuple<?, ?> currentViewTuple;

	/**
	 * Creates a new Instance of the {@link MainViewModel}
	 */
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
		this.currentViewProperty = new SimpleObjectProperty<>();
		this.currentViewProperty.set(this.views.get(DEFAULT_VIEW_ID).getView());
		this.currentViewTuple = this.views.get(DEFAULT_VIEW_ID);
	}

	/**
	 * Creates a new File and checks for unsaved changes
	 *
	 * @param event Ignored
	 */
	public void newFile(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false) != SaveStateEnum.CANCEL) {
			App.easyDI.getInstance(Settings.class).setLastOpenedFile("");
			Util.resetGUIData();
		}
	}

	/**
	 * Opens a new File and checks for unsaved changes
	 *
	 * @param event Ignored
	 */
	public void openFile(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false) != SaveStateEnum.CANCEL) {
			Util.loadGUIDataFromFile();
		}
	}

	/**
	 * Saves the GUI data to a file. If a file is currently opened, then it will
	 * save it to that file.
	 *
	 * @param event Ignored
	 */
	public void saveFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, false);
	}

	/**
	 * Saves the GUI data to a new file. Even if a file is currently opened.
	 *
	 * @param event Ignored
	 */
	public void saveAsFile(ActionEvent event) {
		Util.saveGUIDataToFile(false, true);
	}

	/**
	 * Exports the GUI data to a JSON file that is valid for importing the
	 * {@link ISkill} to the {@link ConversationEngine}
	 *
	 * @param event Ignored
	 */
	public void exportFile(ActionEvent event) {
		Util.exportData();
	}

	/**
	 * Request the application to close. If there are unsaved changes the user will
	 * be ask if the changes should be saved first. The closing operation will be
	 * canceled if the user cancels the saving process
	 *
	 * @param event Ignored
	 */
	public void closeApplication(ActionEvent event) {
		if (Util.saveGUIDataToFile(true, false) != SaveStateEnum.CANCEL) {
			Platform.exit();
		}
	}

	/**
	 * Gets the {@link Property} of the currently selected view
	 *
	 * @return the {@link Property} of the currently selected view
	 */
	public SimpleObjectProperty<Node> getCurrentViewProperty() {
		return this.currentViewProperty;
	}

	/**
	 * Sets the view to the event's user data
	 *
	 * @param event The event that holds the user data, which determines the next
	 *              view to show
	 */
	public void setView(ActionEvent event) {
		final Button source = (Button) event.getSource();
		final ViewTuple<?, ?> view = this.views.get(source.getUserData());
		if (view != null) {
			this.currentViewTuple.getViewModel().publish("unload");
			this.currentViewTuple = view;
			this.currentViewProperty.set(view.getView());
		}
	}
}
