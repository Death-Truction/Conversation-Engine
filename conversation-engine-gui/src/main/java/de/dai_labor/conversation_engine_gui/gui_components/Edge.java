package de.dai_labor.conversation_engine_gui.gui_components;

import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class Edge extends StackPane {
	private State source;
	private State target;
	private Arrow transition;

	public Edge(State source, State target, String triggerName) {
		this.source = source;
		this.target = target;
		this.transition = new Arrow(source, target, triggerName);
		this.getChildren().add(this.transition);
	}

	public String getSource() {
		return this.source.getName();
	}

	public String getTarget() {
		return this.target.getName();
	}

	public TextField getTriggerTextField() {
		return this.transition.getTriggerTextField();
	}
}