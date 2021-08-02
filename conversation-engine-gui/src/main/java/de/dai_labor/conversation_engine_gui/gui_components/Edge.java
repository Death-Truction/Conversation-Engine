package de.dai_labor.conversation_engine_gui.gui_components;

import javafx.scene.layout.StackPane;

public class Edge extends StackPane {
	private State source;
	private State target;

	public Edge(State source, State target) {
		this.source = source;
		this.target = target;
		Arrow arrow = new Arrow(source, target);
		this.getChildren().add(arrow);
	}

	public String getSource() {
		return this.source.getName();
	}

	public String getTarget() {
		return this.target.getName();
	}
}