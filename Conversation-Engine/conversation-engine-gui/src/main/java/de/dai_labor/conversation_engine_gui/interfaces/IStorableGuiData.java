package de.dai_labor.conversation_engine_gui.interfaces;

import org.json.JSONObject;

/**
 * An interface for all classes that contain GUI data that are supposed to be
 * included in a saved file
 *
 * @author Marcel Engelmann
 *
 */
public interface IStorableGuiData {
	/**
	 * Gets the current GUI data of the class object. When the saved file is loaded,
	 * the same key/value pairs will be restored.
	 *
	 * @return the current GUI data of the class object.
	 */
	public JSONObject getGUIData();

	/**
	 * Sets the given GUI data from a saved file. The same key/value pairs of the
	 * {@link #getGUIData()} method will be present.
	 *
	 * @param data the given GUI data from a saved file.
	 */
	public void setGUIData(JSONObject data);

	/**
	 * Resets all the GUI data to default
	 */
	public void resetData();

	/**
	 * Checks whether the GUI data has been changed. This will determine if there is
	 * new data that needs to be saved.
	 *
	 * @return true if the GUI data has been changed.
	 */
	public boolean hasChanged();

	/**
	 * Sets the current GUI data as unchanged. If the user saved the current
	 * progress the GUI data must be handled as if it was not changed
	 */
	public void setUnchanged();
}
