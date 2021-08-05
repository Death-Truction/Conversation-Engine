package de.dai_labor.conversation_engine_gui.view.main;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainView implements FxmlView<MainViewModel>, Initializable {

	@InjectViewModel
	private MainViewModel viewModel;
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private VBox contentArea;
	@FXML
	private MenuItem closeMenuItem;
	@FXML
	private MenuItem newMenuItem;
	@FXML
	private MenuItem openMenuItem;
	@FXML
	private MenuItem exportMenuItem;
	@FXML
	private MenuItem saveMenuItem;
	@FXML
	private MenuItem saveAsMenuItem;
	@FXML
	private Button navButtonDialogue;
	@FXML
	private Button navButtonDialogueData;
	@FXML
	private Button navButtonSettings;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the new view to the back
		contentArea.setViewOrder(1000);
		newMenuItem.setOnAction(viewModel::newFile);
		openMenuItem.setOnAction(viewModel::openFile);
		exportMenuItem.setOnAction(viewModel::exportFile);
		saveMenuItem.setOnAction(viewModel::saveFile);
		saveAsMenuItem.setOnAction(viewModel::saveAsFile);
		closeMenuItem.setOnAction(viewModel::closeApplication);
		navButtonDialogue.setOnAction(viewModel::setView);
		navButtonDialogueData.setOnAction(viewModel::setView);
		navButtonSettings.setOnAction(viewModel::setView);
		viewModel.getViewBinding().addListener(event -> updateScene());
		updateScene();
	}

	private void updateScene() {
		Node view = viewModel.getViewBinding().get();
		this.contentArea.getChildren().clear();
		this.contentArea.getChildren().add(view);
		VBox.setVgrow(view, Priority.ALWAYS);
	}
}
