package de.dai_labor.dialog_modeling_tool.models;

/**
 * The selected action of a user for a to be saved file.
 *
 * @author Marcel Engelmann
 *
 */
public enum SaveStateEnum {
	/**
	 * The user wants to save the file.
	 */
	YES,
	/**
	 * The user does not want to save the file.
	 */
	NO,
	/**
	 * The user canceled saving the file.
	 */
	CANCEL
}
