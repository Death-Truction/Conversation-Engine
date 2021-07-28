package de.dai_labor.conversation_engine_gui.gui_component;

import de.dai_labor.conversation_engine_gui.models.DragElementData;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class DialogModelPane extends Pane {

	private DragElementData dragElementData = new DragElementData();
	private Node movingElement;
	private static final double MIN_ZOOM_LEVEL = 0.2;
	private static final double MAX_ZOOM_LEVEL = 3.0;

	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (!event.isSecondaryButtonDown()) {
			return;
		}
		dragElementData.x = movingElement.getBoundsInParent().getMinX() * this.movingElement.getScaleX()
				- event.getScreenX();
		dragElementData.y = movingElement.getBoundsInParent().getMinY() * this.movingElement.getScaleY()
				- event.getScreenY();
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

	private EventHandler<MouseEvent> mouseReleasedEventHandler = event -> this.setCursor(Cursor.DEFAULT);

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private EventHandler<ScrollEvent> mouseScrollEventHandler = event -> {
		double zoomFactor = 1.2;

		double scale = movingElement.scaleXProperty().get();
		double oldScale = scale;

		if (event.getDeltaY() < 0)
			scale /= zoomFactor;
		else
			scale *= zoomFactor;

		scale = clamp(scale, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);

		double f = (scale / oldScale) - 1;

		double dx = (event.getX()
				- (movingElement.getBoundsInParent().getWidth() / 2 + movingElement.getBoundsInParent().getMinX()));
		double dy = (event.getY()
				- (movingElement.getBoundsInParent().getHeight() / 2 + movingElement.getBoundsInParent().getMinY()));

		movingElement.setScaleX(scale);
		movingElement.setScaleY(scale);

		// note: pivot value must be untransformed, i. e. without scaling
		this.setPivot(f * dx, f * dy);

		// resize to fit parent

		event.consume();
	};

	// SOURCE:
	// https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer/29530135#29530135
	private void setPivot(double x, double y) {
		movingElement.setTranslateX(movingElement.getTranslateX() - x);
		movingElement.setTranslateY(movingElement.getTranslateY() - y);
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

	private void centerElement(Node movingElement) {
		double centerX = this.getWidth() / 2;
		double centerY = this.getHeight() / 2;
		double width = movingElement.getBoundsInLocal().getWidth();
		double height = movingElement.getBoundsInLocal().getHeight();
		double targetX = centerX - width / 2;
		double targetY = centerY - height / 2;
		movingElement.setTranslateX(targetX);
		movingElement.setTranslateY(targetY);
		movingElement.setScaleX(1.0);
		movingElement.setScaleY(1.0);
	}

	public DialogModelPane(Node movingElement) {
		this.setStyle("-fx-background-color: RED");
		this.movingElement = movingElement;
		this.getChildren().add(0, movingElement);
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.mouseDraggedEventHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
		this.addEventHandler(ScrollEvent.ANY, this.mouseScrollEventHandler);
	}

}