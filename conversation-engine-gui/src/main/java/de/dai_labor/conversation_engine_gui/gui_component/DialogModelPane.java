package de.dai_labor.conversation_engine_gui.gui_component;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DialogModelPane extends Pane {

	private DragElementData dragElementData = new DragElementData();
	private Node movingElement;

	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isSecondaryButtonDown()) {
			return;
		}
		dragElementData.x = movingElement.getBoundsInParent().getMinX() - event.getScreenX();
		dragElementData.y = movingElement.getBoundsInParent().getMinY() - event.getScreenY();
	};

	private EventHandler<MouseEvent> mouseDraggedEventHandler = event -> {
		if (!event.isSecondaryButtonDown()) {
			return;
		}
		this.setCursor(Cursor.MOVE);
		double xDifference = event.getScreenX() + dragElementData.x;
		double yDifference = event.getScreenY() + dragElementData.y;
		this.movingElement.relocate(xDifference, yDifference);
	};

	private EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {
		this.setCursor(Cursor.DEFAULT);
	};

	public DialogModelPane(Node movingElement) {
		this.setScale(1.0);
		this.movingElement = movingElement;
		this.getChildren().add(0, movingElement);
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
	}

	public double getScale() {
		return this.scaleXProperty().get();
	}

	public void setScale(double scale) {
		this.scaleXProperty().set(scale);
		this.scaleYProperty().set(scale);
	}

}