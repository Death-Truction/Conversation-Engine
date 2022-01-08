package de.dai_labor.dialog_modeling_tool.gui_components;

import de.dai_labor.dialog_modeling_tool.models.DragElementData;
import de.dai_labor.dialog_modeling_tool.models.Settings;
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

/**
 * The State class represents a State visually
 *
 * @author Marcel Engelmann
 *
 */
public class State extends StackPane {

	private final Circle stateShape;
	private Arc specialStartShape;
	private StackPane specialEndShape;
	private TextArea nameTextArea = null;
	private DragElementData dragElementData;
	private final int stateID;
	private final Settings settings;
	private SimpleObjectProperty<State> selectedState;

	/**
	 * EventHandler that handles the key released event.
	 *
	 * It removes the focus of the {@link State}'s {@link #nameTextArea}
	 */
	private final EventHandler<KeyEvent> keyReleasedEventHandler = event -> {
		if (event.getCode().equals(KeyCode.ENTER)) {
			this.requestFocus();
			event.consume();
		}
	};

	/**
	 * EventHandler that handles the mouse pressed event.
	 *
	 * It visually selects this {@link State} and focuses the {@link #nameTextArea}
	 * on a double click. It also saves the required variables for the
	 * {@link #mouseDraggedEventHandler mouse dragging event}
	 */
	private final EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.selectedState.set(this);
		this.toFront();
		if (event.getClickCount() == 2) {
			this.focusNameTextArea();
			event.consume();
		}
		this.dragElementData = new DragElementData(event.getScreenX(), event.getScreenY(), this.getTranslateX(),
				this.getTranslateY());
		event.consume();
	};

	/**
	 * EventHandler that handles the mouse dragged event.
	 *
	 * It calculates the new Position of the {@link State} to enable a dragging
	 * feature
	 */
	private final EventHandler<MouseEvent> mouseDraggedEventHandler = event -> {
		if (!event.isPrimaryButtonDown()) {
			return;
		}
		this.setCursor(Cursor.MOVE);
		final double scale = this.getParent().getScaleX();
		final double xDifference = (event.getScreenX() - this.dragElementData.getMouseX()) / scale
				+ this.dragElementData.getTranslateX();
		final double yDifference = (event.getScreenY() - this.dragElementData.getMouseY()) / scale
				+ this.dragElementData.getTranslateY();
		this.setTranslateX(xDifference);
		this.setTranslateY(yDifference);
		event.consume();
	};

	/**
	 * EventHandler that handles the mouse released event.
	 *
	 * It resets the mouse cursor
	 */
	private final EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {
		if (!event.isStillSincePress()) {
			this.setCursor(Cursor.DEFAULT);
		}
		event.consume();
	};

	/**
	 * Creates a new {@link State}
	 *
	 * @param stateID       The ID of the {@link State}
	 * @param name          The name of the {@link State}
	 * @param x             The x-coordinate of the new {@link State}
	 * @param y             The y-coordinate of the new {@link State}
	 * @param settings      The Instance of the Settings object
	 * @param selectedState The {@link SimpleObjectProperty} of the currently
	 *                      selected {@link State}
	 * @param requestFocus  Whether the new {@link State} should be selected after
	 *                      creating it
	 */
	public State(int stateID, String name, double x, double y, Settings settings,
			SimpleObjectProperty<State> selectedState, boolean requestFocus) {
		this.settings = settings;
		this.stateID = stateID;
		this.selectedState = selectedState;
		this.selectedState.addListener((observable, oldVal, newVal) -> {
			if (newVal != null && newVal.equals(this)) {
				this.setSelectedProperties();
			} else {
				this.removeSelectedProperties();
			}
		});
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
		if (requestFocus) {
			this.initFocusRequest();
			this.selectedState.set(this);
		}
	}

	/**
	 * Selects this {@link State}
	 */
	public void select() {
		this.selectedState.set(this);
	}

	/**
	 * Deselects this {@link State}
	 */
	public void deselect() {
		this.selectedState.set(this);
	}

	/**
	 * Gets the TextArea of the {@link State} that displays the {@link State}'s name
	 *
	 * @return the TextArea of the {@link State} that displays the {@link State}'s
	 *         name
	 */
	public TextArea getTextArea() {
		return this.nameTextArea;
	}

	/**
	 * Gets the name of the {@link State}
	 *
	 * @return the name of the {@link State}
	 */
	public String getName() {
		return this.nameTextArea.getText();
	}

	/**
	 * Gets the ID of the {@link State}
	 *
	 * @return the ID of the {@link State}
	 */
	public int getStateId() {
		return this.stateID;
	}

	/**
	 * Sets this {@link State} to be the start state
	 */
	public void setStartState() {
		this.specialEndShape.setVisible(false);
		this.specialStartShape.setVisible(true);
	}

	/**
	 * Sets this {@link State} to be the end state
	 */
	public void setEndState() {
		this.specialStartShape.setVisible(false);
		this.specialEndShape.setVisible(true);
	}

	/**
	 * Sets this {@link State} to be a normal state
	 */
	public void setNormalState() {
		this.specialStartShape.setVisible(false);
		this.specialEndShape.setVisible(false);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * Changes the color of the {@link State} to the defined
	 * {@link Settings#getStateSelectedColorProperty() Color}
	 */
	private void setSelectedProperties() {
		this.stateShape.fillProperty().bind(this.settings.getStateSelectedColorProperty());
	}

	/**
	 * Removes the {@link #setSelectedProperties() selected properties} of this
	 * {@link State} and removes the focus of the {@link #nameTextArea}
	 */
	private void removeSelectedProperties() {
		this.nameTextArea.deselect();
		this.requestFocus();
		this.stateShape.fillProperty().bind(this.settings.getStateNormalColorProperty());
	}

	/**
	 * Focuses the {@link #nameTextArea}
	 */
	private void focusNameTextArea() {
		this.nameTextArea.requestFocus();
		this.nameTextArea.selectAll();
	}

	/**
	 * Focuses this {@link State} if it is still the selected State in the next tick
	 */
	private void initFocusRequest() {
		Platform.runLater(() -> {
			if (this.selectedState.get().equals(this)) {
				this.focusNameTextArea();
			}
		});
	}

	/**
	 * Adds the Properties to the {@link State} elements, defined inside the
	 * {@link Settings}
	 */
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

	/**
	 * Adds the EventHanlders for the user input events
	 */
	private void addDraggingEventHandlers() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
		this.nameTextArea.addEventHandler(KeyEvent.KEY_RELEASED, this.keyReleasedEventHandler);
	}

	/**
	 * Creates the start and end Shapes for the {@link State} that are shown, when
	 * the state will be set as the start/end state
	 */
	private void createSpecialShapes() {
		this.createSpecialStartShape();
		this.createSpecialEndShape();
	}

	/**
	 * Creates an {@link Arc} to visually display the {@link State} as the start
	 * state
	 */
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

	/**
	 * Creates a cross with two {@link Line Lines} to visually display the
	 * {@link State} as the end state
	 */
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
