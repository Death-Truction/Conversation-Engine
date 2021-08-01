package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class State extends StackPane {

	public final static double INITIAL_SIZE = 80;
	private final static double INITIAL_ARC_SIZE = 15;
	private final static Color STATE_SHAPE_COLOR = Color.STEELBLUE;

	private Rectangle stateShape;
	private Label nameLabel;
	private DragElementData dragElementData = new DragElementData();

	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.setCursor(Cursor.MOVE);
		dragElementData.mouseX = event.getScreenX();
		dragElementData.mouseY = event.getScreenY();
		dragElementData.translateX = this.getTranslateX();
		dragElementData.translateY = this.getTranslateY();
		this.toFront();
	};

	private EventHandler<MouseEvent> mouseDraggedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		double scale = this.getParent().getScaleX();
		double xDifference = (event.getScreenX() - dragElementData.mouseX) / scale + dragElementData.translateX;
		double yDifference = (event.getScreenY() - dragElementData.mouseY) / scale + dragElementData.translateY;
		this.setTranslateX(xDifference);
		this.setTranslateY(yDifference);
	};

	private EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {
		if (!event.isStillSincePress()) {
			this.setCursor(Cursor.DEFAULT);
		}
	};

	public State(String name, double x, double y) {
		this.stateShape = new Rectangle(INITIAL_SIZE, INITIAL_SIZE);
		this.stateShape.setArcHeight(INITIAL_ARC_SIZE);
		this.stateShape.setArcWidth(INITIAL_ARC_SIZE);
		this.stateShape.setFill(STATE_SHAPE_COLOR);
		this.nameLabel = new Label(name);
		this.getChildren().addAll(this.stateShape, this.nameLabel);
		this.addEventHandlers(this);
		this.relocate(x, y);
	}

	public String getName() {
		return this.nameLabel.getText();
	}

	public void setName(String name) {
		this.nameLabel.setText(name);
	}

	private void addEventHandlers(Node node) {
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
	}

}
