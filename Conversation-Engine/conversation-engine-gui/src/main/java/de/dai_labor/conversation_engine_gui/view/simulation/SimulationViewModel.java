package de.dai_labor.conversation_engine_gui.view.simulation;

import de.dai_labor.conversation_engine_gui.gui_components.DialoguePane;
import de.dai_labor.conversation_engine_gui.view.dialogue.DialogueViewModel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class SimulationViewModel implements ViewModel {
	private DialoguePane dialogueView;

	public SimulationViewModel(DialogueViewModel dialogueViewModel) {
		this.dialogueView = dialogueViewModel.getView();
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(true);
	}

	public Node getDialoguePane() {
		return this.dialogueView;
	}

	public void unload() {
		Pane dialogueElementsLayer = (Pane) this.dialogueView.getChildren().get(0);
		dialogueElementsLayer.setDisable(false);
	}

}
