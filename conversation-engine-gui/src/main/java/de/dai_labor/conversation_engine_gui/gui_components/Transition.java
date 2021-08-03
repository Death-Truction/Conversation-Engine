package de.dai_labor.conversation_engine_gui.gui_components;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class Transition extends StackPane {
	private State source;
	private State target;
	private Arrow transition;

	public Transition(State source, State target, String triggerName) {
		this.source = source;
		this.target = target;
		this.transition = new Arrow(source, target, triggerName);
		this.getChildren().add(this.transition);
		this.transition.addEventFilter(MouseEvent.ANY, event -> {
			if (event.getEventType() != MouseEvent.MOUSE_PRESSED || event.getButton() != MouseButton.PRIMARY
					|| event.getClickCount() != 2) {
				event.consume();
				return;
			}
		});
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
}