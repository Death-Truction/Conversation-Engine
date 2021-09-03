package de.dai_labor.conversation_engine_gui.view.simulation;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.interfaces.IStorableGuiData;
import de.dai_labor.conversation_engine_gui.models.ClassWithJarFile;
import de.dai_labor.conversation_engine_gui.models.LanguageEnum;
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
public class SimulationSettingsViewModel implements ViewModel, IStorableGuiData {

	private SimpleStringProperty selectedLanguageProperty = new SimpleStringProperty();
	private SimpleStringProperty conversationInputProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedSkillProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedNLPComponentProperty = new SimpleStringProperty();
	private ClassWithJarFile nlpComponent = null;
	private ClassWithJarFile skill = null;
	private ObservableList<String> availableLanguages = FXCollections.observableArrayList();
	private Settings settings;
	private boolean dataHasChanged = false;
	private static final String DEFAULT_LANGUAGE = LanguageEnum.GERMAN.toString();

	public SimulationSettingsViewModel(Settings settings) {
		this.settings = settings;
		for (LanguageEnum language : LanguageEnum.values()) {
			this.availableLanguages.add(language.toString());
		}
		this.selectedLanguageProperty.set(DEFAULT_LANGUAGE);
		this.addChangedListener(this.selectedLanguageProperty, this.conversationInputProperty,
				this.selectedSkillProperty, this.selectedNLPComponentProperty);
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

	public SimpleStringProperty getSelectedSkillProperty() {
		return this.selectedSkillProperty;
	}

	public ClassWithJarFile getNLPComponent() {
		return this.nlpComponent;
	}

	public ClassWithJarFile getSkill() {
		return this.skill;
	}

	public void pickNLPComponentFile() {
		String nlpComponentJarFilePath = Util.fileChooser(false, new ExtensionFilter("Java .jar File", "*.jar"),
				this.settings.getLastNLPComponentFolderPathProperty());
		if (nlpComponentJarFilePath.isBlank()) {
			return;
		}
		this.nlpComponent = this.selectImplementedClass(nlpComponentJarFilePath, INLPComponent.class);
		if (this.nlpComponent != null) {
			this.selectedNLPComponentProperty.set(this.nlpComponent.getSelectedClass().getName());
		}
	}

	public void pickSkillFile() {
		String skillJarFilePath = Util.fileChooser(false, new ExtensionFilter("Java .jar File", "*.jar"),
				this.settings.getLastSkillFolderPathProperty());
		if (skillJarFilePath.isBlank()) {
			return;
		}
		this.skill = this.selectImplementedClass(skillJarFilePath, ISkill.class);
		if (this.skill != null) {
			this.selectedSkillProperty.set(this.skill.getSelectedClass().getName());
		}
	}

	public void removeNLPComponent() {
		this.nlpComponent = null;
	}

	public void removeSkill() {
		this.skill = null;
	}

	public ObservableList<String> getAvailableLanguages() {
		return this.availableLanguages;
	}

	public void startSimulation() {
		// creates a new class and injects the required dependencies
		// the simulation stage does everything by itself
		App.easyDI.getInstance(SimulationStage.class);
	}

	@Override
	public boolean hasChanged() {
		return this.dataHasChanged;
	}

	@Override
	public void setUnchanged() {
		this.dataHasChanged = false;
	}

	@Override
	public JSONObject getGUIData() {
		JSONObject data = new JSONObject();
		data.put("selectedLanguage", this.selectedLanguageProperty.get());
		data.put("conversationInput", this.conversationInputProperty.get());
		data.put("skillFilePath", this.selectedSkillProperty.get());
		if (this.nlpComponent != null) {
			data.put("nlpClass", this.nlpComponent.getSelectedClass().getName());
			data.put("nlpJarFile", this.nlpComponent.getJarFile().getAbsolutePath());
		}
		if (this.skill != null) {
			data.put("skillClass", this.skill.getSelectedClass().getName());
			data.put("skillJarFile", this.skill.getJarFile().getAbsolutePath());
		}
		return data;
	}

	@Override
	public void setGUIData(JSONObject data) {
		this.selectedLanguageProperty.set(data.optString("selectedLanguage", LanguageEnum.GERMAN.name()));
		this.conversationInputProperty.set(data.optString("conversationInput"));
		this.selectedSkillProperty.set(data.optString("conversationInput"));
		String nlpClass = data.optString("nlpClass");
		String nlpJarFile = data.optString("nlpJarFile");
		if (!nlpClass.isBlank() && !nlpJarFile.isBlank()) {
			this.nlpComponent = this.loadClassWithJarFile(nlpClass, nlpJarFile);
			if (this.nlpComponent != null) {
				this.selectedNLPComponentProperty.set(this.nlpComponent.getSelectedClass().getName());
			}
		}
		String skillClass = data.optString("skillClass");
		String skillJarFile = data.optString("skillJarFile");
		if (!skillClass.isBlank() && !skillJarFile.isBlank()) {
			this.skill = this.loadClassWithJarFile(skillClass, skillJarFile);
			if (this.skill != null) {
				this.selectedSkillProperty.set(this.skill.getSelectedClass().getName());
			}
		}
		this.dataHasChanged = false;
	}

	@Override
	public void resetData() {
		this.selectedLanguageProperty.set(DEFAULT_LANGUAGE);
		this.conversationInputProperty.set("");
		this.selectedSkillProperty.set("");
		this.selectedNLPComponentProperty.set("");
		this.nlpComponent = null;
		this.dataHasChanged = false;
	}

	private ClassWithJarFile selectImplementedClass(String jarFilePath, Class<?> interfaceClass) {
		File jarFile = new File(jarFilePath);
		Set<Class<?>> classNames = this.getClassesFromJarFile(jarFile);

		if (classNames == null) {
			return null;
		}
		Iterator<Class<?>> iterator = classNames.iterator();
		while (iterator.hasNext()) {
			Class<?> newClass = iterator.next();
			if (!interfaceClass.isAssignableFrom(newClass) || newClass.isInterface()) {
				iterator.remove();
			}
		}
		if (classNames.isEmpty()) {
			Util.showError("Invalid Jar File", MessageFormat
					.format("No classes implementing the {0} interface where found!", interfaceClass.getSimpleName()));
			return null;
		}
		if (classNames.size() == 1) {
			return new ClassWithJarFile((Class<?>) classNames.toArray()[0], jarFile);
		} else {
			Class<?> selectedClass = this.selectNLPComponentClass(classNames);
			if (selectedClass == null) {
				return null;
			}
			return new ClassWithJarFile(selectedClass, jarFile);
		}
	}

	private ClassWithJarFile loadClassWithJarFile(String className, String jarPath) {
		File jarFile = new File(jarPath);
		Set<Class<?>> classNames = this.getClassesFromJarFile(jarFile);

		if (classNames == null) {
			return null;
		}
		Optional<Class<?>> selectedClass = classNames.stream().filter(element -> element.getName().equals(className))
				.findFirst();
		if (!selectedClass.isPresent()) {
			return null;
		}
		return new ClassWithJarFile(selectedClass.get(), jarFile);
	}

	private void addChangedListener(Property<?>... properties) {
		for (Property<?> property : properties) {
			property.addListener(change -> this.dataHasChanged = true);
		}
	}

	private Class<?> selectNLPComponentClass(Set<Class<?>> classNames) {
		ViewTuple<SimulationPickClassView, SimulationPickClassViewModel> viewTuple = FluentViewLoader
				.fxmlView(SimulationPickClassView.class).load();
		Class<?> selectedClass = null;
		viewTuple.getViewModel().addClasses(classNames);
		Stage pickClassStage = new Stage();
		Scene newScene = new Scene(viewTuple.getView());
		newScene.getStylesheets().add(Util.getStyleSheetPath());
		pickClassStage.setScene(newScene);
		pickClassStage.setMinWidth(300);
		pickClassStage.setMinHeight(500);
		pickClassStage.initModality(Modality.WINDOW_MODAL);
		pickClassStage.initOwner(App.mainStage);
		pickClassStage.getIcons().add(Util.getIcon());
		viewTuple.getViewModel().subscribe("pickClassCloseRequest", (t, observe) -> pickClassStage.close());
		pickClassStage.showAndWait();
		selectedClass = viewTuple.getViewModel().getSelectedClass();
		return selectedClass;
	}

	private Set<Class<?>> getClassesFromJarFile(File jarFile) {
		Set<Class<?>> classNames = null;
		try {
			classNames = Util.getClassesFromJarFile(jarFile);
		} catch (ClassNotFoundException | NoClassDefFoundError | IOException e) {
			Util.showError(MessageFormat.format("Error loading Jar File {0}", jarFile.getAbsoluteFile()),
					e.getLocalizedMessage());
			e.printStackTrace();
		}
		return classNames;
	}

}
