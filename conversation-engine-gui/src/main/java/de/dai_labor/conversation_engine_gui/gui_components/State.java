package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
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
	private TextArea nameTextArea = null;
	private final DragElementData dragElementData = new DragElementData();
	private final int stateID;
	private final Settings settings;
	private SimpleObjectProperty<State> selectedState;

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
		this.selectedState.set(this);
		this.toFront();
		if (event.getClickCount() == 2) {
			this.focusLabel();
			event.consume();
		}
		this.dragElementData.mouseX = event.getScreenX();
		this.dragElementData.mouseY = event.getScreenY();
		this.dragElementData.translateX = this.getTranslateX();
		this.dragElementData.translateY = this.getTranslateY();
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

	public State(int stateID, String name, double x, double y, Settings settings,
			SimpleObjectProperty<State> selectedState, boolean requestFocus) {
		this.settings = settings;
		this.stateID = stateID;
		this.selectedState = selectedState;
		this.selectedState.addListener((observable, oldVal, newVal) -> this.selectStatus(newVal));
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
		this.addDraggingEventHandlers();
		this.relocate(x, y);
		this.setFocusTraversable(true);
		if (requestFocus) {
			this.initFocusRequest();
			this.selectedState.set(this);
		}
	}

	public void selectStatus(State newVal) {
		if (newVal != null && newVal.equals(this)) {
			this.stateShape.fillProperty().bind(this.settings.getStateSelectedColorProperty());
		} else {
			this.nameTextArea.deselect();
			this.requestFocus();
			this.stateShape.fillProperty().bind(this.settings.getStateNormalColorProperty());
		}
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

	private void focusLabel() {
		this.nameTextArea.requestFocus();
		this.nameTextArea.selectAll();
	}

	private void initFocusRequest() {
		Platform.runLater(() -> {
			if (this.selectedState.get().equals(this)) {
				this.focusLabel();
			}
		});
	}

	private void addDynamicEventListeners() {
		this.nameTextArea.maxHeightProperty().bind(this.settings.getStateSizeProperty().multiply(2));
		this.nameTextArea.maxWidthProperty().bind(this.settings.getStateSizeProperty().multiply(2));
		this.stateShape.radiusProperty().bind(this.settings.getStateSizeProperty());
		this.nameTextArea.fontProperty()
				.bind(Bindings.createObjectBinding(() -> new Font(this.settings.getStateFontSizeProperty().get()),
						this.settings.getStateFontSizeProperty()));
		this.nameTextArea.styleProperty().bind(Bindings.createObjectBinding(
				() -> "-fx-text-fill: " + this.settings.getStateFontColorProperty().get().toString().replace("0x", "#"),
				this.settings.getStateFontColorProperty()));

	}

	private void addDraggingEventHandlers() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
		this.nameTextArea.addEventHandler(KeyEvent.KEY_PRESSED, this.labelKeyEvent);
	}

}
