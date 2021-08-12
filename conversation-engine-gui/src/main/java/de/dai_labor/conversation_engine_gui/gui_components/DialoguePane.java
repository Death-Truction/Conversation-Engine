package de.dai_labor.conversation_engine_gui.gui_components;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.inject.Singleton;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

@Singleton
public class DialoguePane extends Pane {

	private DragElementData dragElementData = new DragElementData();
	private Pane dialogueModelDataLayer;
	private static final double MIN_ZOOM_LEVEL = 0.2;
	private static final double MAX_ZOOM_LEVEL = 3.0;
	private BiConsumer<Double, Double> addState;
	private BiConsumer<State, State> addTransition;
	private Consumer<State> removeState;
	private Consumer<Transition> removeTransition;
	private SimpleStringProperty insertMode;
	private State sourceTransitionState;
	private State targetTransitionState;
	private State selectedState;
	private Transition selectedTransition;
	private Arrow dragArrow;

	public DialoguePane(Pane dialogModelDataLayer, SimpleStringProperty insertMode, BiConsumer<Double, Double> addState,
			BiConsumer<State, State> addTransition, Consumer<State> removeState,
			Consumer<Transition> removeTransition) {
		this.dialogueModelDataLayer = dialogModelDataLayer;
		this.addState = addState;
		this.addTransition = addTransition;
		this.removeState = removeState;
		this.removeTransition = removeTransition;
		this.insertMode = insertMode;
		this.getChildren().add(0, this.dialogueModelDataLayer);
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventFilter);
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventFilter);
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventFilter);
		this.addEventFilter(MouseEvent.ANY, this.mouseEventFilter);
		this.addEventFilter(ScrollEvent.ANY, this.mouseScrollEventFilter);
		this.addEventFilter(KeyEvent.ANY, this.keyEventFilter);
	}

	public void centerMovingElement() {
		double targetX = 0.0;
		double targetY = 0.0;
		dialogueModelDataLayer.setScaleX(1.0);
		dialogueModelDataLayer.setScaleY(1.0);
		this.dialogueModelDataLayer.setTranslateX(0);
		this.dialogueModelDataLayer.setTranslateY(0);
		for (Node state : this.dialogueModelDataLayer.getChildren()) {
			Bounds bounds = state.getBoundsInParent();
			targetX += bounds.getCenterX();
			targetY += bounds.getCenterY();
		}
		targetX /= this.dialogueModelDataLayer.getChildren().size();
		targetY /= this.dialogueModelDataLayer.getChildren().size();
		targetX = (this.getWidth() / 2) + (this.getScene().getWidth() / 2) - targetX;
		targetY = (this.getHeight() / 2) + (this.getScene().getHeight() / 2) - targetY;
		this.dialogueModelDataLayer.relocate(targetX, targetY);
	}

	// filter all MouseEvent types
	private EventHandler<MouseEvent> mouseEventFilter = event -> {
		// if any toggleButton is active -> do not allow the State children to fire the
		// event. Instead redirect the event to this Pane
		if (this.insertMode.getValue().equals("addState") && isStateTarget(event)) {
			event.consume();
			Event.fireEvent(this, event);
		}
	};

	private EventHandler<KeyEvent> keyEventFilter = event -> {
		if (event.getCode() == KeyCode.DELETE) {
			if (this.selectedState != null) {
				this.removeState.accept(this.selectedState);
				this.selectedState = null;
			}
			if (this.selectedTransition != null) {
				this.removeTransition.accept(this.selectedTransition);
				this.selectedTransition = null;
			}
		}
	};

	private EventHandler<MouseEvent> mousePressedEventFilter = event -> {
		if (event.isPrimaryButtonDown()) {
			this.requestFocus();
			if (this.selectedState != null) {
				this.selectedState.unselect();
				this.selectedState = null;
			}
			if (this.selectedTransition != null) {
				this.selectedTransition.unselect();
				this.selectedTransition = null;
			}
			if (isStateTarget(event) && this.insertMode.get().equals("")) {
				State eventTarget = (State) event.getPickResult().getIntersectedNode();
				eventTarget.select();
				this.selectedState = eventTarget;
			}
			if (isTransitionTarget(event) && this.insertMode.get().equals("")) {
				Transition eventTarget = (Transition) event.getPickResult().getIntersectedNode();
				eventTarget.select();
				this.selectedTransition = eventTarget;
			}
		}
		if (event.isSecondaryButtonDown()) {
			dragElementData.mouseX = event.getScreenX();
			dragElementData.mouseY = event.getScreenY();
			dragElementData.translateX = dialogueModelDataLayer.getTranslateX();
			dragElementData.translateY = dialogueModelDataLayer.getTranslateY();
			event.consume();
		} else if (this.insertMode.get().equals("addTransition") && isStateTarget(event)) {
			this.sourceTransitionState = (State) event.getPickResult().getIntersectedNode();
			event.consume();
		} else if (this.insertMode.get().isBlank() && isStateTarget(event) && event.getClickCount() == 2) {
			State eventTarget = (State) event.getPickResult().getIntersectedNode();
			eventTarget.focusLabel();
			event.consume();
		} else if (this.insertMode.get().isBlank() && isTransitionTarget(event) && event.getClickCount() == 2) {
			Transition eventTarget = (Transition) event.getPickResult().getIntersectedNode();
			eventTarget.focusTextField();
			event.consume();
		}

	};

	private EventHandler<MouseEvent> mouseDraggedEventFilter = event -> {
		if (event.isSecondaryButtonDown()) {
			this.setCursor(Cursor.MOVE);
			double scale = 1.0;
			double xDifference = (event.getScreenX() - dragElementData.mouseX) / scale + dragElementData.translateX;
			double yDifference = (event.getScreenY() - dragElementData.mouseY) / scale + dragElementData.translateY;
			this.dialogueModelDataLayer.setTranslateX(xDifference);
			this.dialogueModelDataLayer.setTranslateY(yDifference);
			event.consume();
		} else if (this.insertMode.get().equals("addTransition")) {
			this.dialogueModelDataLayer.getChildren().remove(this.dragArrow);
			Double scale = this.dialogueModelDataLayer.getScaleX();
			StackPane tmpPane = new StackPane();
			tmpPane.setTranslateX(event.getX() - this.dialogueModelDataLayer.getBoundsInParent().getMinX());
			tmpPane.setTranslateY(event.getY() - this.dialogueModelDataLayer.getBoundsInParent().getMinY());
			this.dragArrow = new Arrow(sourceTransitionState, tmpPane, this.dialogueModelDataLayer.getScaleX());
			this.dragArrow.setMouseTransparent(true);
			this.dialogueModelDataLayer.getChildren().add(this.dragArrow);
			event.consume();
		}
	};

	private EventHandler<MouseEvent> mouseReleasedEventFilter = event -> {
		this.dialogueModelDataLayer.getChildren().remove(this.dragArrow);
		if (event.getButton() == MouseButton.SECONDARY) {
			this.setCursor(Cursor.DEFAULT);
			event.consume();
		} else if (event.getButton() == MouseButton.PRIMARY) {
			if (this.insertMode.get().equals("addState")) {
				Double scale = this.dialogueModelDataLayer.getScaleX();
				double x = (event.getX() - this.dialogueModelDataLayer.getBoundsInParent().getMinX()) / scale;
				double y = (event.getY() - this.dialogueModelDataLayer.getBoundsInParent().getMinY()) / scale;
				addState.accept(x, y);
			} else if (this.insertMode.get().equals("addTransition") && isStateTarget(event)) {
				this.targetTransitionState = (State) event.getPickResult().getIntersectedNode();
				this.addTransition.accept(this.sourceTransitionState, this.targetTransitionState);
				event.consume();
			}
		}
	};

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private EventHandler<ScrollEvent> mouseScrollEventFilter = event -> {
		double zoomFactor = 1.2;

		double scale = dialogueModelDataLayer.scaleXProperty().get();
		double oldScale = scale;

		if (event.getDeltaY() < 0)
			scale /= zoomFactor;
		else
			scale *= zoomFactor;

		scale = clamp(scale, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);

		double f = (scale / oldScale) - 1;

		double dx = (event.getX() - (dialogueModelDataLayer.getBoundsInParent().getWidth() / 2
				+ dialogueModelDataLayer.getBoundsInParent().getMinX()));
		double dy = (event.getY() - (dialogueModelDataLayer.getBoundsInParent().getHeight() / 2
				+ dialogueModelDataLayer.getBoundsInParent().getMinY()));

		dialogueModelDataLayer.setScaleX(scale);
		dialogueModelDataLayer.setScaleY(scale);

		// note: pivot value must be untransformed, i. e. without scaling
		this.setPivot(f * dx, f * dy);

		// resize to fit parent
		event.consume();
	};

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private void setPivot(double x, double y) {
		dialogueModelDataLayer.setTranslateX(dialogueModelDataLayer.getTranslateX() - x);
		dialogueModelDataLayer.setTranslateY(dialogueModelDataLayer.getTranslateY() - y);
	}

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private static double clamp(double value, double min, double max) {

		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}

	private boolean isStateTarget(MouseEvent event) {
		Class<? extends Node> eventTargetClass = event.getPickResult().getIntersectedNode().getClass();
		if (eventTargetClass.equals(State.class)) {
			return true;
		}
		return false;
	}

	private boolean isTransitionTarget(MouseEvent event) {
		Class<? extends Node> eventTargetClass = event.getPickResult().getIntersectedNode().getClass();
		if (eventTargetClass.equals(Transition.class)) {
			return true;
		}
		return false;
	}

}