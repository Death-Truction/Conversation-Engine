package de.dai_labor.conversation_engine_gui.view.simulation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueDataViewModel;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulationStage {
	private final Stage simulationStage = new Stage();
	private ViewTuple<SimulationView, SimulationViewModel> viewTuple;
	private DialogueViewModel dialogueViewModel;
	private DialogueDataViewModel dialogueDataViewModel;
	private SimulationSettingsViewModel simulationSettingsViewModel;
	private List<String> errorMessages = new ArrayList<>();

	public SimulationStage(DialogueViewModel dialogueViewModel, DialogueDataViewModel dialogueDataViewModel,
			SimulationSettingsViewModel simulationSettingsViewModel) {
		this.dialogueViewModel = dialogueViewModel;
		this.dialogueDataViewModel = dialogueDataViewModel;
		this.simulationSettingsViewModel = simulationSettingsViewModel;
		this.start();
		this.dialogueViewModel.getViewProperty().set(null);
		this.simulationStage.setOnCloseRequest(event -> {
			this.viewTuple.getViewModel().unload();
			dialogueViewModel.getViewProperty().set(dialogueViewModel.getView());
			App.mainStage.show();
		});
		App.mainStage.hide();
	}

	private void start() {
		this.validSimulationData();
		if (!this.errorMessages.isEmpty()) {
			Util.showError("Invalid Data", String.join("\n", this.errorMessages));
			return;
		}
		this.showStage();

	}

	private void validSimulationData() {
		Map<Integer, State> allStates = this.dialogueViewModel.getStates();
		List<Transition> allTransitions = this.dialogueViewModel.getTransitions();
		State startState = this.dialogueViewModel.getStartState();
		State endState = this.dialogueViewModel.getEndState();
		if (allStates.size() < 2) {
			this.addErrorMessage("You need at least two states");
		}
		if (allTransitions.isEmpty()) {
			this.addErrorMessage("You need at least one transition");
		}
		if (startState == null) {
			this.addErrorMessage("Missing start state");
		}
		if (endState == null) {
			this.addErrorMessage("Missing end state");
		}
		List<String> duplicatedStates = this.getDuplicateStateNames(allStates);
		if (!duplicatedStates.isEmpty()) {
			this.addErrorMessage("You must not have duplicated state names: " + String.join(",", duplicatedStates));
		}
		// check whether the start state is the source or the end state the target of a
		// transition
		if (startState != null && endState != null) {
			for (Transition transition : this.dialogueViewModel.getTransitions()) {
				if (transition.getTarget() == this.dialogueViewModel.getStartState()) {
					this.addErrorMessage("The start state must not be a transition target : {0}",
							transition.toString());
				}
				if (transition.getSource() == this.dialogueViewModel.getEndState()) {
					this.addErrorMessage("The end state must not be a transition source : {0}", transition.toString());
				}
			}
		}
		String nlpComponentPath = this.simulationSettingsViewModel.getSelectedNLPComponentProperty().get();
		if (nlpComponentPath == null || nlpComponentPath.isBlank()) {
			this.addErrorMessage("You must select a NLP-Component");
		} else {
			// TODO: load nlpComponent dynamically and check for errors
		}
		String conversationInputs = this.simulationSettingsViewModel.getConversationInputProperty().get();
		if (conversationInputs == null || conversationInputs.isBlank()) {
			this.addErrorMessage("You must enter at least one conversation input");
		} else {
			// TODO: load skill dynamically and check for errors
		}
	}

	private void addErrorMessage(String message, Object... args) {
		this.errorMessages.add("- " + MessageFormat.format(message, args));
	}

	private void showStage() {
		this.viewTuple = FluentViewLoader.fxmlView(SimulationView.class).load();
		this.viewTuple.getViewModel().subscribe("SimulationViewCloseRequest", (t, observe) -> {
			App.mainStage.show();
			this.simulationStage.close();
		});
		this.simulationStage.getIcons().add(Util.getIcon());
		this.viewTuple.getView().getStylesheets().add(Util.getStyleSheetPath());
		this.simulationStage.minHeightProperty().set(480.0);
		this.simulationStage.minWidthProperty().set(640.0);
		this.simulationStage.setHeight(720);
		this.simulationStage.setWidth(1280);
		this.simulationStage.setScene(new Scene(this.viewTuple.getView()));
		this.simulationStage.show();
	}

	private List<String> getDuplicateStateNames(Map<Integer, State> states) {
		Set<String> allStateNames = new HashSet<>();
		List<String> duplicates = new ArrayList<>();
		for (State state : states.values()) {
			if (!allStateNames.add(state.getName())) {
				duplicates.add(state.getName());
			}
		}

		return duplicates;

	}
}
