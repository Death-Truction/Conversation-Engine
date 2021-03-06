package de.dai_labor.dialog_modeling_tool.view.simulation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dai_labor.dialog_modeling_tool.App;
import de.dai_labor.dialog_modeling_tool.gui_components.State;
import de.dai_labor.dialog_modeling_tool.gui_components.Transition;
import de.dai_labor.dialog_modeling_tool.models.ClassWithJarFile;
import de.dai_labor.dialog_modeling_tool.util.Util;
import de.dai_labor.dialog_modeling_tool.view.dialog.DialogViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The SimulationStage handles the new window that is used to simulate the
 * skill's state machine
 *
 * @author Marcel Engelmann
 *
 */
public class SimulationStage {
	private final Stage windowStage = new Stage();
	private ViewTuple<SimulationView, SimulationViewModel> viewTuple;
	private DialogViewModel dialogViewModel;
	private SimulationSettingsViewModel simulationSettingsViewModel;
	private List<String> errorMessages = new ArrayList<>();

	/**
	 * Create a new instance of the {@link SimulationStage}
	 *
	 * @param dialogViewModel             The instance of the
	 *                                    {@link DialogViewModel}
	 * @param simulationSettingsViewModel The instance of the
	 *                                    {@link SimulationSettingsViewModel}
	 */
	public SimulationStage(DialogViewModel dialogViewModel, SimulationSettingsViewModel simulationSettingsViewModel) {
		this.dialogViewModel = dialogViewModel;
		this.simulationSettingsViewModel = simulationSettingsViewModel;
		this.dialogViewModel.getViewProperty().set(null);
		this.start();
		this.windowStage.setOnCloseRequest(event -> {
			App.mainStage.show();
			this.viewTuple.getViewModel().unload();
			dialogViewModel.getViewProperty().set(dialogViewModel.getView());
		});
	}

	/**
	 * Validates the data and starts the simulation if the data is valid
	 */
	private void start() {
		this.validateSimulationData();
		if (!this.errorMessages.isEmpty()) {
			Util.showError("Invalid Data", String.join("\n", this.errorMessages));
			return;
		}
		this.showStage();

	}

	/**
	 * Validates the required simulation data
	 */
	private void validateSimulationData() {
		Map<Integer, State> allStates = this.dialogViewModel.getStates();
		List<Transition> allTransitions = this.dialogViewModel.getTransitions();
		State startState = this.dialogViewModel.getStartState();
		State endState = this.dialogViewModel.getEndState();
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
		if (startState != null || endState != null) {
			this.validateTransitions();
		}
		ClassWithJarFile nlpComponent = this.simulationSettingsViewModel.getNLPComponent();
		if (nlpComponent == null) {
			this.addErrorMessage("You must select a NLP-Component");
		}

		ClassWithJarFile skill = this.simulationSettingsViewModel.getSkill();
		if (skill == null) {
			this.addErrorMessage("You must select a Skill");
		}
		String conversationInputs = this.simulationSettingsViewModel.getConversationInputProperty().get();
		if (conversationInputs == null || conversationInputs.isBlank()) {
			this.addErrorMessage("You must enter at least one conversation input");
		}
	}

	/**
	 * Validates that the start state is not the source or the end state not the
	 * target of a transition
	 */
	private void validateTransitions() {
		for (Transition transition : this.dialogViewModel.getTransitions()) {
			if (transition.getTarget() == this.dialogViewModel.getStartState()) {
				this.addErrorMessage("The start state must not be a transition target : {0}", transition.toString());
			}
			if (transition.getSource() == this.dialogViewModel.getEndState()) {
				this.addErrorMessage("The end state must not be a transition source : {0}", transition.toString());
			}
		}
	}

	/**
	 * Adds an error message to the list of {@link #errorMessages error messages}
	 *
	 * @param message The message in a valid {@link MessageFormat} format
	 * @param args    The arguments for the {@link MessageFormat}
	 */
	private void addErrorMessage(String message, Object... args) {
		this.errorMessages.add("- " + MessageFormat.format(message, args));
	}

	/**
	 * Sets up the simulation window and shows it
	 */
	private void showStage() {
		this.viewTuple = FluentViewLoader.fxmlView(SimulationView.class).load();
		this.viewTuple.getViewModel().subscribe("SimulationViewCloseRequest", (t, observe) -> {
			App.mainStage.show();
			this.windowStage.close();
		});
		this.windowStage.getIcons().add(Util.getIcon());
		this.windowStage.setTitle("Conversation Engine - Dialog Modeling Tool");
		this.viewTuple.getView().getStylesheets().add(Util.getStyleSheetPath());
		this.windowStage.minHeightProperty().set(480.0);
		this.windowStage.minWidthProperty().set(640.0);
		this.windowStage.setHeight(720);
		this.windowStage.setWidth(1280);
		this.windowStage.setScene(new Scene(this.viewTuple.getView()));
		this.windowStage.show();
		App.mainStage.hide();
	}

	/**
	 * Gets a unique {@link List} of all duplicated state names
	 *
	 * @param states The states to check for duplicates
	 * @return all duplicated state names
	 */
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
