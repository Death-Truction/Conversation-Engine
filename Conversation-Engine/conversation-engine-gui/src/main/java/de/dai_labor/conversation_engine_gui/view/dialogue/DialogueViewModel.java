package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.gui_components.Transition;
import de.dai_labor.conversation_engine_gui.interfaces.IStorableGuiData;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

/**
 * The ViewModel for the {@link DialogueView}
 *
 * @author Marcel Engelmann
 *
 */
@Singleton
public class DialogueViewModel implements ViewModel, IStorableGuiData {
	private static final int DIAGRAM_ELEMENT_LAYER_SIZE = 50000;
	private final DialoguePane dialoguePane;
	private final SimpleObjectProperty<Node> viewProperty = new SimpleObjectProperty<>();
	private final Pane dialogueElementsLayer = new Pane();
	private final SimpleStringProperty insertMode = new SimpleStringProperty("");
	private SimpleObjectProperty<State> selectedState = new SimpleObjectProperty<>();
	private SimpleObjectProperty<Transition> selectedTransition = new SimpleObjectProperty<>();
	private final Settings settings;
	private Toggle toggleButton;
	private final ObservableMap<Integer, State> states = FXCollections.observableHashMap();
	private final List<Transition> transitions = new ArrayList<>();
	private boolean dataHasChanged = false;
	private State startState;
	private State endState;
	private ToggleGroup toggleGroup = new ToggleGroup();

	/**
	 * Create a new {@link DialogueViewModel} instance
	 *
	 * @param settings The instance of the {@link Settings} class
	 */
	public DialogueViewModel(Settings settings) {
		this.settings = settings;
		this.dialoguePane = new DialoguePane(this.dialogueElementsLayer, this.insertMode, this.selectedState,
				this.selectedTransition, this::addState, this::addTransition, this::removeState,
				this::removeTransition);
		Platform.runLater(() -> {
			this.dialogueElementsLayer.setMinWidth(DIAGRAM_ELEMENT_LAYER_SIZE);
			this.dialogueElementsLayer.setMinHeight(DIAGRAM_ELEMENT_LAYER_SIZE);
			this.dialogueElementsLayer.relocate(DIAGRAM_ELEMENT_LAYER_SIZE / -2.0, DIAGRAM_ELEMENT_LAYER_SIZE / -2.0);
		});
		this.dialoguePane.toBack();
		this.subscribe("unload", (ignore, ignore2) -> this.dialoguePane.deselectAll());
		this.dialoguePane.toBack();
		this.viewProperty.set(this.dialoguePane);
		this.toggleGroup.selectedToggleProperty().addListener(event -> {
			this.setMode(this.toggleGroup.getSelectedToggle());
		});
	}

	/**
	 * Gets the {@link Property} of the current view Node
	 *
	 * @return the {@link Property} of the current view Node
	 */
	public SimpleObjectProperty<Node> getViewProperty() {
		return this.viewProperty;
	}

	/**
	 * Gets the {@link DialoguePane} that is displayed at the center of the view
	 *
	 * @return the {@link DialoguePane} that is displayed at the center of the view
	 */
	public DialoguePane getView() {
		return this.dialoguePane;
	}

	/**
	 * Centers the children of the {@link DialoguePane} in the current viewport
	 */
	public void centerElements() {
		this.dialoguePane.center();
	}

	/**
	 * Adds a new default {@link State}
	 *
	 * @param locationX The x-coordination of the new {@link State}
	 * @param locationY The y-coordination of the new {@link State}
	 */
	public void addState(double locationX, double locationY) {
		// center location to mousePointer
		locationX -= this.settings.getStateSizeProperty().get();
		locationY -= this.settings.getStateSizeProperty().get();
		int emptyStateId = this.getFirstEmptyStateId();
		this.addState(emptyStateId, "State" + (emptyStateId + 1), locationX, locationY, true);
	}

	/**
	 * Adds a new {@link State}
	 *
	 * @param id           The {@link State}'s id
	 * @param name         The name of the State
	 * @param locationX    The x-coordination of the new {@link State}
	 * @param locationY    The y-coordination of the new {@link State}
	 * @param focusRequest Whether the new {@link State} should get focused after
	 *                     being added
	 */
	public void addState(int id, String name, double locationX, double locationY, boolean focusRequest) {
		final State newState = new State(id, name, locationX, locationY, this.settings, this.selectedState,
				focusRequest);
		newState.getTextArea().textProperty().addListener((s, y, z) -> this.dataHasChanged = true);
		this.states.put(id, newState);
		this.dataHasChanged = true;
		this.dialogueElementsLayer.getChildren().add(newState);
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	/**
	 * Gets an {@link ObservableMap} of all available {@link State States}
	 *
	 * @return an {@link ObservableMap} of all available {@link State States}
	 */
	public ObservableMap<Integer, State> getStates() {
		return this.states;
	}

	/**
	 * Gets the currently selected start {@link State}
	 *
	 * @return the currently selected start {@link State}
	 */
	public State getStartState() {
		return this.startState;
	}

	/**
	 * Sets the start {@link State}. If multiple {@link State States} have the same
	 * name, the first found occurrence will be used.
	 *
	 * @param stateName The name of the {@link State}
	 */
	public void setStartState(String stateName) {
		State newStartState = this.getStateByName(stateName);
		if (this.startState != newStartState && newStartState != null) {
			if (this.startState != null) {
				this.startState.setNormalState();
			}
			newStartState.setStartState();
			this.startState = newStartState;
			if (this.startState == this.endState) {
				this.endState = null;
			}
		}

	}

	/**
	 * Gets the currently selected end {@link State}
	 *
	 * @return the currently selected end {@link State}
	 */
	public State getEndState() {
		return this.endState;
	}

	/**
	 * Sets the end {@link State}. If multiple {@link State States} have the same
	 * name, the first found occurrence will be used.
	 *
	 * @param stateName The name of the {@link State}
	 */
	public void setEndState(String stateName) {
		State newEndState = this.getStateByName(stateName);
		if (this.endState != newEndState && newEndState != null) {
			if (this.endState != null) {
				this.endState.setNormalState();
			}
			newEndState.setEndState();
			this.endState = newEndState;
			if (this.startState == this.endState) {
				this.startState = null;
			}
		}

	}

	/**
	 * Removes the given {@link State}
	 *
	 * @param state The {@link State} to be removed
	 */
	public void removeState(State state) {
		for (final Iterator<Transition> iterator = this.transitions.iterator(); iterator.hasNext();) {
			final Transition transition = iterator.next();
			if (transition.getSource().equals(state) || transition.getTarget().equals(state)) {
				iterator.remove();
				this.dialogueElementsLayer.getChildren().remove(transition);
			}
		}
		this.dialogueElementsLayer.getChildren().remove(this.states.remove(state.getStateId()));
		if (this.startState != null && this.startState.equals(state)) {
			this.startState = null;
		} else if (this.endState != null && this.endState.equals(state)) {
			this.endState = null;
		}
		this.dataHasChanged = true;
	}

	/**
	 * Removes the given {@link Transition}
	 *
	 * @param transition The {@link Transition} to be removed
	 */
	public void removeTransition(Transition transition) {
		this.dialogueElementsLayer.getChildren().remove(transition);
		this.transitions.remove(transition);
		this.dataHasChanged = true;
	}

	/**
	 * Adds a new default {@link Transition}
	 *
	 * @param source The source {@link State} of the {@link Transition}
	 * @param target The target {@link State} of the {@link Transition}
	 */
	public void addTransition(State source, State target) {
		this.addTransition(source, target, this.getFirstEmptyTransitionName(), true);
	}

	/**
	 * Adds a new transition {@link Transition}
	 *
	 * @param source       The source {@link State} of the {@link Transition}
	 * @param target       The target {@link State} of the {@link Transition}
	 * @param trigger      The name of the trigger
	 * @param focusRequest Whether the new {@link Transition} should get focused
	 *                     after being added
	 */
	public void addTransition(State source, State target, String trigger, boolean focusRequest) {
		if (!this.transitionExists(source, target)) {
			final Transition newTransition = new Transition(source, target, trigger, this.selectedTransition,
					focusRequest, this.settings);
			newTransition.getTriggerTextField().textProperty().addListener((s, y, z) -> this.dataHasChanged = true);
			this.transitions.add(newTransition);
			this.dataHasChanged = true;
			this.dialogueElementsLayer.getChildren().add(newTransition);
			newTransition.toBack();
		}
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(false);
		}
	}

	/**
	 * Checks whether a {@link Transition} between the two {@link State States}
	 * exists
	 *
	 * @param source The source {@link State}
	 * @param target The target {@link State}
	 * @return true if the {@link Transition} exists
	 */
	public boolean transitionExists(State source, State target) {
		return this.getTransition(source, target) != null;
	}

	/**
	 * Gets the {@link Transition} between the two {@link State States}
	 *
	 * @param source The source {@link State}
	 * @param target The target {@link State}
	 * @return The {@link Transition} between the two given {@link State States} or
	 *         null if no {@link Transition} exists
	 */
	public Transition getTransition(State source, State target) {
		for (final Transition transition : this.transitions) {
			if (transition.getSource().equals(source) && transition.getTarget().equals(target)
					|| transition.getSource().equals(target) && transition.getTarget().equals(source)) {
				return transition;
			}
		}
		return null;
	}

	/**
	 * Gets a {@link List} of all available {@link Transition Transitions}
	 *
	 * @return all available {@link Transition Transitions}
	 */
	public List<Transition> getTransitions() {
		return this.transitions;
	}

	@Override
	public void resetData() {
		this.dialogueElementsLayer.getChildren().clear();
		this.transitions.clear();
		this.states.clear();
		this.dataHasChanged = false;
	}

	@Override
	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	@Override
	public void setUnchanged() {
		this.dataHasChanged = false;
	}

	@Override
	public void setGUIData(JSONObject dialogueModelDataObject) {
		this.resetData();
		try {
			final JSONArray newStates = dialogueModelDataObject.getJSONArray("states");
			final JSONArray newTransitions = dialogueModelDataObject.getJSONArray("transitions");
			final double locationX = dialogueModelDataObject.optDouble("locationX", -5000);
			final double locationY = dialogueModelDataObject.optDouble("locationY", -5000);
			final double scale = dialogueModelDataObject.optDouble("scale", 1.0);
			this.setViewScale(1);
			this.dialogueElementsLayer.setTranslateX(0);
			this.dialogueElementsLayer.setTranslateY(0);
			this.dialogueElementsLayer.relocate(locationX, locationY);
			this.setViewScale(scale);
			this.setStatesGUIData(newStates);
			this.setTransitionsGUIData(newTransitions);
		} catch (final JSONException ex) {
			Util.showError("Error loading your file", ex.getLocalizedMessage());
		}

		this.setUnchanged();
	}

	@Override
	public JSONObject getGUIData() {
		final JSONObject data = new JSONObject();
		final double scale = this.dialogueElementsLayer.getScaleX();
		this.setViewScale(1);
		data.put("states", this.getStatesGUIData());
		data.put("transitions", this.getTransitionsGUIData());
		data.put("locationX", this.dialogueElementsLayer.getBoundsInParent().getMinX());
		data.put("locationY", this.dialogueElementsLayer.getBoundsInParent().getMinY());
		data.put("scale", scale);
		this.setViewScale(scale);
		return data;

	}

	/**
	 * Gets a {@link State} by a given name. If multiple {@link State States} with
	 * the same name exist, the first occurrence will be returned.
	 *
	 * @param stateName The name of the {@link State}
	 * @return The {@link State} with the given name or null if no such
	 *         {@link State} exists
	 */
	public State getStateByName(String stateName) {
		for (State state : this.states.values()) {
			if (state.getName().equals(stateName)) {
				return state;
			}
		}
		return null;
	}

	/**
	 * Deselects the currently selected {@link ToggleButton}. Has no effect, if no
	 * {@link ToggleButton} is currently selected
	 */
	public void deselectToggleButtons() {
		this.toggleGroup.getSelectedToggle().setSelected(false);
	}

	/**
	 * Adds a {@link Toggle} to the {@link ToggleGroup}
	 *
	 * @param toggle The {@link Toggle} to be added
	 */
	public void addToToggleGroup(Toggle toggle) {
		this.toggleGroup.getToggles().add(toggle);
	}

	/**
	 * Resets the current view by centering all the elements of the view
	 *
	 * @param e Ignored
	 */
	public void resetView(ActionEvent e) {
		this.centerElements();
	}

	/**
	 * Sets the insert mode for a {@link State} or {@link Transition}. The insert
	 * mode is determined by the currently selected {@link ToggleButton}
	 *
	 * @param toggleButton
	 */
	private void setMode(Toggle toggleButton) {
		this.toggleButton = toggleButton;
		if (toggleButton == null) {
			this.insertMode.set("");
		} else {
			this.insertMode.set(toggleButton.getUserData().toString());
		}
	}

	/**
	 * Gets the first unused key/id of the {@link #states} {@link Map}
	 *
	 * @return the first unused key/id of the {@link #states} {@link Map}
	 */
	private int getFirstEmptyStateId() {
		for (int i = 0; i < this.states.size(); i++) {
			if (!this.states.containsKey(i)) {
				return i;
			}
		}
		return this.states.size();
	}

	/**
	 * Gets the first unused default {@link Transition} name. The default
	 * {@link Transition} name has the pattern 'TransitionX' where X is the first
	 * index number that is currently not used
	 *
	 * @return the first unused default {@link Transition} name
	 */
	private String getFirstEmptyTransitionName() {
		for (int i = 1; i <= this.transitions.size(); i++) {
			String newTransitionName = "Transition" + i;
			if (this.transitions.stream().anyMatch(transition -> transition.getTrigger().equals(newTransitionName))) {
				continue;
			}
			return newTransitionName;
		}
		return "Transition" + (this.transitions.size() + 1);
	}

	/**
	 * Gets the GUI data for all {@link State States}
	 *
	 * @return A JSONArray that includes the required data for all available
	 *         {@link State States}
	 */
	private JSONArray getStatesGUIData() {
		final JSONArray statesData = new JSONArray();
		for (final Entry<Integer, State> stateEntry : this.states.entrySet()) {
			final State state = stateEntry.getValue();
			final int id = stateEntry.getKey();
			final JSONObject stateData = new JSONObject();
			stateData.put("locationX", state.getBoundsInParent().getMinX());
			stateData.put("locationY", state.getBoundsInParent().getMinY());
			stateData.put("name", state.getTextArea().getText());
			stateData.put("id", id);
			statesData.put(stateData);
		}
		return statesData;
	}

	/**
	 * Gets the GUI data for all {@link Transition Transitions}
	 *
	 * @return A JSONArray that includes the required data for all available
	 *         {@link Transition Transitions}
	 */
	private JSONArray getTransitionsGUIData() {
		final JSONArray transitionsData = new JSONArray();
		for (final Transition transition : this.transitions) {
			final JSONObject transitionData = new JSONObject();
			transitionData.put("source", transition.getSource().getStateId());
			transitionData.put("target", transition.getTarget().getStateId());
			transitionData.put("trigger", transition.getTrigger());
			transitionsData.put(transitionData);
		}
		return transitionsData;
	}

	/**
	 * Sets the GUI data for all {@link State States} A JSONArray that includes the
	 * required data for all available {@link State States}
	 *
	 * @param states A JSONArray that includes the required data for the
	 *               {@link State States}
	 * @throws JSONException on invalid formatting of the {@link State States} JSON
	 *                       objects
	 */
	private void setStatesGUIData(JSONArray states) throws JSONException {

		for (int i = 0; i < states.length(); i++) {
			final JSONObject state = states.getJSONObject(i);
			this.addState(state.getInt("id"), state.getString("name"), state.getDouble("locationX"),
					state.getDouble("locationY"), false);
		}
	}

	/**
	 * Sets the GUI data for all {@link Transition Transitions} A JSONArray that
	 * includes the required data for all available {@link Transition Transitions}
	 *
	 * @param transitions A JSONArray that includes the required data for the
	 *                    {@link Transition Transitions}
	 * @throws JSONException on invalid formatting of the {@link State States} JSON
	 *                       objects
	 */
	private void setTransitionsGUIData(JSONArray transitions) throws JSONException {
		for (int i = 0; i < transitions.length(); i++) {
			final JSONObject transition = transitions.getJSONObject(i);
			final State sourceState = this.states.get(transition.getInt("source"));
			final State targetState = this.states.get(transition.getInt("target"));
			final String trigger = transition.getString("trigger");
			this.addTransition(sourceState, targetState, trigger, false);
		}
	}

	/**
	 * Sets the scale of the view
	 *
	 * @param scale The new scale
	 */
	private void setViewScale(double scale) {
		this.dialogueElementsLayer.setScaleX(scale);
		this.dialogueElementsLayer.setScaleY(scale);
	}
}