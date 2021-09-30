package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * Holds a Transition between two {@link State States}
 *
 * @author Marcel Engelmann
 *
 */
public class Transition extends StackPane {
	private State source;
	private State target;
	private TransitionArrow transitionArrow;
	private SimpleObjectProperty<Transition> selectedTransition;

	/**
	 * Creates a new {@link Transition}
	 *
	 * @param source             The source {@link State} of the {@link Transition}
	 * @param target             The target {@link State} of the {@link Transition}
	 * @param triggerName        The name of the trigger for the {@link Transition}
	 * @param selectedTransition The Property of the currently selected
	 *                           {@link Transition}
	 * @param requestFocus       Whether the new {@link State} should be selected
	 *                           after creating it
	 * @param settings           The instance of the {@link Settings} object
	 */
	public Transition(State source, State target, String triggerName,
			SimpleObjectProperty<Transition> selectedTransition, boolean requestFocus, Settings settings) {
		this.source = source;
		this.target = target;
		this.selectedTransition = selectedTransition;
		this.selectedTransition.addListener((observable, oldVal, newVal) -> {
			if (newVal != null && newVal.equals(this)) {
				this.transitionArrow.select();
			} else {
				this.transitionArrow.deselect();
			}
		});
		this.transitionArrow = new TransitionArrow(source, target, triggerName, () -> this.selectedTransition.set(this),
				settings);
		this.getChildren().add(this.transitionArrow);
		this.transitionArrow.toBack();
		if (requestFocus) {
			this.selectedTransition.set(this);
			this.initFocusRequest();
		}
		this.setPickOnBounds(false);
	}

	/**
	 * Gets the source {@link State} of the {@link Transition}
	 *
	 * @return the source {@link State} of the {@link Transition}
	 */
	public State getSource() {
		return this.source;
	}

	/**
	 * Gets the target {@link State} of the {@link Transition}
	 *
	 * @return the target {@link State} of the {@link Transition}
	 */
	public State getTarget() {
		return this.target;
	}

	/**
	 * Gets the {@link TextField} that displays the trigger
	 *
	 * @return the {@link TextField} that displays the trigger
	 */
	public TextField getTriggerTextField() {
		return this.transitionArrow.getTriggerTextField();
	}

	/**
	 * Gets the trigger of this {@link Transition}
	 *
	 * @return the trigger of this {@link Transition}
	 */
	public String getTrigger() {
		return this.transitionArrow.getTriggerTextField().getText();
	}

	/**
	 * Selects this {@link Transition}
	 */
	public void select() {
		this.transitionArrow.select();
	}

	/**
	 * Deselects this {@link Transition}
	 */
	public void deselect() {
		this.transitionArrow.deselect();
	}

	@Override
	public String toString() {
		return this.source.getName() + " -> " + this.target.getName();
	}

	/**
	 * Sets focus on the {@link TextField} that displays the trigger
	 */
	private void focusTriggerTextField() {
		this.transitionArrow.focusTriggerTextField();
	}

	/**
	 * Focuses this {@link Transition} if it is still the selected
	 * {@link Transition} in the next tick
	 */
	private void initFocusRequest() {
		Platform.runLater(() -> {
			if (this.selectedTransition.get().equals(this)) {
				this.focusTriggerTextField();
			}
		});
	}
}