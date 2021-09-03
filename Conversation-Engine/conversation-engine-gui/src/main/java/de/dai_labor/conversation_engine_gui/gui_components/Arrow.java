package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

//Source:
// https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201
public class Arrow extends Pane {
	double sceneX, sceneY, layoutX, layoutY;
	private TextField triggerTextField;
	private final Line line;
	private StackPane arrow;
	private static final double DEFAULT_ARROW_SIZE = 12; // Arrow size
	private Runnable setSelectedTransition;
	private Settings settings;

	public Arrow(State source, State target, String triggerName, Runnable setSelectedTransition, Settings settings) {
		this.setSelectedTransition = setSelectedTransition;
		this.settings = settings;
		this.line = this.getLine(source, target, 1.0);
		this.arrow = this.getArrow(true, this.line, source, target);
		this.arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		this.triggerTextField = new TextField(triggerName);
		this.setTextFieldProperties(this.line);
		this.getChildren().addAll(this.line, this.arrow, this.triggerTextField);
		this.setPickOnBounds(false);
		this.addEventListeners();
		this.addSettingsChangeListeners();
		// small hack to force recalculating the bindings for the arrow head (otherwise
		// it only gets displayed correctly after any interaction with the transition
		// arrow)
		Platform.runLater(this::deselect);
	}

	// display temporally dragging arrow
	public Arrow(State source, StackPane tmpPane, double scale) {
		this.line = this.getLine(source, tmpPane, scale);
		this.arrow = this.getArrow(true, this.line, source, tmpPane);
		this.arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		this.getChildren().addAll(this.line, this.arrow);
	}

	public TextField getTriggerTextField() {
		return this.triggerTextField;
	}

	public void select() {
		this.line.strokeProperty().bind(this.settings.getTransitionSelectedColorProperty());
		this.arrow.backgroundProperty()
				.bind(Bindings.createObjectBinding(
						() -> new Background(
								new BackgroundFill(this.settings.getTransitionSelectedColorProperty().get(),
										CornerRadii.EMPTY, Insets.EMPTY)),
						this.settings.getTransitionSelectedColorProperty()));
	}

	public void deselect() {
		this.line.strokeProperty().bind(this.settings.getTransitionNormalColorProperty());
		this.arrow.backgroundProperty()
				.bind(Bindings
						.createObjectBinding(
								() -> new Background(
										new BackgroundFill(this.settings.getTransitionNormalColorProperty().get(),
												CornerRadii.EMPTY, Insets.EMPTY)),
								this.settings.getTransitionNormalColorProperty()));
		this.triggerTextField.deselect();
	}

	private void addEventListeners() {
		this.line.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.arrow.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.triggerTextField.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.triggerTextField.addEventFilter(KeyEvent.KEY_RELEASED, this.keyReleasedEventHandler);
	}

	private void addSettingsChangeListeners() {
		this.line.strokeProperty().bind(this.settings.getTransitionNormalColorProperty());
		this.triggerTextField.fontProperty()
				.bind(Bindings.createObjectBinding(() -> new Font(this.settings.getTransitionFontSizeProperty().get()),
						this.settings.getTransitionFontSizeProperty()));
		this.triggerTextField.styleProperty()
				.bind(Bindings.createObjectBinding(
						() -> "-fx-text-fill: "
								+ this.settings.getTransitionFontColorProperty().get().toString().replace("0x", "#"),
						this.settings.getTransitionFontColorProperty()));
		this.arrow.minHeightProperty().bind(this.settings.getTransitionSizeProperty());
		this.arrow.minWidthProperty().bind(this.settings.getTransitionSizeProperty());
		this.arrow.maxHeightProperty().bind(this.settings.getTransitionSizeProperty());
		this.arrow.minWidthProperty().bind(this.settings.getTransitionSizeProperty());
		this.line.strokeWidthProperty()
				.bind(Bindings.createDoubleBinding(
						() -> (this.settings.getTransitionSizeProperty().get() <= 8 ? 1.0
								: this.settings.getTransitionSizeProperty().get() / 8.0),
						this.settings.getTransitionSizeProperty()));
	}

	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (event.isPrimaryButtonDown()) {
			this.setSelectedTransition.run();
			if (event.getClickCount() == 2) {
				this.focusTriggerTextField();
			}
		}
		event.consume();
	};
	private EventHandler<KeyEvent> keyReleasedEventHandler = event -> {
		if (event.getCode().equals(KeyCode.ENTER)) {
			this.requestFocus();
		}
		event.consume();
	};

	public void focusTriggerTextField() {
		this.triggerTextField.requestFocus();
		this.triggerTextField.selectAll();
	}

	/**
	 * Builds a line between the provided start and end panes center point.
	 *
	 * @param startDot Pane for considering start point
	 * @param endDot   Pane for considering end point
	 * @return Line joining the layout center points of the provided panes.
	 */
	private Line getLine(StackPane startDot, StackPane endDot, double scale) {
		final Line line = new Line();
		line.startXProperty().bind(startDot.layoutXProperty().add(startDot.translateXProperty().divide(scale))
				.add(startDot.widthProperty().divide(2)));
		line.startYProperty().bind(startDot.layoutYProperty().add(startDot.translateYProperty().divide(scale))
				.add(startDot.heightProperty().divide(2)));
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
		final StackPane arrow = new StackPane();

		// Determining the arrow visibility unless there is enough space between dots.
		final DoubleBinding xDiff = line.endXProperty().subtract(line.startXProperty());
		final DoubleBinding yDiff = line.endYProperty().subtract(line.startYProperty());
		final BooleanBinding visible = xDiff.lessThanOrEqualTo(DEFAULT_ARROW_SIZE)
				.and(xDiff.greaterThanOrEqualTo(-DEFAULT_ARROW_SIZE))
				.and(yDiff.greaterThanOrEqualTo(-DEFAULT_ARROW_SIZE)).and(yDiff.lessThanOrEqualTo(DEFAULT_ARROW_SIZE))
				.not();
		arrow.visibleProperty().bind(visible);

		// Determining the x point on the line which is at a certain distance.
		final DoubleBinding tX = Bindings.createDoubleBinding(() -> {
			final double xDiffSqu = Math.pow(line.getEndX() - line.getStartX(), 2);
			final double yDiffSqu = Math.pow(line.getEndY() - line.getStartY(), 2);
			final double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
			double dt;
			if (toLineEnd) {
				// When determining the point towards end, the required distance is total length
				// minus (radius + arrow half width)
				dt = lineLength - endDot.getWidth() / 2 - arrow.getWidth() / 2;
			} else {
				// When determining the point towards start, the required distance is just
				// (radius + arrow half width)
				dt = startDot.getWidth() / 2 + arrow.getWidth() / 2;
			}

			final double t = dt / lineLength;
			final double dx = (1 - t) * line.getStartX() + t * line.getEndX();
			return dx;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

		// Determining the y point on the line which is at a certain distance.
		final DoubleBinding tY = Bindings.createDoubleBinding(() -> {
			final double xDiffSqu = (line.getEndX() - line.getStartX()) * (line.getEndX() - line.getStartX());
			final double yDiffSqu = (line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY());
			final double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
			double dt;
			if (toLineEnd) {
				dt = lineLength - endDot.getHeight() / 2 - arrow.getHeight() / 2;
			} else {
				dt = startDot.getHeight() / 2 + arrow.getHeight() / 2;
			}
			final double t = dt / lineLength;
			final double dy = (1 - t) * line.getStartY() + t * line.getEndY();
			return dy;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

		arrow.layoutXProperty().bind(tX.subtract(arrow.widthProperty().divide(2)));
		arrow.layoutYProperty().bind(tY.subtract(arrow.heightProperty().divide(2)));

		final DoubleBinding endArrowAngle = Bindings.createDoubleBinding(() -> {
			final double stX = toLineEnd ? line.getStartX() : line.getEndX();
			final double stY = toLineEnd ? line.getStartY() : line.getEndY();
			final double enX = toLineEnd ? line.getEndX() : line.getStartX();
			final double enY = toLineEnd ? line.getEndY() : line.getStartY();
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
		this.triggerTextField.getStyleClass().add("transitionTextField");

		final DoubleBinding wgtSqrHalfWidth = this.triggerTextField.widthProperty().divide(2);
		final DoubleBinding wgtSqrHalfHeight = this.triggerTextField.heightProperty().divide(2);
		final DoubleBinding lineXHalfLength = line.endXProperty().subtract(line.startXProperty()).divide(2);
		final DoubleBinding lineYHalfLength = line.endYProperty().subtract(line.startYProperty()).divide(2);

		this.triggerTextField.layoutXProperty()
				.bind(line.startXProperty().add(lineXHalfLength.subtract(wgtSqrHalfWidth)));
		this.triggerTextField.layoutYProperty()
				.bind(line.startYProperty().add(lineYHalfLength.subtract(wgtSqrHalfHeight)));
	}

}