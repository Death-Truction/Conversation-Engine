package de.dai_labor.conversations_engine_gui.controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import de.dai_labor.conversations_engine_gui.gui_component.DialogModelPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class DialogModelController implements Initializable {

	@FXML
	private AnchorPane mainPane;

	private Group circles = new Group();

	private DialogModelPane dialogModelPane = new DialogModelPane();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.addCircle(null);
		this.clipChildren();
		dialogModelPane.getChildren().add(circles);
		mainPane.getChildren().add(0, dialogModelPane);
	}

	@FXML
	private void addCircle(ActionEvent e) {
		for (int i = 0; i < 1000; i++) {
			Circle circle = new Circle(new Random().nextInt(1280), new Random().nextInt(720),
					new Random().nextInt(50) + 5);
			Color randomColor = Color.color(Math.random(), Math.random(), Math.random());
			circle.setFill(randomColor);
			circle.setStroke(randomColor);
			circles.getChildren().add(circle);
		}
	}

	private void clipChildren() {
		final Rectangle clipPane = new Rectangle();
		dialogModelPane.setClip(clipPane);

		dialogModelPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
			clipPane.setWidth(newValue.getWidth());
			clipPane.setHeight(newValue.getHeight());
		});
	}

}
