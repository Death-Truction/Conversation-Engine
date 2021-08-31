package de.dai_labor.conversation_engine_gui.view.simulation;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.ClassWithJarFile;
import de.dai_labor.conversation_engine_gui.models.Settings;
import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Singleton
public class SimulationSettingsViewModel implements ViewModel {

	private SimpleStringProperty selectedLanguageProperty = new SimpleStringProperty();
	private SimpleStringProperty conversationInputProperty = new SimpleStringProperty();
	private SimpleStringProperty skillFilePathProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedNLPComponentProperty = new SimpleStringProperty();
	private ClassWithJarFile nlpComponent = null;
	private ObservableList<String> availableLanguages = FXCollections.observableArrayList();
	private Settings settings;
	private boolean dataHasChanged = false;
	private static final String DEFAULT_LANGUAGE = "German";

	public SimulationSettingsViewModel(Settings settings) {
		this.settings = settings;
		this.availableLanguages.add("English");
		this.availableLanguages.add(DEFAULT_LANGUAGE);
		this.selectedLanguageProperty.set(DEFAULT_LANGUAGE);
		this.addChangedListener(this.selectedLanguageProperty, this.conversationInputProperty,
				this.skillFilePathProperty, this.selectedNLPComponentProperty);
	}

	public SimpleStringProperty getSelectedLanguageProperty() {
		return this.selectedLanguageProperty;
	}

	public SimpleStringProperty getConversationInputProperty() {
		return this.conversationInputProperty;
	}

	public SimpleStringProperty getSelectedNLPComponentProperty() {
		return this.selectedNLPComponentProperty;

	}

	public SimpleStringProperty getSkillFilePathProperty() {
		return this.skillFilePathProperty;
	}

	public void pickNLPComponentFile() {
		String nlpComponentPath = Util.fileChooser(false, new ExtensionFilter("Java .jar File", "*.jar"),
				this.settings.getLastNLPComponentFolderPath());
		if (nlpComponentPath.isBlank()) {
			return;
		}
		Set<Class<?>> classNames = null;
		File jarFile = null;
		try {
			jarFile = new File(nlpComponentPath);
			classNames = Util.getClassesFromJarFile(jarFile);
			Iterator<Class<?>> iterator = classNames.iterator();
			while (iterator.hasNext()) {
				Class<?> newClass = iterator.next();
				if (!INLPComponent.class.isAssignableFrom(newClass) || newClass.isInterface()) {
					iterator.remove();
				}
			}
		} catch (IOException e) {
			Util.showError("Invalid NLP File", e.getMessage());
		}
		if (classNames == null || classNames.isEmpty()) {
			Util.showError("Invalid NLP File", "No classes implementing the INLPComponent interface where found!");
		} else if (classNames.size() == 1) {
			this.nlpComponent = new ClassWithJarFile((Class<?>) classNames.toArray()[0], jarFile);
		} else {
			Class<?> selectedClass = this.selectNLPComponentClass(classNames);
			if (selectedClass == null) {
				return;
			}
			this.nlpComponent = new ClassWithJarFile(selectedClass, jarFile);
		}
		this.selectedNLPComponentProperty.set(this.nlpComponent.getSelectedClass().getName());
	}

	public ObservableList<String> getAvailableLanguages() {
		return this.availableLanguages;
	}

	public void startSimulation() {
		// creates a new class and injects the required dependencies
		// the simulation stage does everything by itself
		App.easyDI.getInstance(SimulationStage.class);
	}

	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	public void setUnchanged() {
		this.dataHasChanged = false;
	}

	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("selectedLanguage", this.selectedLanguageProperty.get());
		data.put("conversationInput", this.conversationInputProperty.get());
		data.put("skillFilePath", this.skillFilePathProperty.get());
		data.put("nlpClass", this.nlpComponent.getSelectedClass().getName());
		data.put("nlpJarFile", this.nlpComponent.getJarFile().getAbsolutePath());
		return data;
	}

	public void setGUIData(JSONObject data) {
		this.selectedLanguageProperty.set(data.optString("selectedLanguage", "German"));
		this.conversationInputProperty.set(data.optString("conversationInput"));
		this.skillFilePathProperty.set(data.optString("conversationInput"));
		this.createClassWithJarFile(data.optString("nlpClass"), data.optString("nlpJarFile"));
		if (this.nlpComponent != null) {
			this.selectedNLPComponentProperty.set(this.nlpComponent.getSelectedClass().getName());
		}
		this.dataHasChanged = false;
	}

	public void pickSkillFilePath() {
		String filepath = Util.fileChooser(false, new ExtensionFilter("Java Class", "*.class", "*.CLASS"));
		this.skillFilePathProperty.set(filepath);
	}

	public void resetData() {
		this.selectedLanguageProperty.set(DEFAULT_LANGUAGE);
		this.conversationInputProperty.set("");
		this.skillFilePathProperty.set("");
		this.selectedNLPComponentProperty.set("");
		this.nlpComponent = null;
		this.dataHasChanged = false;
	}

	private void createClassWithJarFile(String className, String jarPath) {
		File jarFile = new File(jarPath);
		Set<Class<?>> classNames;
		try {
			classNames = Util.getClassesFromJarFile(jarFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Optional<Class<?>> selectedClass = classNames.stream().filter(element -> element.getName().equals(className))
				.findFirst();
		if (!selectedClass.isPresent()) {
			return;
		}
		this.nlpComponent = new ClassWithJarFile(selectedClass.get(), jarFile);
	}

	private void addChangedListener(Property... properties) {
		for (Property property : properties) {
			property.addListener(change -> this.dataHasChanged = true);
		}
	}

	private Class selectNLPComponentClass(Set<Class<?>> classNames) {
		ViewTuple<SimulationPickClassView, SimulationPickClassViewModel> viewTuple = FluentViewLoader
				.fxmlView(SimulationPickClassView.class).load();
		Class<?> selectedClass = null;
		viewTuple.getViewModel().addClasses(classNames);
		Stage pickClassStage = new Stage();
		Scene newScene = new Scene(viewTuple.getView());
		newScene.getStylesheets().add(Util.getStyleSheetPath());
		pickClassStage.setScene(newScene);
		pickClassStage.setMinWidth(100);
		pickClassStage.setMinHeight(200);
		pickClassStage.initModality(Modality.WINDOW_MODAL);
		pickClassStage.initOwner(App.mainStage);
		pickClassStage.getIcons().add(Util.getIcon());
		pickClassStage.setResizable(false);
		viewTuple.getViewModel().subscribe("pickClassCloseRequest", (t, observe) -> pickClassStage.close());
		pickClassStage.showAndWait();
		selectedClass = viewTuple.getViewModel().getSelectedClass();
		return selectedClass;
	}

}
