package de.dai_labor.conversation_engine_gui.util;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.interfaces.IStorableGuiData;
import de.dai_labor.conversation_engine_gui.models.SaveStateEnum;
import de.dai_labor.conversation_engine_gui.models.Settings;
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

public class Util {
	private Util() throws IllegalStateException {
		throw new IllegalStateException("Utility class");
	}

	public static SaveStateEnum saveGUIDataToFile(boolean askFirst, boolean saveToNewFile, boolean forceSave) {
		EasyDI easyDI = App.easyDI;
		Settings settings = easyDI.getInstance(Settings.class);
		boolean dataHasChanged = hasGUIDataChanged();

		if (!saveToNewFile && !dataHasChanged && !forceSave) {
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
			saveJSONStringToFile(filepath, data);
			setGUIDataUnchanged();
			return SaveStateEnum.YES;
		}
		// if the user pressed cancel on the fileChooser
		return SaveStateEnum.CANCEL;
	}

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

	public static void showError(String title, String content) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText(title);
		errorAlert.setContentText(content);
		errorAlert.showAndWait();
	}

	public static SaveStateEnum saveDataBeforeExitConfirmation() {

		Alert saveBeforeExitConfirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"You have unsaved changes.\nWould you like to save them now?");
		saveBeforeExitConfirmation.setHeaderText("Save unsaved changes");
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

	public static String fileChooser(boolean save, ExtensionFilter extensions) {
		Settings settings = App.easyDI.getInstance(Settings.class);
		return fileChooser(save, extensions, settings.getLastFileChooserFolderProperty());
	}

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

	public static void saveJSONStringToFile(String filepath, String data) {
		try (FileWriter fileWriter = new FileWriter(filepath)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException e) {
			showError("Error while saving the file", e.getLocalizedMessage());
		}

	}

	public static Image getIcon() {
		return new Image(App.class.getResource("images/Icon.png").toExternalForm());
	}

	public static String getStyleSheetPath() {
		return App.class.getResource("styles/style.css").toExternalForm();
	}

	public static Set<Class<?>> getClassesFromJarFile(File jarFile)
			throws IOException, ClassNotFoundException, NoClassDefFoundError {
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
			}
		}
		return classNames;
	}

	private static JSONObject getToSavedData() {
		JSONObject savedData = new JSONObject();
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			savedData.put(storableGuiDataClass.getSimpleName(), storableGuiData.getGUIData());

		}
		return savedData;
	}

	private static void setLoadedData(JSONObject guiData) {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			JSONObject data = guiData.optJSONObject(storableGuiDataClass.getSimpleName());
			if (data != null) {
				IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
				storableGuiData.setGUIData(data);
			}
		}
	}

	private static boolean hasGUIDataChanged() {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			if (storableGuiData.hasChanged()) {
				return true;
			}
		}
		return false;
	}

	private static void setGUIDataUnchanged() {
		for (Class<? extends IStorableGuiData> storableGuiDataClass : getIStorableGuiDataClasses()) {
			IStorableGuiData storableGuiData = App.easyDI.getInstance(storableGuiDataClass);
			storableGuiData.setUnchanged();
		}
	}

	private static Set<Class<? extends IStorableGuiData>> getIStorableGuiDataClasses() {
		Reflections reflections = new Reflections("de.dai_labor.conversation_engine_gui");

		return reflections.getSubTypesOf(IStorableGuiData.class);
	}
}
