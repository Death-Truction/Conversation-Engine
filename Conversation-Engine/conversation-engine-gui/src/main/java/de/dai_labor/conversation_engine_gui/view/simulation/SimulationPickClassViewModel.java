package de.dai_labor.conversation_engine_gui.view.simulation;

import java.util.Set;

import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimulationPickClassViewModel implements ViewModel {
	private ObservableList<Class<?>> classNames = FXCollections.observableArrayList();
	private SimpleObjectProperty<Class<?>> selectedClassProperty = new SimpleObjectProperty();
	private Class<?> selectedClass = null;

	public ObservableList<Class<?>> getAllClassNames() {
		return this.classNames;
	}

	public SimpleObjectProperty<Class<?>> getSelectedClassProperty() {
		return this.selectedClassProperty;
	}

	public void addClasses(Set<Class<?>> allClasses) {
		this.classNames.addAll(allClasses);
	}

	public Class<?> getSelectedClass() {
		return this.selectedClass;
	}

	public void okButtonPressed() {
		this.selectedClass = this.selectedClassProperty.get();
		if (this.selectedClass == null) {
			Util.showError("No class selected", "Please select a class from the list or cancel the process.");
			return;
		}
		this.closeStage();
	}

	public void cancelButtonPressed() {
		this.closeStage();
	}

	private void closeStage() {
		this.publish("pickClassCloseRequest");
	}

}
