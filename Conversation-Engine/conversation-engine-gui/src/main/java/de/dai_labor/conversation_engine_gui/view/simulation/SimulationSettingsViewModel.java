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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The ViewModel of the {@link SimulationSettingsView}
 *
 * @author Marcel Engelmann
 *
 */
@Singleton
public class SimulationSettingsViewModel implements ViewModel, IStorableGuiData {

	private SimpleStringProperty selectedLanguageProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedLoggingLevelProperty = new SimpleStringProperty();
	private SimpleStringProperty conversationInputProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedSkillProperty = new SimpleStringProperty();
	private SimpleStringProperty selectedNLPComponentProperty = new SimpleStringProperty();
	private ClassWithJarFile nlpComponent = null;
	private ClassWithJarFile skill = null;
	private ObservableList<String> availableLanguages = FXCollections.observableArrayList();
	private ObservableList<String> availableLoggingLevels = FXCollections.observableArrayList();
	private Settings settings;
	private boolean dataHasChanged = false;
	private static final String DEFAULT_LANGUAGE = LanguageEnum.GERMAN.toString();
	private static final String DEFAULT_LOGGING_LEVEL = "DEBUG";

	/**
	 * Creates a new {@link SimulationSettingsViewModel} instance
	 *
	 * @param settings The instance of the {@link Settings} class
	 */
	public SimulationSettingsViewModel(Settings settings) {
		this.settings = settings;
		for (LanguageEnum language : LanguageEnum.values()) {
			this.availableLanguages.add(language.toString());
		}
		this.selectedLanguageProperty.set(DEFAULT_LANGUAGE);
		this.addChangedListener(this.selectedLanguageProperty, this.conversationInputProperty,
				this.selectedSkillProperty, this.selectedNLPComponentProperty, this.selectedLoggingLevelProperty);
		this.availableLoggingLevels.addAll("ALL", "TRACE", DEFAULT_LOGGING_LEVEL, "INFO", "WARN", "ERROR", "OFF");
		this.selectedLoggingLevelProperty.set(DEFAULT_LOGGING_LEVEL);
	}

	/**
	 * Gets the {@link Property} of the selected language
	 *
	 * @return the {@link Property} of the selected language
	 */
	public SimpleStringProperty getSelectedLanguageProperty() {
		return this.selectedLanguageProperty;
	}

	/**
	 * Gets the {@link Property} of the selected logging level
	 *
	 * @return the {@link Property} of the selected logging level
	 */
	public SimpleStringProperty getSelectedLoggingLevelProperty() {
		return this.selectedLoggingLevelProperty;
	}

	/**
	 * Gets the {@link Property} of the conversation input
	 *
	 * @return the {@link Property} of the conversation input
	 */
	public SimpleStringProperty getConversationInputProperty() {
		return this.conversationInputProperty;
	}

	/**
	 * Gets the {@link Property} of the selected {@link INLPComponent}
	 *
	 * @return the {@link Property} of the selected {@link INLPComponent}
	 */
	public SimpleStringProperty getSelectedNLPComponentProperty() {
		return this.selectedNLPComponentProperty;

	}

	/**
	 * Gets the {@link Property} of the selected {@link ISkill}
	 *
	 * @return the {@link Property} of the selected {@link ISkill}
	 */
	public SimpleStringProperty getSelectedSkillProperty() {
		return this.selectedSkillProperty;
	}

	/**
	 * Gets the {@link ClassWithJarFile} of the {@link INLPComponent}
	 *
	 * @return the {@link ClassWithJarFile} of the {@link INLPComponent}
	 */
	public ClassWithJarFile getNLPComponent() {
		return this.nlpComponent;
	}

	/**
	 * Gets the {@link ClassWithJarFile} of the {@link ISkill}
	 *
	 * @return the {@link ClassWithJarFile} of the {@link ISkill}
	 */
	public ClassWithJarFile getSkill() {
		return this.skill;
	}

	/**
	 * Prompts the user to selected the jar file including the {@link INLPComponent}
	 */
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

	/**
	 * Prompts the user to selected the jar file including the {@link ISkill}
	 */
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

	/**
	 * Removes the {@link INLPComponent}
	 */
	public void removeNLPComponent() {
		this.nlpComponent = null;
	}

	/**
	 * Removes the {@link ISkill}
	 */
	public void removeSkill() {
		this.skill = null;
	}

	/**
	 * Gets the {@link ObservableList} that contains all available languages
	 *
	 * @return the {@link ObservableList} that contains all available languages
	 */
	public ObservableList<String> getAvailableLanguages() {
		return this.availableLanguages;
	}

	/**
	 * Gets the {@link ObservableList} that contains all available logging levels
	 *
	 * @return the {@link ObservableList} that contains all available logging levels
	 */
	public ObservableList<String> getAvailableLoggingLevels() {
		return this.availableLoggingLevels;
	}

	/**
	 * Starts the simulation by creating a new {@link SimulationStage}
	 */
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
		data.put("selectedLoggingLevel", this.selectedLoggingLevelProperty.get());
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
		this.selectedLoggingLevelProperty.set(data.optString("selectedLoggingLevel", DEFAULT_LOGGING_LEVEL));
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
		this.selectedLoggingLevelProperty.set(DEFAULT_LOGGING_LEVEL);
	}

	/**
	 * Lets the user select a class the implements the given interface from the
	 * given jar file.
	 *
	 * @param jarFilePath    The file path to the jar file that is supposed to be
	 *                       checked for the implemented interface
	 * @param interfaceClass The interface that the classes must implement, which
	 *                       the user can select
	 * @return a new instance of the {@link ClassWithJarFile} storing the jar file
	 *         path and the selected class implementing the given interface. Returns
	 *         null if the user did not select a class or no appropriate class has
	 *         been found within the jar file
	 */
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
			Class<?> selectedClass = this.userSelectClass(classNames);
			if (selectedClass == null) {
				return null;
			}
			return new ClassWithJarFile(selectedClass, jarFile);
		}
	}

	/**
	 * Creates a new {@link ClassWithJarFile} instance storing the file path to the
	 * given jar file and the class that fits the given class name
	 *
	 * @param className The name of the class to load from the jar file
	 * @param jarPath   The file path to the jar file
	 * @return a new {@link ClassWithJarFile} instance storing the file path to the
	 *         given jar file and the class that fits the given class name
	 */
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

	/**
	 * Adds a {@link ChangeListener} to the {@link Property Properties} that updates
	 * the {@link #dataHasChanged} attribute accordingly
	 *
	 * @param properties
	 */
	private void addChangedListener(Property<?>... properties) {
		for (Property<?> property : properties) {
			property.addListener(change -> this.dataHasChanged = true);
		}
	}

	/**
	 * Creates a new {@link SimulationPickClassView} that let's the user select a
	 * class of the given set of classes
	 *
	 * @param classes The classes the user can choose one class from
	 * @return The user's selected class or null if the user canceled the process
	 */
	private Class<?> userSelectClass(Set<Class<?>> classes) {
		ViewTuple<SimulationPickClassView, SimulationPickClassViewModel> viewTuple = FluentViewLoader
				.fxmlView(SimulationPickClassView.class).load();
		Class<?> selectedClass = null;
		viewTuple.getViewModel().addClasses(classes);
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

	/**
	 * Gets a {@link Set} of all classes from a jar file
	 *
	 * @param jarFile The file path to the jar file
	 * @return all classes from a jar file
	 */
	private Set<Class<?>> getClassesFromJarFile(File jarFile) {
		Set<Class<?>> classNames = null;
		try {
			classNames = Util.getClassesFromJarFile(jarFile);
		} catch (IOException e) {
			Util.showError(MessageFormat.format("Error loading Jar File {0}", jarFile.getAbsoluteFile()),
					e.getLocalizedMessage());
			e.printStackTrace();
		}
		return classNames;
	}

}
