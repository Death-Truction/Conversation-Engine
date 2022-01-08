package de.dai_labor.dialog_modeling_tool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import de.dai_labor.conversation_engine_core.conversation_engine.ConversationEngine;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.dialog_modeling_tool.App;
import de.dai_labor.dialog_modeling_tool.gui_components.Transition;
import de.dai_labor.dialog_modeling_tool.interfaces.IStorableGuiData;
import de.dai_labor.dialog_modeling_tool.models.SaveStateEnum;
import de.dai_labor.dialog_modeling_tool.models.Settings;
import de.dai_labor.dialog_modeling_tool.view.dialog.DialogDataViewModel;
import de.dai_labor.dialog_modeling_tool.view.dialog.DialogViewModel;
import eu.lestard.easydi.EasyDI;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Static utility class
 *
 * @author Marcel Engelmann
 *
 */
public class Util {

	/**
	 * Static class, constructor is not allowed
	 *
	 * @throws IllegalStateException static class, constructor is not allowed
	 */
	private Util() throws IllegalStateException {
		throw new IllegalStateException("Static class");
	}

	/**
	 * Saves the GUI data of all classes implementing the {@link IStorableGuiData}
	 * interface to a file. The target file path will be selected by the user.
	 *
	 * @param askFirst      Whether to user should be asked first before opening the
	 *                      {@link FileChooser} the set the target path
	 * @param saveToNewFile Whether the GUI data should be saved to a new file and
	 *                      not to the currently opened one. If there is currently
	 *                      an opened file.
	 * @return {@link SaveStateEnum#YES} if the user picked a target file to save
	 *         the data to. {@link SaveStateEnum#NO} if the user does not want to
	 *         save the file. {@link SaveStateEnum#CANCEL} if the user canceled the
	 *         saving process
	 */
	public static SaveStateEnum saveGUIDataToFile(boolean askFirst, boolean saveToNewFile) {
		EasyDI easyDI = App.easyDI;
		Settings settings = easyDI.getInstance(Settings.class);
		boolean dataHasChanged = hasGUIDataChanged();

		if (!saveToNewFile && !dataHasChanged && askFirst) {
			return SaveStateEnum.NO;
		}
		String filepath = "";
		if (!saveToNewFile) {
			filepath = settings.getLastOpenedFile();
		}
		// ask user if he wants to save the unsaved changes
		if (askFirst) {
			SaveStateEnum saveState = saveDataBeforeExitConfirmation();
			if (saveState != SaveStateEnum.YES) {
				return saveState;
			}
		}
		// if the file has not been saved before -> ask for a save location
		if (filepath.isBlank()) {
			filepath = fileChooser(true, new ExtensionFilter("CEGUI File", "*.cegui", "*.CEGUI"));
		}
		// if the user picked a file
		if (!filepath.isBlank()) {
			if (!filepath.endsWith(".cegui")) {
				filepath += ".cegui";
			}
			String data = getToSavedData().toString();
			settings.setLastOpenedFile(filepath);
			saveStringToFile(filepath, data);
			setGUIDataUnchanged();
			return SaveStateEnum.YES;
		}
		// if the user pressed cancel on the fileChooser
		return SaveStateEnum.CANCEL;
	}

	/**
	 * Let's the user pick a file to load the GUI data from and loads them, by
	 * passing the data to each class implementing the {@link IStorableGuiData}
	 * interface.
	 */
	public static void loadGUIDataFromFile() {

		EasyDI easyDI = App.easyDI;
		Settings settings = easyDI.getInstance(Settings.class);
		String filepath = fileChooser(false, new ExtensionFilter("CEGUI File", "*.cegui", "*.CEGUI"));
		settings.setLastOpenedFile(filepath);
		String jsonString = loadStringFromFile(filepath);
		if (jsonString.isBlank()) {
			return;
		}
		try {
			JSONObject guiData = new JSONObject(jsonString);
			setLoadedData(guiData);
		} catch (JSONException e) {
			showError(MessageFormat.format("Error loading the file {0}", new File(filepath).getName()),
					e.getLocalizedMessage());
		}
	}

	/**
	 * Reads the content of the file as a {@link String}
	 *
	 * @param filepath The file path of the file to be red
	 * @return the content of the file
	 */
	public static String loadStringFromFile(String filepath) {
		String data = "";
		try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
			data = lines.collect(Collectors.joining("\n"));

		} catch (IOException e) {
			showError(MessageFormat.format("Error loading the file {0}", new File(filepath).getName()),
					e.getLocalizedMessage());
		}
		return data;
	}

	/**
	 * Displays an {@link Alert} window with the given title and error message
	 *
	 * @param title        The title of the {@link Alert} window
	 * @param errorMessage The error message
	 */
	public static void showError(String title, String errorMessage) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText(title);
		errorAlert.setContentText(errorMessage);
		errorAlert.showAndWait();
	}

	/**
	 * Exports the GUI data to a JSON file picked by the user
	 */
	public static void exportData() {
		String exportData = Util.getSkillStateMachineData().toString();
		String filePath = Util.fileChooser(true, new ExtensionFilter("JSON-Files", "*.json"));
		if (filePath != null && !filePath.isBlank()) {
			Util.saveStringToFile(filePath, exportData);
		}
	}

	/**
	 * Opens a {@link FileChooser} to select a target file
	 *
	 * @param save       Whether select a file for a saving or opening action
	 * @param extensions The allowed file extension to pick
	 * @return The file path to the selected target file
	 */
	public static String fileChooser(boolean save, ExtensionFilter extensions) {
		Settings settings = App.easyDI.getInstance(Settings.class);
		return fileChooser(save, extensions, settings.getLastFileChooserFolderProperty());
	}

	/**
	 * Opens a {@link FileChooser} to select a target file
	 *
	 * @param save       Whether select a file for a saving or opening action
	 * @param extensions The allowed file extension to pick
	 * @param folderPath The path to the directory that the {@link FileChooser}
	 *                   should start in
	 * @return The file path to the selected target file
	 */
	public static String fileChooser(boolean save, ExtensionFilter extensions, StringProperty folderPath) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(extensions);
		File file = null;
		File folder = new File(folderPath.get());
		if (folder.isDirectory()) {
			fileChooser.setInitialDirectory(folder);
		}
		if (save) {
			fileChooser.setTitle("Save file");
			file = fileChooser.showSaveDialog(App.mainStage);
		} else {
			fileChooser.setTitle("Open file");
			file = fileChooser.showOpenDialog(App.mainStage);
		}
		if (file == null) {
			return "";
		}
		folderPath.set(file.getParent());
		return file.getAbsolutePath();
	}

	/**
	 * Saves a String to a given file
	 *
	 * @param filepath The path to the target file
	 * @param data     The data to be saved
	 */
	public static void saveStringToFile(String filepath, String data) {
		try (FileWriter fileWriter = new FileWriter(filepath)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException e) {
			showError("Error while saving the file", e.getLocalizedMessage());
		}

	}

	/**
	 * Gets the Application's icon
	 *
	 * @return The {@link Image} of the icon
	 */
	public static Image getIcon() {
		return new Image(App.class.getResource("images/Icon.png").toExternalForm());
	}

	/**
	 * Gets the path to the style sheet file
	 *
	 * @return the path to the style sheet file
	 */
	public static String getStyleSheetPath() {
		return App.class.getResource("styles/style.css").toExternalForm();
	}

	/**
	 * Gets a Set of all classes from a jar file
	 *
	 * @param jarFile The file path to the jar file
	 * @return a Set of all classes from a jar file
	 * @throws IOException when the given file path is invalid
	 */
	public static Set<Class<?>> getClassesFromJarFile(File jarFile) throws IOException {
		Set<Class<?>> classNames = new HashSet<>();
		try (JarFile loadedJarFile = new JarFile(jarFile)) {
			URL[] urls = { new URL(String.format("jar:file:%s!/", jarFile.getAbsolutePath())) };
			try (URLClassLoader cl = URLClassLoader.newInstance(urls)) {
				Enumeration<JarEntry> iterator = loadedJarFile.entries();
				while (iterator.hasMoreElements()) {
					JarEntry entry = iterator.nextElement();
					if (entry.getName().endsWith(".class")) {
						Class<?> foundClass = cl.loadClass(
								entry.getName().substring(0, entry.getName().lastIndexOf(".")).replace("/", "."));
						classNames.add(foundClass);
					}
				}
			} catch (ClassNotFoundException e) {
				// Should never happen, because the class is loaded from the jar file and
				// directly used
			}
		}
		return classNames;
	}

	/**
	 * Gets all the data required to create a {@link ISkill}'s state machine by the
	 * {@link ConversationEngine}
	 *
	 * @return all the data required to create a {@link ISkill}'s state machine by
	 *         the {@link ConversationEngine}
	 */
	public static JSONObject getSkillStateMachineData() {
		JSONObject data = new JSONObject();
		DialogDataViewModel dialogDataViewModel = App.easyDI.getInstance(DialogDataViewModel.class);
		DialogViewModel dialogViewModel = App.easyDI.getInstance(DialogViewModel.class);
		data.put("name", dialogDataViewModel.getSkillNameProperty().get());
		data.put("startAt", dialogDataViewModel.getSelectedStartStateProperty().get());
		data.put("endAt", dialogDataViewModel.getSelectedEndStateProperty().get());
		JSONArray entities = new JSONArray();
		for (String entity : dialogDataViewModel.getEntitiesProperty().get().split("\n")) {
			entities.put(entity);
		}
		data.put("usedEntities", entities);
		JSONArray intents = new JSONArray();
		for (String intent : dialogDataViewModel.getIntentsProperty().get().split("\n")) {
			entities.put(intent);
		}
		data.put("usedIntents", intents);

		JSONArray states = new JSONArray();
		dialogViewModel.getStates().forEach((key, state) -> {
			JSONObject stateObject = new JSONObject();
			stateObject.put("name", state.getName());
			states.put(stateObject);
		});
		data.put("states", states);

		JSONArray transitions = new JSONArray();
		for (Transition transition : dialogViewModel.getTransitions()) {
			JSONObject transitionObject = new JSONObject();
			transitionObject.put("source", transition.getSource().getName());
			transitionObject.put("target", transition.getTarget().getName());
			transitionObject.put("trigger", transition.getTrigger());
			transitions.put(transitionObject);
		}
		data.put("transitions", transitions);
		return data;
	}

	/**
	 * Resets all the GUI data by calling each class implementing the
	 * {@link IStorableGuiData} interface
	 */
	public static void resetGUIData() {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			storableGuiData.resetData();

		}
	}

	/**
	 * Asks the user whether he wants to save the made changes or not
	 *
	 * @return {@link SaveStateEnum#YES} if the user wants to save the changes
	 *         {@link SaveStateEnum#NO} if the user does not want to save the
	 *         changes {@link SaveStateEnum#CANCEL} if the user canceled the process
	 */
	private static SaveStateEnum saveDataBeforeExitConfirmation() {

		Alert saveBeforeExitConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"You have unsaved changes.\nWould you like to save them now?");
		saveBeforeExitConfirmation.setHeaderText("Save changes");
		saveBeforeExitConfirmation.setTitle("Unsaved changes");
		saveBeforeExitConfirmation.initModality(Modality.APPLICATION_MODAL);
		((Stage) saveBeforeExitConfirmation.getDialogPane().getScene().getWindow()).getIcons().add(getIcon());
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		saveBeforeExitConfirmation.getButtonTypes().clear();
		saveBeforeExitConfirmation.getButtonTypes().addAll(yesButton, noButton, cancelButton);
		Optional<ButtonType> response = saveBeforeExitConfirmation.showAndWait();
		if (response.isPresent()) {
			if (response.get() == yesButton) {
				return SaveStateEnum.YES;
			}
			if (response.get() == noButton) {
				return SaveStateEnum.NO;
			}
		}
		return SaveStateEnum.CANCEL;
	}

	/**
	 * Gets all the GUI data from all classes implementing the
	 * {@link IStorableGuiData} interface
	 *
	 * @return all the GUI data from all classes implementing the
	 *         {@link IStorableGuiData} interface
	 */
	private static JSONObject getToSavedData() {
		JSONObject savedData = new JSONObject();
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			savedData.put(storableGuiDataClass.getSimpleName(), storableGuiData.getGUIData());

		}
		return savedData;
	}

	/**
	 * Sets the GUI data of all classes implementing the {@link IStorableGuiData}
	 * interface
	 *
	 * @param guiData the GUI data to set
	 */
	private static void setLoadedData(JSONObject guiData) {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			JSONObject data = guiData.optJSONObject(storableGuiDataClass.getSimpleName());
			if (data != null) {
				IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
				storableGuiData.setGUIData(data);
			}
		}
	}

	/**
	 * Checks whether any GUI data of the classes implementing the
	 * {@link IStorableGuiData} interface has been changed
	 *
	 * @return true if any GUI data has been changed
	 */
	private static boolean hasGUIDataChanged() {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			if (storableGuiData.hasChanged()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets all GUI data of the classes implementing the {@link IStorableGuiData}
	 * interface to unchanged
	 */
	private static void setGUIDataUnchanged() {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			storableGuiData.setUnchanged();
		}
	}

	/**
	 * Gets all classes implementing the {@link IStorableGuiData} interface
	 *
	 * @return all classes implementing the {@link IStorableGuiData} interface
	 */
	private static Set<Class<? extends IStorableGuiData>> getIStorableGuiDataClasses() {
		Reflections reflections = new Reflections("de.dai_labor.dialog_modeling_tool");

		return reflections.getSubTypesOf(IStorableGuiData.class);
	}
}
