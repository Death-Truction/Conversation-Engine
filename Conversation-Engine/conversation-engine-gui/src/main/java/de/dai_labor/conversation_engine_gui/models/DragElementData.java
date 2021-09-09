package de.dai_labor.conversation_engine_gui.models;

/**
 * Stores the data for a dragging event
 *
 * @author Marcel Engelmann
 *
 */
public class DragElementData {
	private double mouseX;
	private double mouseY;
	private double translateX;
	private double translateY;

	/**
	 * Create a new {@link DragElementData} object
	 *
	 * @param mouseX     The mouseX position
	 * @param mouseY     The mouseY position
	 * @param translateX The translateX value
	 * @param translateY The translateY value
	 */
	public DragElementData(double mouseX, double mouseY, double translateX, double translateY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.translateX = translateX;
		this.translateY = translateY;
	}

	/**
	 * Gets the mouseX value.
	 *
	 * @return the mouseX value.
	 */
	public double getMouseX() {
		return this.mouseX;
	}

	/**
	 * Gets the mouseY value.
	 *
	 * @return the mouseY value.
	 */
	public double getMouseY() {
		return this.mouseY;
	}

	/**
	 * Gets the translateX value.
	 *
	 * @return the translateX value.
	 */
	public double getTranslateX() {
		return this.translateX;
	}

	/**
	 * Gets the translateY value.
	 *
	 * @return the translateY value.
	 */
	public double getTranslateY() {
		return this.translateY;
	}
}
