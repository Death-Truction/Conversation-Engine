package de.dai_labor.conversation_engine_gui.gui_components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

//Source:
// https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201
public class Arrow extends Pane {
	double sceneX, sceneY, layoutX, layoutY;
	private TextField triggerTextField;
	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final Color SELECTED_COLOR = Color.GREEN;
	private Line line;
	private StackPane arrow;

	public Arrow(State source, State target, String triggerName) {
		this.line = getLine(source, target, 1.0);
		this.arrow = getArrow(true, line, source, target);
		arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		this.unselect();
		this.triggerTextField = new TextField(triggerName);
		setTextFieldProperties(line);
		this.getChildren().addAll(line, arrow, this.triggerTextField);
		arrow.setMouseTransparent(true);
		this.setPickOnBounds(false);

	}

	public Arrow(State source, StackPane tmpPane, double scale) {
		this.line = getLine(source, tmpPane, 1.0);
		this.arrow = getArrow(true, line, source, tmpPane);
		arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		this.unselect();
		this.getChildren().addAll(line, arrow);
	}

	public TextField getTriggerTextField() {
		return this.triggerTextField;
	}

	public void select() {
		this.line.setStroke(SELECTED_COLOR);
		this.arrow.setBackground(new Background(new BackgroundFill(SELECTED_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
	}

	public void unselect() {
		this.line.setStroke(DEFAULT_COLOR);
		this.arrow.setBackground(new Background(new BackgroundFill(DEFAULT_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
	}

	/**
	 * Builds a line between the provided start and end panes center point.
	 *
	 * @param startDot Pane for considering start point
	 * @param endDot   Pane for considering end point
	 * @return Line joining the layout center points of the provided panes.
	 */
	private Line getLine(StackPane startDot, StackPane endDot, double scale) {
		Line line = new Line();
		line.setStrokeWidth(2);
		line.startXProperty().bind(
				startDot.layoutXProperty().add(startDot.translateXProperty()).add(startDot.widthProperty().divide(2)));
		line.startYProperty().bind(
				startDot.layoutYProperty().add(startDot.translateYProperty()).add(startDot.heightProperty().divide(2)));
		line.endXProperty().bind(endDot.layoutXProperty().add(endDot.translateXProperty().divide(scale))
				.add(endDot.widthProperty().divide(2)));
		line.endYProperty().bind(endDot.layoutYProperty().add(endDot.translateYProperty().divide(scale))
				.add(endDot.heightProperty().divide(2)));
		return line;
	}

	/**
	 * Builds an arrow on the provided line pointing towards the specified pane.
	 *
	 * @param toLineEnd Specifies whether the arrow to point towards end pane or
	 *                  start pane.
	 * @param line      Line joining the layout center points of the provided panes.
	 * @param startDot  Pane which is considered as start point of line
	 * @param endDot    Pane which is considered as end point of line
	 * @return Arrow towards the specified pane.
	 */
	private StackPane getArrow(boolean toLineEnd, Line line, StackPane startDot, StackPane endDot) {
		double size = 12; // Arrow size
		StackPane arrow = new StackPane();
		arrow.setPrefSize(size, size);
		arrow.setMaxSize(size, size);
		arrow.setMinSize(size, size);

		// Determining the arrow visibility unless there is enough space between dots.
		DoubleBinding xDiff = line.endXProperty().subtract(line.startXProperty());
		DoubleBinding yDiff = line.endYProperty().subtract(line.startYProperty());
		BooleanBinding visible = (xDiff.lessThanOrEqualTo(size).and(xDiff.greaterThanOrEqualTo(-size))
				.and(yDiff.greaterThanOrEqualTo(-size)).and(yDiff.lessThanOrEqualTo(size))).not();
		arrow.visibleProperty().bind(visible);

		// Determining the x point on the line which is at a certain distance.
		DoubleBinding tX = Bindings.createDoubleBinding(() -> {
			double xDiffSqu = Math.pow(line.getEndX() - line.getStartX(), 2);
			double yDiffSqu = Math.pow(line.getEndY() - line.getStartY(), 2);
			double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
			double dt;
			if (toLineEnd) {
				// When determining the point towards end, the required distance is total length
				// minus (radius + arrow half width)
				dt = lineLength - (endDot.getWidth() / 2) - (arrow.getWidth() / 2);
			} else {
				// When determining the point towards start, the required distance is just
				// (radius + arrow half width)
				dt = (startDot.getWidth() / 2) + (arrow.getWidth() / 2);
			}

			double t = dt / lineLength;
			double dx = ((1 - t) * line.getStartX()) + (t * line.getEndX());
			return dx;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

		// Determining the y point on the line which is at a certain distance.
		DoubleBinding tY = Bindings.createDoubleBinding(() -> {
			double xDiffSqu = (line.getEndX() - line.getStartX()) * (line.getEndX() - line.getStartX());
			double yDiffSqu = (line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY());
			double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
			double dt;
			if (toLineEnd) {
				dt = lineLength - (endDot.getHeight() / 2) - (arrow.getHeight() / 2);
			} else {
				dt = (startDot.getHeight() / 2) + (arrow.getHeight() / 2);
			}
			double t = dt / lineLength;
			double dy = ((1 - t) * line.getStartY()) + (t * line.getEndY());
			return dy;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

		arrow.layoutXProperty().bind(tX.subtract(arrow.widthProperty().divide(2)));
		arrow.layoutYProperty().bind(tY.subtract(arrow.heightProperty().divide(2)));

		DoubleBinding endArrowAngle = Bindings.createDoubleBinding(() -> {
			double stX = toLineEnd ? line.getStartX() : line.getEndX();
			double stY = toLineEnd ? line.getStartY() : line.getEndY();
			double enX = toLineEnd ? line.getEndX() : line.getStartX();
			double enY = toLineEnd ? line.getEndY() : line.getStartY();
			double angle = Math.toDegrees(Math.atan2(enY - stY, enX - stX));
			if (angle < 0) {
				angle += 360;
			}
			return angle;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());
		arrow.rotateProperty().bind(endArrowAngle);

		return arrow;
	}

	/**
	 * Builds a pane at the center of the provided line.
	 *
	 * @param line Line on which the pane need to be set.
	 * @return Pane located at the center of the provided line.
	 */
	private void setTextFieldProperties(Line line) {
		double size = 20;
		this.triggerTextField.getStyleClass().add("transitionTextField");

		DoubleBinding wgtSqrHalfWidth = this.triggerTextField.widthProperty().divide(2);
		DoubleBinding wgtSqrHalfHeight = this.triggerTextField.heightProperty().divide(2);
		DoubleBinding lineXHalfLength = line.endXProperty().subtract(line.startXProperty()).divide(2);
		DoubleBinding lineYHalfLength = line.endYProperty().subtract(line.startYProperty()).divide(2);

		this.triggerTextField.layoutXProperty()
				.bind(line.startXProperty().add(lineXHalfLength.subtract(wgtSqrHalfWidth)));
		this.triggerTextField.layoutYProperty()
				.bind(line.startYProperty().add(lineYHalfLength.subtract(wgtSqrHalfHeight)));
	}

}
