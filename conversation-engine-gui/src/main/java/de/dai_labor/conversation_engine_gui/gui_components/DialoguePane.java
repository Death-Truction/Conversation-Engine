package de.dai_labor.conversation_engine_gui.gui_components;

import java.util.function.BiConsumer;

import javax.inject.Singleton;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

@Singleton
public class DialoguePane extends Pane {

	private DragElementData dragElementData = new DragElementData();
	private Pane dialogModelDataLayer;
	private static final double MIN_ZOOM_LEVEL = 0.2;
	private static final double MAX_ZOOM_LEVEL = 3.0;
	private BiConsumer<Double, Double> addState;
	private SimpleStringProperty insertMode;

	public DialoguePane(Pane dialogModelDataLayer, BiConsumer<Double, Double> addState,
			SimpleStringProperty insertMode) {
		this.dialogModelDataLayer = dialogModelDataLayer;
		this.addState = addState;
		this.insertMode = insertMode;
		this.getChildren().add(0, this.dialogModelDataLayer);
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventFilter);
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventFilter);
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventFilter);
		this.addEventFilter(MouseEvent.ANY, this.mouseEventFilter);
		this.addEventFilter(ScrollEvent.ANY, this.mouseScrollEventFilter);
	}

	public void centerMovingElement() {
		double targetX = 0.0;
		double targetY = 0.0;
		for (Node state : this.dialogModelDataLayer.getChildren()) {
			Bounds bounds = state.getBoundsInParent();
			targetX += bounds.getCenterX();
			targetY += bounds.getCenterY();
		}
		targetX /= this.dialogModelDataLayer.getChildren().size();
		targetY /= this.dialogModelDataLayer.getChildren().size();
		this.dialogModelDataLayer.setTranslateX(0);
		this.dialogModelDataLayer.setTranslateY(0);
		this.dialogModelDataLayer.relocate(this.getWidth() / 2 - targetX, this.getHeight() / 2 - targetY);
		dialogModelDataLayer.setScaleX(1.0);
		dialogModelDataLayer.setScaleY(1.0);
	}

	// filter all MouseEvent types
	private EventHandler<MouseEvent> mouseEventFilter = event -> {
		// if any toggleButton is active -> do not allow the State children to fire the
		// event
		if (this.insertMode.getValue().equals("addState") && isStateTarget(event)) {
			event.consume();
			Event.fireEvent(this, event);
		}
	};

	private EventHandler<MouseEvent> mousePressedEventFilter = event -> {
		if (event.isSecondaryButtonDown()) {
			this.setCursor(Cursor.MOVE);
			dragElementData.mouseX = event.getScreenX();
			dragElementData.mouseY = event.getScreenY();
			dragElementData.translateX = dialogModelDataLayer.getTranslateX();
			dragElementData.translateY = dialogModelDataLayer.getTranslateY();
			event.consume();
		}

	};

	private EventHandler<MouseEvent> mouseDraggedEventFilter = event -> {
		if (event.isSecondaryButtonDown()) {
			double scale = 1.0;
			double xDifference = (event.getScreenX() - dragElementData.mouseX) / scale + dragElementData.translateX;
			double yDifference = (event.getScreenY() - dragElementData.mouseY) / scale + dragElementData.translateY;
			this.dialogModelDataLayer.setTranslateX(xDifference);
			this.dialogModelDataLayer.setTranslateY(yDifference);
			event.consume();
		}
	};

	private EventHandler<MouseEvent> mouseReleasedEventFilter = event -> {
		if (event.getButton() == MouseButton.SECONDARY) {
			this.setCursor(Cursor.DEFAULT);
			event.consume();
		} else if (event.getButton() == MouseButton.PRIMARY) {
			if (this.insertMode.get().equals("addState")) {
				double x = event.getX() - this.dialogModelDataLayer.getBoundsInParent().getMinX();
				double y = event.getY() - this.dialogModelDataLayer.getBoundsInParent().getMinY();
				addState.accept(x, y);
			}
		}
	};

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private EventHandler<ScrollEvent> mouseScrollEventFilter = event -> {
		double zoomFactor = 1.2;

		double scale = dialogModelDataLayer.scaleXProperty().get();
		double oldScale = scale;

		if (event.getDeltaY() < 0)
			scale /= zoomFactor;
		else
			scale *= zoomFactor;

		scale = clamp(scale, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);

		double f = (scale / oldScale) - 1;

		double dx = (event.getX() - (dialogModelDataLayer.getBoundsInParent().getWidth() / 2
				+ dialogModelDataLayer.getBoundsInParent().getMinX()));
		double dy = (event.getY() - (dialogModelDataLayer.getBoundsInParent().getHeight() / 2
				+ dialogModelDataLayer.getBoundsInParent().getMinY()));

		dialogModelDataLayer.setScaleX(scale);
		dialogModelDataLayer.setScaleY(scale);

		// note: pivot value must be untransformed, i. e. without scaling
		this.setPivot(f * dx, f * dy);

		// resize to fit parent
		event.consume();
	};

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private void setPivot(double x, double y) {
		dialogModelDataLayer.setTranslateX(dialogModelDataLayer.getTranslateX() - x);
		dialogModelDataLayer.setTranslateY(dialogModelDataLayer.getTranslateY() - y);
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
		Class<? extends EventTarget> eventTargetClass = event.getTarget().getClass();
		if (eventTargetClass.equals(Text.class) || eventTargetClass.equals(Rectangle.class)) {
			return true;
		}
		return false;
	}

}