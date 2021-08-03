package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class State extends StackPane {

	public final static double INITIAL_SIZE = 40;
	private final static Color STATE_SHAPE_COLOR = Color.STEELBLUE;

	private Circle stateShape;
	private TextArea nameTextArea;
	private DragElementData dragElementData = new DragElementData();
	private int stateID;

	private EventHandler<KeyEvent> labelKeyEvent = event -> {
		if (event.getCode().equals(KeyCode.ENTER)) {
			this.requestFocus();
			event.consume();
		}
	};

	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		dragElementData.mouseX = event.getScreenX();
		dragElementData.mouseY = event.getScreenY();
		dragElementData.translateX = this.getTranslateX();
		dragElementData.translateY = this.getTranslateY();
		this.toFront();
		event.consume();
	};

	private EventHandler<MouseEvent> mouseDraggedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.setCursor(Cursor.MOVE);
		double scale = this.getParent().getScaleX();
		double xDifference = (event.getScreenX() - dragElementData.mouseX) / scale + dragElementData.translateX;
		double yDifference = (event.getScreenY() - dragElementData.mouseY) / scale + dragElementData.translateY;
		this.setTranslateX(xDifference);
		this.setTranslateY(yDifference);
		event.consume();
	};

	private EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {
		if (!event.isStillSincePress()) {
			this.setCursor(Cursor.DEFAULT);
		}
		event.consume();
	};

	public State(int stateID, String name, double x, double y) {
		this.stateID = stateID;
		this.stateShape = new Circle(INITIAL_SIZE);
		this.stateShape.setFill(STATE_SHAPE_COLOR);
		this.stateShape.setMouseTransparent(true);
		this.nameTextArea = new TextArea(name);
		this.nameTextArea.setMaxSize(INITIAL_SIZE * 2, INITIAL_SIZE * 2);
		this.nameTextArea.getStyleClass().add("stateTextArea");
		this.nameTextArea.setMouseTransparent(true);
		this.getChildren().addAll(this.stateShape, this.nameTextArea);
		this.addEventHandlers();
		this.relocate(x, y);
	}

	public TextArea getTextArea() {
		return this.nameTextArea;
	}

	public int getStateID() {
		return this.stateID;
	}

	public void focusLabel() {
		this.nameTextArea.requestFocus();
		this.nameTextArea.selectAll();
	}

	private void addEventHandlers() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
		this.nameTextArea.addEventHandler(KeyEvent.KEY_PRESSED, this.labelKeyEvent);
	}

}
