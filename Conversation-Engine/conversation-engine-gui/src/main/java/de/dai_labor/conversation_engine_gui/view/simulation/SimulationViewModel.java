package de.dai_labor.conversation_engine_gui.view.simulation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.models.LanguageEnum;
import de.dai_labor.conversation_engine_gui.models.SimulationStep;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SimulationViewModel implements ViewModel {
	private SimpleBooleanProperty simulationIsRunningProperty = new SimpleBooleanProperty(false);
	private SimpleStringProperty simulationSpeedProperty = new SimpleStringProperty();
	private SimpleStringProperty playPauseButtonTextProperty = new SimpleStringProperty("Play");
	private DialoguePane dialogueView;
	private DialogueViewModel dialogueViewModel;
	private List<SimulationStep> simulationSteps = new ArrayList<>();
	private ObservableList<Node> conversationMessages;
	private int simulationStepIndex = 0;
	private double simulationSpeed;
	private Timer timer = new Timer();
	private static final double DEFAULT_SIMULATION_STEP_TIME = 2000.0;

	public SimulationViewModel(DialogueViewModel dialogueViewModel,
			SimulationSettingsViewModel simulationSettingsViewModel) {
		this.dialogueViewModel = dialogueViewModel;
		this.dialogueView = dialogueViewModel.getView();
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(true);
		Class<?> nlpClass = simulationSettingsViewModel.getNLPComponent().getSelectedClass();
		Class<?> skillClass = simulationSettingsViewModel.getSkill().getSelectedClass();
		INLPComponent nlpComponent;
		ISkill skill;
		try {
			Constructor<?> nlpConstrutor;
			nlpConstrutor = nlpClass.getConstructor();
			nlpComponent = (INLPComponent) nlpConstrutor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Util.showError("Could not initialize the NLP Component", e.getLocalizedMessage());
			this.closeRequest();
			return;
		}
		try {
			Constructor<?> skillConstrutor;
			skillConstrutor = skillClass.getConstructor();
			skill = (ISkill) skillConstrutor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Util.showError("Could not initialize the Skill", e.getLocalizedMessage());
			this.closeRequest();
			return;
		}
		this.initializeConversationEngine(simulationSettingsViewModel, nlpComponent, skill);

	}

	public SimpleBooleanProperty getSimulationIsRunningProperty() {
		return this.simulationIsRunningProperty;
	}

	public SimpleStringProperty getSimulationSpeedProperty() {
		return this.simulationSpeedProperty;
	}

	public SimpleStringProperty getPlayPauseButtonTextProperty() {
		return this.playPauseButtonTextProperty;
	}

	public void toStart() {
		while (this.simulationStepIndex > 0) {
			this.stepBackwards();
		}
	}

	public void stepBackwards() {
		if (this.simulationStepIndex == 0) {
			return;
		}
		this.simulationStepIndex--;
		SimulationStep step = this.simulationSteps.get(this.simulationStepIndex);
		step.getTarget().deselect();
		step.getSource().select();
		step.getTransition().deselect();
		if (this.simulationStepIndex > 0) {
			this.simulationSteps.get(this.simulationStepIndex - 1).getTransition().select();
		}
		int numOfRemovingMessages = step.getOutput().size() + 1; // +1 for input message
		int newEndIndex = this.conversationMessages.size() - numOfRemovingMessages;
		this.conversationMessages.remove(newEndIndex, this.conversationMessages.size());
	}

	public void playPause() {
		boolean isPlaying = this.simulationIsRunningProperty.get();
		this.simulationIsRunningProperty.set(!isPlaying);
		if (isPlaying) {
			this.playPauseButtonTextProperty.set("Play");
		} else {
			this.playPauseButtonTextProperty.set("Pause");
			try {
				this.simulationSpeed = Double.parseDouble(this.simulationSpeedProperty.get());
			} catch (NumberFormatException | NullPointerException ex) {
				this.simulationSpeed = 1.0;
				this.simulationSpeedProperty.set("1.0");
			}
			this.startAutoSimulation();
		}
	}

	public void stepForward() {
		if (this.simulationStepIndex >= this.simulationSteps.size()) {
			return;
		}
		SimulationStep step = this.simulationSteps.get(this.simulationStepIndex);
		this.addConversationMessages(step);
		step.getSource().deselect();
		step.getTarget().select();
		step.getTransition().select();
		if (this.simulationStepIndex > 0) {
			this.simulationSteps.get(this.simulationStepIndex - 1).getTransition().deselect();
		}
		this.simulationStepIndex++;
	}

	public void toEnd() {
		while (this.simulationStepIndex < this.simulationSteps.size()) {
			this.stepForward();
		}
	}

	public void setConversationVBoxChildren(ObservableList<Node> children) {
		this.conversationMessages = children;

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

	private void initializeConversationEngine(SimulationSettingsViewModel simulationSettingsViewModel,
			INLPComponent nlpComponent, ISkill skill) {
		String initializedContextObject = "{}";
		String selectedLanguage = simulationSettingsViewModel.getSelectedLanguageProperty().get();
		Locale language = LanguageEnum.valueOf(selectedLanguage.toUpperCase()).getLocale();
		ConversationEngine ce = new ConversationEngine(nlpComponent, 1000, initializedContextObject, language);
		String skillJsonString = Util.getSkillStateMachineData().toString();
		String[] inputs = simulationSettingsViewModel.getConversationInputProperty().get().split("\n");
		ce.addSkill(skill, skillJsonString);
		this.dialogueViewModel.getStartState().select();
		this.simulateConversationEngineProcess(ce, inputs);
	}

	private void simulateConversationEngineProcess(ConversationEngine ce, String[] inputs) {
		for (int i = 0; i < inputs.length; i++) {
			String input = inputs[i];
			State source;
			if (i == 0) {
				source = this.dialogueViewModel.getStartState();
			} else {
				source = this.dialogueViewModel.getStateByName(ce.getState());
			}
			List<String> outputs = ce.userInput(input);
			State target;
			if (ce.getState().equals("defaultState")) {
				target = this.dialogueViewModel.getEndState();
			} else {
				target = this.dialogueViewModel.getStateByName(ce.getState());
			}
			Transition transition = this.dialogueViewModel.getTransition(source, target);
			this.simulationSteps.add(new SimulationStep(source, target, transition, input, outputs));
		}
	}

	private void startAutoSimulation() {
		this.stepForward();
		this.timerTaskStepForward();
	}

	private void timerTaskStepForward() {
		if (!this.simulationIsRunningProperty.get()) {
			this.timer.cancel();
			return;
		}
		if (this.simulationStepIndex == this.simulationSteps.size()) {
			this.playPause();
			return;
		}

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					SimulationViewModel.this.stepForward();
					SimulationViewModel.this.timerTaskStepForward();
				});

			}
		};
		this.timer.cancel();
		this.timer = new Timer();
		double stepTime = DEFAULT_SIMULATION_STEP_TIME / this.simulationSpeed;
		this.timer.schedule(timerTask, (long) stepTime);
	}

	private void addConversationMessageToView(Pos alignment, String message) {
		Label messageLabel = new Label(message);
		if (alignment == Pos.CENTER_LEFT) {
			messageLabel.setStyle("-fx-background-color: Green; -fx-text-fill: WHITE");
		} else {
			messageLabel.setStyle("-fx-background-color: PURPLE; -fx-text-fill: WHITE");
		}
		HBox messageContainer = new HBox(messageLabel);
		messageContainer.setAlignment(alignment);
		this.conversationMessages.add(messageContainer);
	}

	private void addConversationMessages(SimulationStep step) {
		this.addConversationMessageToView(Pos.CENTER_RIGHT, step.getInput());
		for (String message : step.getOutput()) {
			this.addConversationMessageToView(Pos.CENTER_LEFT, message);
		}
	}

}
