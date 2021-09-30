package de.dai_labor.conversation_engine_gui.view.simulation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.models.DebugColorEnum;
import de.dai_labor.conversation_engine_gui.models.LanguageEnum;
import de.dai_labor.conversation_engine_gui.models.MemoryLogger;
import de.dai_labor.conversation_engine_gui.models.SimulationStep;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * The ViewModel for the {@link SimulationView}
 *
 * @author Marcel Engelmann
 *
 */
public class SimulationViewModel implements ViewModel {
	private SimpleBooleanProperty simulationIsRunningProperty = new SimpleBooleanProperty(false);
	private SimpleStringProperty simulationSpeedProperty = new SimpleStringProperty();
	private SimpleStringProperty playPauseButtonTextProperty = new SimpleStringProperty("Play");
	private SimpleStringProperty loadingProgressLabelProperty = new SimpleStringProperty("0.0");
	private SimpleDoubleProperty loadingProgressValueProperty = new SimpleDoubleProperty(0.0);
	private DialoguePane dialogueView;
	private DialogueViewModel dialogueViewModel;
	private List<SimulationStep> simulationSteps = new ArrayList<>();
	private ObservableList<Node> conversationMessages;
	private ObservableList<Node> loggingMessages;
	private int simulationStepIndex = 0;
	private double simulationSpeed;
	private Timer timer = new Timer();
	private boolean showLoggingVBox = true;
	private static final double DEFAULT_SIMULATION_STEP_TIME = 2000.0;

	/**
	 * Creates a new instance of the {@link SimulationViewModel}
	 *
	 * @param dialogueViewModel           The instance of the
	 *                                    {@link DialogueViewModel}
	 * @param simulationSettingsViewModel The instance of the
	 *                                    {@link SimulationSettingsViewModel}
	 */
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

	/**
	 * Gets the {@link Property} of the simulation's running status
	 *
	 * @return the {@link Property} of the simulation's running status
	 */
	public SimpleBooleanProperty getSimulationIsRunningProperty() {
		return this.simulationIsRunningProperty;
	}

	/**
	 * Gets the {@link Property} of the simulation speed
	 *
	 * @return the {@link Property} of the simulation speed
	 */
	public SimpleStringProperty getSimulationSpeedProperty() {
		return this.simulationSpeedProperty;
	}

	/**
	 * Gets the {@link Property} of the play/pause button text
	 *
	 * @return the {@link Property} of the play/pause button text
	 */
	public SimpleStringProperty getLoadingProgressLabelProperty() {
		return this.loadingProgressLabelProperty;
	}

	/**
	 * Gets the {@link Property} of the play/pause button text
	 *
	 * @return the {@link Property} of the play/pause button text
	 */
	public SimpleDoubleProperty getLoadingProgressValueProperty() {
		return this.loadingProgressValueProperty;
	}

	/**
	 * Gets the {@link Property} of the play/pause button text
	 *
	 * @return the {@link Property} of the play/pause button text
	 */
	public SimpleStringProperty getPlayPauseButtonTextProperty() {
		return this.playPauseButtonTextProperty;
	}

	/**
	 * Gets the {@link Property} that determines whether the logging VBox should be
	 * shown or not
	 *
	 * @return the {@link Property} of the showLoggingVBox
	 */
	public boolean showLoggingVBox() {
		return this.showLoggingVBox;
	}

	/**
	 * Jumps back to the beginning of the simulation
	 */
	public void toStart() {
		while (this.simulationStepIndex > 0) {
			this.stepBackwards();
		}
	}

	/**
	 * Goes one step back of the simulation
	 */
	public void stepBackwards() {
		if (this.simulationStepIndex == 0) {
			return;
		}
		this.simulationStepIndex--;
		SimulationStep step = this.simulationSteps.get(this.simulationStepIndex);
		if (step.getSource() != null && step.getTarget() != null && step.getTransition() != null) {
			step.getTarget().deselect();
			step.getSource().select();
			step.getTransition().deselect();
		}
		if (this.simulationStepIndex > 0) {
			this.simulationSteps.get(this.simulationStepIndex - 1).getTransition().select();
		}
		this.removeConversationMessages(step);
		this.removeLoggingMessages(step);

	}

	/**
	 * Starts the automatic simulation. The default delay between each step is 2
	 * seconds which can be modified by the speed parameter
	 */
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

	/**
	 * Goes one step forward in the simulation
	 */
	public void stepForward() {
		if (this.simulationStepIndex >= this.simulationSteps.size()) {
			return;
		}
		SimulationStep step = this.simulationSteps.get(this.simulationStepIndex);
		this.addConversationMessages(step);
		this.addLoggingMessages(step);
		if (this.simulationStepIndex > 0) {
			this.simulationSteps.get(this.simulationStepIndex - 1).getTransition().deselect();
		}
		if (step.getSource() != null && step.getTarget() != null && step.getTransition() != null) {
			step.getSource().deselect();
			step.getTarget().select();
			step.getTransition().select();
		}
		this.simulationStepIndex++;
	}

	/**
	 * Jumps to the end of the simulation
	 */
	public void toEnd() {
		while (this.simulationStepIndex < this.simulationSteps.size()) {
			this.stepForward();
		}
	}

	/**
	 * Sets the conversation messages {@link ObservableList}
	 *
	 * @param children The {@link ObservableList} to set the conversation messages
	 *                 to
	 */
	public void setConversationVBoxChildren(ObservableList<Node> children) {
		this.conversationMessages = children;

	}

	/**
	 * Sets the logging messages {@link ObservableList}
	 *
	 * @param children The {@link ObservableList} to set the logging messages to
	 */
	public void setLoggingVBoxChildren(ObservableList<Node> children) {
		this.loggingMessages = children;

	}

	/**
	 * Gets the Node of the {@link DialoguePane} that holds all the skill's state
	 * machine visual data
	 *
	 * @return the Node of the {@link DialoguePane}
	 */
	public Node getDialoguePane() {
		return this.dialogueView;
	}

	/**
	 * Unloads the SimulationView data
	 */
	public void unload() {
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(false);
		this.dialogueViewModel.getStates().forEach((index, state) -> state.deselect());
		this.dialogueViewModel.getTransitions().stream().forEach(Transition::deselect);
	}

	/**
	 * Notifies the owner of the window that clsoing the window has been requested
	 */
	private void closeRequest() {
		Platform.runLater(() -> this.publish("SimulationViewCloseRequest"));
	}

	/**
	 * Initializes the {@link ConversationEngine} to create all the simulation steps
	 *
	 * @param simulationSettingsViewModel The instance of the
	 *                                    {@link SimulationSettingsViewModel}
	 * @param nlpComponent                The {@link INLPComponent} used by the
	 *                                    {@link ConversationEngine}
	 * @param skill                       The {@link ISkill} used by the
	 *                                    {@link ConversationEngine}
	 */
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
		new Thread() {
			@Override
			public void run() {
				SimulationViewModel.this.simulateConversationEngineProcess(ce, inputs,
						simulationSettingsViewModel.getSelectedLoggingLevelProperty().get());
			}
		}.start();
	}

	/**
	 * Creates the {@link SimulationStep SimulationSteps} for the simulation
	 *
	 * @param conversationEngine The instance of the {@link ConversationEngine}
	 * @param inputs             The user inputs to process
	 */
	private void simulateConversationEngineProcess(ConversationEngine conversationEngine, String[] inputs,
			String loggingLevelValue) {
		Logger logger = (Logger) LoggerFactory.getLogger("DeveloperLogger");
		MemoryLogger logs = new MemoryLogger();
		logs.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		Level loggingLevel = Level.valueOf(loggingLevelValue);
		logger.setLevel(loggingLevel);
		if (loggingLevel == Level.OFF) {
			this.showLoggingVBox = false;
		}
		logger.addAppender(logs);
		logs.start();
		int maxSteps = inputs.length;
		for (int i = 0; i < inputs.length; i++) {
			String input = inputs[i];
			State source;
			if (i == 0) {
				source = this.dialogueViewModel.getStartState();
			} else {
				source = this.dialogueViewModel.getStateByName(conversationEngine.getState());
			}
			List<String> outputs = conversationEngine.userInput(input);
			State target;
			if (conversationEngine.getState().equals("defaultState")) {
				target = this.dialogueViewModel.getEndState();
			} else {
				target = this.dialogueViewModel.getStateByName(conversationEngine.getState());
			}
			Transition transition = this.dialogueViewModel.getTransition(source, target);
			this.simulationSteps
					.add(new SimulationStep(source, target, transition, input, outputs, logs.getMessagesCopy()));
			// if any error occurred within the CE -> stop
			if (logs.contains(Level.ERROR)) {
				break;
			}
			logs.reset();
			int step = i + 1;
			Platform.runLater(() -> {
				this.loadingProgressValueProperty.set((double) step / maxSteps);
				this.loadingProgressLabelProperty.set(step + "/" + maxSteps + " Steps");
			});
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

	private void removeConversationMessages(SimulationStep step) {
		int numOfRemovingMessages = step.getOutput().size() + 1; // +1 for input message
		int newEndIndex = this.conversationMessages.size() - numOfRemovingMessages;
		this.conversationMessages.remove(newEndIndex, this.conversationMessages.size());
	}

	private void addLoggingMessages(SimulationStep step) {
		for (ILoggingEvent event : step.getLoggingOutput()) {
			Label errorLevel = new Label(event.getLevel().levelStr);
			errorLevel.setTextFill(DebugColorEnum.valueOf(event.getLevel().levelStr).getColor());
			Label logMessage = new Label(" - " + event.getFormattedMessage());
			HBox messageBox = new HBox(errorLevel, logMessage);
			messageBox.setAlignment(Pos.CENTER_LEFT);
			this.loggingMessages.add(messageBox);
		}
	}

	private void removeLoggingMessages(SimulationStep step) {
		int numOfRemovingMessages = step.getLoggingOutput().size();
		int newEndIndex = this.loggingMessages.size() - numOfRemovingMessages;
		this.loggingMessages.remove(newEndIndex, this.loggingMessages.size());
	}

}
