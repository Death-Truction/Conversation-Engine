package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class State extends StackPane {

	private final Circle stateShape;
	private final TextArea nameTextArea;
	private final DragElementData dragElementData = new DragElementData();
	private final int stateID;
	private final Settings settings;

	private final EventHandler<KeyEvent> labelKeyEvent = event -> {
		if (event.getCode().equals(KeyCode.ENTER)) {
			this.requestFocus();
			event.consume();
		}
	};

	private final EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.dragElementData.mouseX = event.getScreenX();
		this.dragElementData.mouseY = event.getScreenY();
		this.dragElementData.translateX = this.getTranslateX();
		this.dragElementData.translateY = this.getTranslateY();
		this.toFront();
		event.consume();
	};

	private final EventHandler<MouseEvent> mouseDraggedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.setCursor(Cursor.MOVE);
		final double scale = this.getParent().getScaleX();
		final double xDifference = (event.getScreenX() - this.dragElementData.mouseX) / scale
				+ this.dragElementData.translateX;
		final double yDifference = (event.getScreenY() - this.dragElementData.mouseY) / scale
				+ this.dragElementData.translateY;
		this.setTranslateX(xDifference);
		this.setTranslateY(yDifference);
		event.consume();
	};

	private final EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {
		if (!event.isStillSincePress()) {
			this.setCursor(Cursor.DEFAULT);
		}
		event.consume();
	};

	public State(int stateID, String name, double x, double y, boolean draggable, Settings settings) {
		this.settings = settings;
		this.stateID = stateID;
		int stateSize = settings.getStateSizeProperty().get();
		int stateFontSize = settings.getStateFontSizeProperty().get();
		Color stateFontColor = settings.getStateFontColorProperty().get();
		Color stateNormalColor = settings.getStateNormalColorProperty().get();
		this.stateShape = new Circle(stateSize, stateNormalColor);
		this.stateShape.setMouseTransparent(true);
		this.nameTextArea = new TextArea(name);
		this.nameTextArea.setStyle("-fx-text-fill: " + stateFontColor.toString().replace("0x", "#"));
		this.nameTextArea.setFont(new Font(stateFontSize));
		this.nameTextArea.setMaxSize(stateSize * 2.0, stateSize * 2.0);
		this.nameTextArea.getStyleClass().add("stateTextArea");
		this.nameTextArea.setMouseTransparent(true);
		this.getChildren().addAll(this.stateShape, this.nameTextArea);
		this.addDynamicEventListeners();
		if (draggable) {
			this.addDraggingEventHandlers();
		}
		this.relocate(x, y);
	}

	public void select() {
		this.stateShape.setFill(this.settings.getStateSelectedColorProperty().get());
	}

	public void unselect() {
		this.stateShape.setFill(this.settings.getStateNormalColorProperty().get());
	}

	public TextArea getTextArea() {
		return this.nameTextArea;
	}

	public String getName() {
		return this.nameTextArea.getText();
	}

	public int getStateID() {
		return this.stateID;
	}

	public void focusLabel() {
		this.nameTextArea.requestFocus();
		this.nameTextArea.selectAll();
	}

	private void addDynamicEventListeners() {
		this.settings.getStateSizeProperty().addListener((ChangeListener<? super Number>) (observ, oldVal, newVal) -> {
			final double value = (int) newVal;
			this.stateShape.setRadius(value);
			this.nameTextArea.setMaxSize(value * 2.0, value * 2.0);
		});
		this.settings.getStateFontSizeProperty()
				.addListener((ChangeListener<? super Number>) (observ, oldVal, newVal) -> {
					final double value = (int) newVal;
					this.nameTextArea.setFont(new Font(value));
				});
		this.settings.getStateFontColorProperty()
				.addListener((ChangeListener<? super Color>) (observ, oldVal, newVal) -> {
					this.nameTextArea.setStyle("-fx-text-fill: " + newVal.toString().replace("0x", "#"));
				});
		this.settings.getStateNormalColorProperty()
				.addListener((ChangeListener<? super Color>) (observ, oldVal, newVal) -> {
					this.stateShape.setFill(newVal);
				});

	}

	private void addDraggingEventHandlers() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
		this.nameTextArea.addEventHandler(KeyEvent.KEY_PRESSED, this.labelKeyEvent);
	}

}
