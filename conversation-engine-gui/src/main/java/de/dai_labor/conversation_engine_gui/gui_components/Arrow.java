package de.dai_labor.conversation_engine_gui.gui_components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

//Source:
// https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201
public class Arrow extends Pane {
	double sceneX, sceneY, layoutX, layoutY;
	private TextField triggerTextField;

	public Arrow(State source, State target, String triggerName) {
		Line line = getLine(source, target, 1.0);
		StackPane arrowAB = getArrow(true, line, source, target);
		this.triggerTextField = getTextField(line, triggerName);
		this.getChildren().addAll(line, arrowAB, this.triggerTextField);
		arrowAB.setMouseTransparent(true);
		this.setPickOnBounds(false);
	}

	public Arrow(State source, StackPane tmpPane, double scale) {
		System.out.println(scale);
		Line line = getLine(source, tmpPane, scale);
		StackPane arrowAB = getArrow(true, line, source, tmpPane);
		this.getChildren().addAll(line, arrowAB);
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
		arrow.setStyle(
				"-fx-background-color:#333333;-fx-border-width:1px;-fx-border-color:black;-fx-shape: \"M0,-4L4,0L0,4Z\"");//
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
			double xDiffSqu = (line.getEndX() - line.getStartX()) * (line.getEndX() - line.getStartX());
			double yDiffSqu = (line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY());
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
	private TextField getTextField(Line line, String text) {
		double size = 20;
		TextField weight = new TextField(text);
		weight.setStyle(
				"-fx-background-color: transparent; -fx-text-fill: White; -fx-font-size: 32px; -fx-alignment: center;");

		DoubleBinding wgtSqrHalfWidth = weight.widthProperty().divide(2);
		DoubleBinding wgtSqrHalfHeight = weight.heightProperty().divide(2);
		DoubleBinding lineXHalfLength = line.endXProperty().subtract(line.startXProperty()).divide(2);
		DoubleBinding lineYHalfLength = line.endYProperty().subtract(line.startYProperty()).divide(2);

		weight.layoutXProperty().bind(line.startXProperty().add(lineXHalfLength.subtract(wgtSqrHalfWidth)));
		weight.layoutYProperty().bind(line.startYProperty().add(lineYHalfLength.subtract(wgtSqrHalfHeight)));
		return weight;
	}

	public TextField getTriggerTextField() {
		return this.triggerTextField;
	}

}
