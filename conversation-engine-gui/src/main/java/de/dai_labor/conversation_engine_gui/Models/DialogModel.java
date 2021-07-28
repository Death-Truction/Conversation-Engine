package de.dai_labor.conversation_engine_gui.models;

import java.util.Random;

import de.dai_labor.conversation_engine_gui.gui_component.DialogModelPane;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class DialogModel {
	private DialogModelPane dialogModelPane;
	private DialogModelData dialogModelData;

	private Group stateGroup = new Group();
	private Pane testLayer = new Pane();

	public DialogModel() {
		stateGroup.getChildren().add(testLayer);
		this.testLayer.setMinSize(10000, 10000);
		this.dialogModelPane = new DialogModelPane(this.testLayer);
		this.dialogModelData = new DialogModelData();
	}

	public Node getView() {
		return this.dialogModelPane;
	}

	public void addState() {
		State newState = this.dialogModelData.addNewState(new Random().nextInt(1000), new Random().nextInt(720));
		this.testLayer.getChildren().add(newState);
	}
}
