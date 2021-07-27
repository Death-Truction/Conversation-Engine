package de.dai_labor.conversation_engine_gui.models;

import de.dai_labor.conversation_engine_gui.gui_component.DialogModelPane;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class DialogModel {
	private DialogModelPane dialogModelPane;
	private DialogModelData dialogModelData;

	private Group testLayer = new Group();
	private Pane stateGroup = new Pane();

	public DialogModel() {
		testLayer.getChildren().add(stateGroup);
		this.dialogModelPane = new DialogModelPane(this.testLayer);
		this.dialogModelData = new DialogModelData();
	}

	public Node getView() {
		return this.dialogModelPane;
	}

	public void addState() {
		State newState = this.dialogModelData.addState();
		this.stateGroup.getChildren().add(newState);
	}
}
