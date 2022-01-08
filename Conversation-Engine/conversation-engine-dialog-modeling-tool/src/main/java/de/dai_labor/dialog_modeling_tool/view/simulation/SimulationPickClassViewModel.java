package de.dai_labor.dialog_modeling_tool.view.simulation;

import java.util.Set;

import de.dai_labor.dialog_modeling_tool.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The ViewModel of the {@link SimulationPickClassView}
 *
 * @author Marcel Engelmann
 *
 */
public class SimulationPickClassViewModel implements ViewModel {
	private ObservableList<Class<?>> classes = FXCollections.observableArrayList();
	private SimpleObjectProperty<Class<?>> selectedClassProperty = new SimpleObjectProperty<>();
	private Class<?> selectedClass = null;

	/**
	 * Gets an {@link ObservableList} of all available classes
	 *
	 * @return all available classes
	 */
	public ObservableList<Class<?>> getAllClasses() {
		return this.classes;
	}

	/**
	 * Gets the {@link Property} of the selected class
	 *
	 * @return the {@link Property} of the selected class
	 */
	public SimpleObjectProperty<Class<?>> getSelectedClassProperty() {
		return this.selectedClassProperty;
	}

	/**
	 * Adds classes to the list of available classes
	 *
	 * @param allClasses The classes to be added
	 */
	public void addClasses(Set<Class<?>> allClasses) {
		this.classes.addAll(allClasses);
	}

	/**
	 * Gets the user selected class
	 *
	 * @return the user selected class or null if the user canceled the process
	 */
	public Class<?> getSelectedClass() {
		return this.selectedClass;
	}

	/**
	 * Stores the selected class or informs the user that no class has been
	 * selected.
	 *
	 * If a valid class has been selected the window will be closed
	 */
	public void okButtonPressed() {
		this.selectedClass = this.selectedClassProperty.get();
		if (this.selectedClass == null) {
			Util.showError("No class selected", "Please select a class from the list or cancel the process.");
			return;
		}
		this.closeStage();
	}

	/**
	 * Closes the window
	 */
	public void cancelButtonPressed() {
		this.closeStage();
	}

	/**
	 * Notifies the owner that subscribed the event 'pickClassCloseRequest' to close
	 * the window
	 */
	private void closeStage() {
		this.publish("pickClassCloseRequest");
	}

}
