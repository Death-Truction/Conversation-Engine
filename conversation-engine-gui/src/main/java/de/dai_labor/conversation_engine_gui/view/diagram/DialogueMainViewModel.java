package de.dai_labor.conversation_engine_gui.view.diagram;

import javax.inject.Singleton;

import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.models.DialogueModelData;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;

@Singleton
public class DialogueMainViewModel implements ViewModel {
	private DialoguePane dialoguePane;
	private DialogueModelData dialogueModelData;
	private Pane diagramElementsLayer;
	private SimpleStringProperty insertMode = new SimpleStringProperty("");
	private Toggle toggleButton;

	public DialogueMainViewModel(DialogueModelData dialogueModelData) {
		this.dialogueModelData = dialogueModelData;
		this.diagramElementsLayer = new Pane();
		this.dialoguePane = new DialoguePane(diagramElementsLayer, this::addState, this.insertMode);
	}

	public Node getView() {
		return this.dialoguePane;
	}

	public void centerElements() {
		this.dialoguePane.centerMovingElement();
	}

	public void setMode(Toggle toggleButton) {
		this.toggleButton = toggleButton;
		if (toggleButton == null) {
			this.insertMode.set("");
		} else {
			this.insertMode.set(toggleButton.getUserData().toString());
		}
	}

	public void addState(Double x, Double y) {
		// center location on mousePointer
		x -= State.INITIAL_SIZE / 2;
		y -= State.INITIAL_SIZE / 2;
		this.diagramElementsLayer.getChildren().add(dialogueModelData.addNewState(x, y));
		this.toggleButton.setSelected(false);
	}

}