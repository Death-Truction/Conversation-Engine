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
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

public class State extends StackPane {

	private final Circle stateShape;
	private Arc specialStartShape;
	private StackPane specialEndShape;
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
		this.createSpecialShapes();
		this.getChildren().addAll(this.stateShape, this.specialStartShape, this.specialEndShape, this.nameTextArea);
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

	public void select() {
		this.selectStatus(this);
	}

	public void deselect() {
		this.selectStatus(null);
	}

	public TextArea getTextArea() {
		return this.nameTextArea;
	}

	public String getName() {
		return this.nameTextArea.getText();
	}

	public int getStateId() {
		return this.stateID;
	}

	public void setStartState() {
		this.specialEndShape.setVisible(false);
		this.specialStartShape.setVisible(true);
	}

	public void setEndState() {
		this.specialStartShape.setVisible(false);
		this.specialEndShape.setVisible(true);
	}

	public void setNormalState() {
		this.specialStartShape.setVisible(false);
		this.specialEndShape.setVisible(false);
	}

	@Override
	public String toString() {
		return this.getName();
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
		this.stateShape.fillProperty().bind(this.settings.getStateNormalColorProperty());
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

	private void createSpecialShapes() {
		this.createSpecialStartShape();
		this.createSpecialEndShape();
	}

	private void createSpecialStartShape() {
		this.specialStartShape = new Arc(0, 0, 0, 0, 0, 360);
		this.specialStartShape.radiusXProperty().bind(
				this.stateShape.radiusProperty().subtract(6).subtract(this.specialStartShape.strokeWidthProperty()));
		this.specialStartShape.radiusYProperty().bind(
				this.stateShape.radiusProperty().subtract(6).subtract(this.specialStartShape.strokeWidthProperty()));
		this.specialStartShape.setStrokeType(StrokeType.OUTSIDE);
		this.specialStartShape.strokeWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			if (this.stateShape.getRadius() <= 40) {
				return 2.0;
			}
			return this.stateShape.getRadius() / 20.0;
		}, this.stateShape.radiusProperty()));
		this.specialStartShape.setStroke(Color.WHITE);
		this.specialStartShape.setStrokeLineCap(StrokeLineCap.BUTT);
		this.specialStartShape.setFill(null);
		this.specialStartShape.setVisible(false);
		this.specialStartShape.setMouseTransparent(true);
	}

	private void createSpecialEndShape() {
		Line line1 = new Line();
		line1.setStroke(Color.WHITE);
		line1.setStrokeWidth(4);
		line1.setStartX(0);
		line1.setStartY(0);
		line1.endXProperty().bind(this.stateShape.radiusProperty());
		line1.endYProperty().bind(this.stateShape.radiusProperty());
		Line line2 = new Line();
		line2.setStroke(Color.WHITE);
		line2.setStrokeWidth(4);
		line2.startXProperty().bind(this.stateShape.radiusProperty().multiply(2));
		line2.setStartY(0);
		line2.endXProperty().bind(this.stateShape.radiusProperty());
		line2.endYProperty().bind(this.stateShape.radiusProperty());
		this.specialEndShape = new StackPane();
		this.specialEndShape.getChildren().addAll(line1, line2);
		this.specialEndShape.setVisible(false);
		this.specialEndShape.setMouseTransparent(true);
	}

}
