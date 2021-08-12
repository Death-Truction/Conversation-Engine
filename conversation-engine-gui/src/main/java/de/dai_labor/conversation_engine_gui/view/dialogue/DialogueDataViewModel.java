package de.dai_labor.conversation_engine_gui.view.dialogue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.dai_labor.conversation_engine_gui.gui_components.State;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser.ExtensionFilter;

public class DialogueDataViewModel implements ViewModel {

	private DialogueViewModel dialogueViewModel;
	private SimpleStringProperty skillNameProperty;
	private SimpleStringProperty skillFilePathProperty;
	private SimpleStringProperty intentsProperty;
	private SimpleStringProperty entitiesProperty;
	private ObservableList<String> availableStates;

	public DialogueDataViewModel(DialogueViewModel dialogueViewModel) {
		this.dialogueViewModel = dialogueViewModel;
		this.availableStates = FXCollections.observableArrayList();
		this.skillFilePathProperty = new SimpleStringProperty();
		this.skillNameProperty = new SimpleStringProperty();
		this.intentsProperty = new SimpleStringProperty();
		this.entitiesProperty = new SimpleStringProperty();
		updateAvailableStates(null);
		this.dialogueViewModel.getStates().addListener((MapChangeListener<? super Integer, ? super State>) change -> {
			updateAvailableStates(null);
		});
	}

	public SimpleStringProperty getSkillFilePathProperty() {
		return this.skillFilePathProperty;
	}

	public String getSkillFilePath() {
		return this.skillFilePathProperty.get();
	}

	public void setSkillFilePath(String filePath) {
		this.skillFilePathProperty.set(filePath);
	}

	public SimpleStringProperty getSkillNameProperty() {
		return this.skillNameProperty;
	}

	public String getSkillName() {
		return this.skillNameProperty.get();
	}

	public void setSkillName(String skillName) {
		this.skillNameProperty.set(skillName);
	}

	public SimpleStringProperty getIntentsProperty() {
		return this.intentsProperty;
	}

	public String getIntents() {
		return this.intentsProperty.get();
	}

	public void setIntents(String intents) {
		this.intentsProperty.set(intents);
	}

	public SimpleStringProperty getEntitiesProperty() {
		return this.entitiesProperty;
	}

	public String getEntities() {
		return this.entitiesProperty.get();
	}

	public void setEntities(String entities) {
		this.entitiesProperty.set(entities);
	}

	public ObservableList<String> getAvailableState() {
		return this.availableStates;
	}

	public void updateAvailableStates(MouseEvent e) {
		List<String> allStates = new ArrayList<>();
		for (State state : this.dialogueViewModel.getStates().values()) {
			allStates.add(state.getName());
		}
		availableStates.retainAll(allStates);
		allStates.removeAll(availableStates);
		availableStates.addAll(allStates);
	}

	public void pickSkillFilePath(MouseEvent e) {
		String filepath = Util.fileChooser(false, new ExtensionFilter("Java Class", "*.class", "*.CLASS"));
		this.setSkillFilePath(filepath);
		if (this.getSkillName() == null || this.getSkillName().isBlank()) {
			String filename = new File(filepath).getName();
			if (filename.indexOf(".") > -1) {
				filename = filename.substring(0, filename.lastIndexOf("."));
			}
			this.setSkillName(filename);
		}
	}
}
