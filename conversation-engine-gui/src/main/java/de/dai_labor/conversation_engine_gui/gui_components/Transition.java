package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class Transition extends StackPane {
	private State source;
	private State target;
	private Arrow transition;
	private SimpleObjectProperty<Transition> selectedTransition;

	public Transition(State source, State target, String triggerName,
			SimpleObjectProperty<Transition> selectedTransition, boolean requestFocus, Settings settings) {
		this.source = source;
		this.target = target;
		this.selectedTransition = selectedTransition;
		this.selectedTransition.addListener((observable, oldVal, newVal) -> this.selectionStatus(newVal));
		this.transition = new Arrow(source, target, triggerName, () -> this.selectedTransition.set(this), settings);
		this.getChildren().add(this.transition);
		this.transition.toBack();
		if (requestFocus) {
			this.selectedTransition.set(this);
			this.initFocusRequest();
		}
		this.setPickOnBounds(false);
	}

	public State getSource() {
		return this.source;
	}

	public State getTarget() {
		return this.target;
	}

	public TextField getTriggerTextField() {
		return this.transition.getTriggerTextField();
	}

	private void selectionStatus(Transition newVal) {
		if (newVal != null && newVal.equals(this)) {
			this.transition.select();
		} else {
			this.transition.deselect();
		}
	}

	private void focusTriggerTextField() {
		this.transition.focusTriggerTextField();
	}

	private void initFocusRequest() {
		Platform.runLater(() -> {
			if (this.selectedTransition.get().equals(this)) {
				this.focusTriggerTextField();
			}
		});
	}
}