package de.dai_labor.conversation_engine_gui.view.diagram;

import javax.inject.Singleton;

import de.saxsys.mvvmfx.ViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

@Singleton
public class DialogueToolbarViewModel implements ViewModel {
	private ToggleGroup toggleGroup = new ToggleGroup();
	private DialogueMainViewModel dialogueMainViewModel;

	public DialogueToolbarViewModel(DialogueMainViewModel dialogueMainViewModel) {
		this.dialogueMainViewModel = dialogueMainViewModel;
		this.toggleGroup.selectedToggleProperty().addListener(event -> {
			dialogueMainViewModel.setMode(this.toggleGroup.getSelectedToggle());
		});
	}

	public void unselectToggleButtons() {
		this.toggleGroup.getSelectedToggle().setSelected(false);
	}

	public void addToggleGroup(Toggle toggle) {
		this.toggleGroup.getToggles().add(toggle);
	}

	public void resetView(ActionEvent e) {
		this.dialogueMainViewModel.centerElements();
	}
}