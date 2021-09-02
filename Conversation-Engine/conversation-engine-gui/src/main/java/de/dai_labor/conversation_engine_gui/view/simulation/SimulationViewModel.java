package de.dai_labor.conversation_engine_gui.view.simulation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.models.LanguageEnum;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class SimulationViewModel implements ViewModel {
	private DialoguePane dialogueView;

	public SimulationViewModel(DialogueViewModel dialogueViewModel,
			SimulationSettingsViewModel simulationSettingsViewModel) {
		this.dialogueView = dialogueViewModel.getView();
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(true);
		Constructor<?> nlpConstrutor;
		INLPComponent nlpComponent;
		try {
			nlpConstrutor = simulationSettingsViewModel.getNLPComponent().getSelectedClass().getConstructor();
			nlpComponent = (INLPComponent) nlpConstrutor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Util.showError("Could not initialize the NLP Component", e.getLocalizedMessage());
			this.closeRequest();
			return;
		}
		String initializedContextObject = "{}";
		String selectedLanguage = simulationSettingsViewModel.getSelectedLanguageProperty().get();
		Locale language = LanguageEnum.valueOf(selectedLanguage).getLocale();
		ConversationEngine ce = new ConversationEngine(nlpComponent, 1000, initializedContextObject, language);
		ce.userInput("Hi");
	}

	public Node getDialoguePane() {
		return this.dialogueView;
	}

	public void unload() {
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(false);
	}

	private void closeRequest() {
		Platform.runLater(() -> this.publish("SimulationViewCloseRequest"));
	}

}
