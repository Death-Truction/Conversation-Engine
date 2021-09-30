package de.dai_labor.conversation_engine_gui.gui_components;

import de.dai_labor.conversation_engine_gui.App;
import de.dai_labor.conversation_engine_gui.models.Settings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

/**
 * A Pane that displays the {@link Transition} between two {@link State States}
 * or a self {@link Transition} to the same {@link State} as an arrow
 *
 * @author Marcel Engelmann
 *
 */
public class TransitionArrow extends Pane {
	private TextField triggerTextField;
	private Shape line;
	private StackPane arrowHead;
	private Runnable setSelectedTransition;
	private Settings settings;

	/**
	 * Event Handler for the mouse pressed event. Selects the this transition
	 * visually and focuses the {@link #triggerTextField} on a double click
	 */
	private EventHandler<MouseEvent> mousePressedEventHandler = event -> {
		if (event.isPrimaryButtonDown()) {
			this.setSelectedTransition.run();
			if (event.getClickCount() == 2) {
				this.focusTriggerTextField();
			}
		}
		event.consume();
	};

	/**
	 * EventHanlder for the key released event. Removes the focus of the
	 * {@link #triggerTextField} when the Enter key has been released.
	 */
	private EventHandler<KeyEvent> keyReleasedEventHandler = event -> {
		if (event.getCode().equals(KeyCode.ENTER)) {
			this.requestFocus();
		}
		event.consume();
	};

	/**
	 * Create a new {@link TransitionArrow} object
	 *
	 * @param source                the source {@link State} of the arrow
	 * @param target                the target {@link State} of the arrow
	 * @param triggerName           the name of the trigger that will be displayed
	 *                              on top of the center of the transition arrow
	 * @param setSelectedTransition the runnable function, called to select this
	 *                              arrow's {@link Transition}
	 * @param settings              the instance of the {@link Settings} object
	 */
	public TransitionArrow(State source, State target, String triggerName, Runnable setSelectedTransition,
			Settings settings) {
		this.setSelectedTransition = setSelectedTransition;
		this.settings = settings;
		this.triggerTextField = new TextField(triggerName);
		if (source == target) {
			this.createSelfTransition(source, 1.0);
		} else {
			this.createTransition(source, target, 1.0);
		}
		this.getChildren().addAll(this.line, this.arrowHead, this.triggerTextField);
		this.addEventListeners();
		this.addSettingsChangeListeners();
		this.setPickOnBounds(false);
		// small hack to force recalculating the bindings for the arrow head (otherwise
		// it only gets displayed correctly after any interaction with the transition
		// arrow)
		Platform.runLater(this::deselect);
	}

	/**
	 * And arrow to display a temporally transition for the dragging event
	 *
	 * @param source the source {@link State} of the arrow
	 * @param target the target {@link State} of the arrow
	 * @param scale  the current scale of the parent
	 */
	public TransitionArrow(State source, StackPane target, double scale) {
		this.settings = App.easyDI.getInstance(Settings.class);
		this.triggerTextField = new TextField();
		if (source == target) {
			this.createSelfTransition(source, 1.0);
		} else {
			this.createTransition(source, target, 1.0);
		}
		this.getChildren().addAll(this.line, this.arrowHead);
		this.arrowHead.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");

		this.addSettingsChangeListeners();
	}

	/**
	 * Gets the {@link TextField} for the trigger
	 *
	 * @return the {@link TextField} for the trigger
	 */
	public TextField getTriggerTextField() {
		return this.triggerTextField;
	}

	/**
	 * Visually selects this arrow by setting the Color to the
	 * {@link Settings#getTransitionSelectedColorProperty() Transition's selected
	 * color}
	 */
	public void select() {
		this.line.strokeProperty().bind(this.settings.getTransitionSelectedColorProperty());
		this.arrowHead.backgroundProperty()
				.bind(Bindings.createObjectBinding(
						() -> new Background(
								new BackgroundFill(this.settings.getTransitionSelectedColorProperty().get(),
										CornerRadii.EMPTY, Insets.EMPTY)),
						this.settings.getTransitionSelectedColorProperty()));
	}

	/**
	 * Visually deselects this arrow by setting the Color to the
	 * {@link Settings#getTransitionNormalColorProperty() Transition's normal color}
	 */
	public void deselect() {
		this.line.strokeProperty().bind(this.settings.getTransitionNormalColorProperty());
		this.arrowHead.backgroundProperty()
				.bind(Bindings
						.createObjectBinding(
								() -> new Background(
										new BackgroundFill(this.settings.getTransitionNormalColorProperty().get(),
												CornerRadii.EMPTY, Insets.EMPTY)),
								this.settings.getTransitionNormalColorProperty()));
		this.triggerTextField.deselect();
	}

	/**
	 * Selects the {@link Transition}'s {@link TextField} with the trigger name
	 */
	public void focusTriggerTextField() {
		this.triggerTextField.requestFocus();
		this.triggerTextField.selectAll();
	}

	/**
	 * Adds all the Event Listeners for the {@link Arrow} instance
	 */
	private void addEventListeners() {
		this.line.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.arrowHead.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.triggerTextField.addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
		this.triggerTextField.addEventFilter(KeyEvent.KEY_RELEASED, this.keyReleasedEventHandler);
	}

	/**
	 * Binds the {@link Arrow}'s elements to the defined settings. Therefore if the
	 * settings change, the arrow's elements will do so as well
	 */
	private void addSettingsChangeListeners() {
		this.line.strokeProperty().bind(this.settings.getTransitionNormalColorProperty());
		this.line.strokeWidthProperty()
				.bind(Bindings.createDoubleBinding(
						() -> (this.settings.getTransitionSizeProperty().get() <= 8 ? 1.0
								: this.settings.getTransitionSizeProperty().get() / 8.0),
						this.settings.getTransitionSizeProperty()));
		if (this.triggerTextField != null) {
			this.triggerTextField.fontProperty()
					.bind(Bindings.createObjectBinding(
							() -> new Font(this.settings.getTransitionFontSizeProperty().get()),
							this.settings.getTransitionFontSizeProperty()));
			this.triggerTextField.styleProperty().bind(Bindings.createObjectBinding(
					() -> "-fx-text-fill: "
							+ this.settings.getTransitionFontColorProperty().get().toString().replace("0x", "#"),
					this.settings.getTransitionFontColorProperty()));
		}
		if (this.arrowHead != null) {
			this.arrowHead.minHeightProperty().bind(this.settings.getTransitionSizeProperty());
			this.arrowHead.minWidthProperty().bind(this.settings.getTransitionSizeProperty());
			this.arrowHead.maxHeightProperty().bind(this.settings.getTransitionSizeProperty());
			this.arrowHead.minWidthProperty().bind(this.settings.getTransitionSizeProperty());
		}
	}

	/**
	 * Sets this TransitionArrow to be a self transition. A self transition is a
	 * transition where the source and target {@link State} is the same
	 *
	 * @param source the {@link State} for the self transition
	 * @param scale  the current scale of the Parent Pane to adjust the new Element
	 *               to that scale.
	 */
	private void createSelfTransition(StackPane source, double scale) {
		Arc transitionLine = this.getArc(source, scale);
		StackPane arrow = this.getArrow(transitionLine);
		this.setTextFieldProperties(transitionLine);
		this.line = transitionLine;
		this.arrowHead = arrow;
	}

	/**
	 * Sets this TransitionArrow to be a transition between two {@link State
	 * states}.
	 *
	 * @param source the source of the transition
	 * @param target the target of the transition
	 * @param scale  the current scale of the Parent Pane to adjust the new Element
	 *               to that scale.
	 */
	private void createTransition(StackPane source, StackPane target, double scale) {
		Line transitionLine = this.getLine(source, target, scale);
		StackPane arrow = this.getArrow(transitionLine, target);
		this.setTextFieldProperties(transitionLine);
		this.line = transitionLine;
		this.arrowHead = arrow;
	}

	/**
	 * Builds a {@link Line} between the provided start and end pane's center point.
	 *
	 * This method has been (partly) taken from this <a href=
	 * "https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201">source</a>
	 *
	 * @param startDot Pane for considering start point
	 * @param endDot   Pane for considering end point
	 * @param scale    the current scale of the {@link Parent}
	 * @return the new created {@link Line}
	 */
	private Line getLine(StackPane startDot, StackPane endDot, double scale) {
		Line newLine = new Line();
		newLine.startXProperty().bind(startDot.layoutXProperty().add(startDot.translateXProperty().divide(scale))
				.add(startDot.widthProperty().divide(2)));
		newLine.startYProperty().bind(startDot.layoutYProperty().add(startDot.translateYProperty().divide(scale))
				.add(startDot.heightProperty().divide(2)));
		newLine.endXProperty().bind(endDot.layoutXProperty().add(endDot.translateXProperty().divide(scale))
				.add(endDot.widthProperty().divide(2)));
		newLine.endYProperty().bind(endDot.layoutYProperty().add(endDot.translateYProperty().divide(scale))
				.add(endDot.heightProperty().divide(2)));
		return newLine;
	}

	/**
	 * Builds an 270 degree long {@link Arc} from the center of the element
	 *
	 * This method has been (partly) taken from this <a href=
	 * "https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201">source</a>
	 *
	 * @param startDot Pane for considering start point
	 * @param endDot   Pane for considering end point
	 * @param scale    the current scale of the {@link Parent}
	 * @return the new created {@link Line}
	 */
	private Arc getArc(StackPane element, double scale) {
		Arc donutArc = new Arc();
		donutArc.setStartAngle(0);
		donutArc.setLength(270);
		DoubleBinding centerX = element.layoutXProperty().add(element.translateXProperty().divide(scale))
				.add(element.widthProperty().divide(2));
		DoubleBinding centerY = element.layoutYProperty().add(element.translateYProperty().divide(scale))
				.add(element.heightProperty().divide(2));
		donutArc.centerXProperty().bind(centerX.subtract(element.widthProperty().divide(2)));
		donutArc.centerYProperty().bind(centerY.subtract(element.heightProperty().divide(2)));
		donutArc.radiusXProperty().bind(element.heightProperty().divide(2));
		donutArc.radiusYProperty().bind(element.widthProperty().divide(2));
		donutArc.setStrokeType(StrokeType.OUTSIDE);
		donutArc.setStrokeLineCap(StrokeLineCap.BUTT);
		donutArc.setFill(null);
		donutArc.setPickOnBounds(false);
		return donutArc;
	}

	/**
	 * Builds an arrow head for the given {@link Arc}
	 *
	 * @param arc the {@link Arc} that requires the arrow head
	 * @return an arrow head for the given {@link Arc}
	 */
	private StackPane getArrow(Arc arc) {
		final StackPane arrow = new StackPane();
		arrow.layoutXProperty().bind(arc.centerXProperty().subtract(arrow.widthProperty()));
		arrow.layoutYProperty().bind(arc.centerYProperty().add(arc.radiusYProperty())
				.subtract(arrow.heightProperty().divide(2)).add(arc.strokeWidthProperty().divide(2)));
		arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		return arrow;
	}

	/**
	 * Builds an arrow on the provided line pointing towards the specified pane.
	 *
	 * This method has been (partly) taken from this <a href=
	 * "https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201">source</a>
	 *
	 * @param toLineEnd Specifies whether the arrow to point towards end pane or
	 *                  start pane.
	 * @param line      The {@link Line} joining the layout center points of the
	 *                  provided panes.
	 * @param startDot  The {@link Pane} which is considered as start point of line
	 * @param endDot    The {@link Pane} which is considered as end point of line
	 */
	private StackPane getArrow(Line line, StackPane endDot) {
		final StackPane arrow = new StackPane();

		// Determining the x point on the line which is at a certain distance.
		final DoubleBinding tX = Bindings.createDoubleBinding(() -> {
			final double xDiffSqu = Math.pow(line.getEndX() - line.getStartX(), 2);
			final double yDiffSqu = Math.pow(line.getEndY() - line.getStartY(), 2);
			final double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
			double dt;
			// When determining the point towards end, the required distance is total length
			// minus (radius + arrow half width)
			dt = lineLength - endDot.getWidth() / 2 - arrow.getWidth() / 2;
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
			dt = lineLength - endDot.getHeight() / 2 - arrow.getHeight() / 2;
			final double t = dt / lineLength;
			final double dy = (1 - t) * line.getStartY() + t * line.getEndY();
			return dy;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

		arrow.layoutXProperty().bind(tX.subtract(arrow.widthProperty().divide(2)));
		arrow.layoutYProperty().bind(tY.subtract(arrow.heightProperty().divide(2)));

		final DoubleBinding endArrowAngle = Bindings.createDoubleBinding(() -> {
			final double stX = line.getStartX();
			final double stY = line.getStartY();
			final double enX = line.getEndX();
			final double enY = line.getEndY();
			double angle = Math.toDegrees(Math.atan2(enY - stY, enX - stX));
			if (angle < 0) {
				angle += 360;
			}
			return angle;
		}, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());
		arrow.rotateProperty().bind(endArrowAngle);
		arrow.setStyle("-fx-shape: \"M0,-4L4,0L0,4Z\"");
		return arrow;
	}

	/**
	 * Places the {@link #triggerTextField} at the center of the arrow's
	 * {@link Line}
	 *
	 * This method has been partly taken from this <a href=
	 * "https://stackoverflow.com/questions/53366602/creating-directed-edges-javafx/53386201#53386201">source</a>
	 *
	 * @param line The {@link Line} on which the {@link #triggerTextField} needs to
	 *             be set.
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

	/**
	 * Places the {@link #triggerTextField} at the center of the arrow's {@link Arc}
	 *
	 * @param arc The {@link Arc} on which the {@link #triggerTextField} needs to be
	 *            set.
	 */
	private void setTextFieldProperties(Arc arc) {
		this.triggerTextField.getStyleClass().add("transitionTextField");

		double angle = 180;

		this.triggerTextField.layoutXProperty().bind(Bindings.createDoubleBinding(
				() -> (arc.getCenterX() + arc.getRadiusX() * Math.cos(angle) - this.triggerTextField.getWidth() / 2),
				arc.centerXProperty(), arc.radiusXProperty(), this.triggerTextField.widthProperty(),
				this.triggerTextField.heightProperty()));
		this.triggerTextField.layoutYProperty().bind(Bindings.createDoubleBinding(
				() -> (arc.getCenterY() + arc.getRadiusY() * Math.sin(angle) - this.triggerTextField.getHeight() / 2),
				arc.centerYProperty(), arc.radiusYProperty(), this.triggerTextField.widthProperty(),
				this.triggerTextField.heightProperty()));
	}

}