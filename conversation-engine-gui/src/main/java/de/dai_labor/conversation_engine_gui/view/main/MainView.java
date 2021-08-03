package de.dai_labor.conversation_engine_gui.view.main;

import java.net.URL;
import java.util.ResourceBundle;

import de.dai_labor.conversation_engine_gui.view.diagram.DialogueMainView;
import de.dai_labor.conversation_engine_gui.view.diagram.DialogueMainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ViewTuple<DialogueMainView, DialogueMainViewModel> viewTuple = FluentViewLoader.fxmlView(DialogueMainView.class)
				.load();
		setScene(viewTuple.getView());
		// set the new view to the back
		contentArea.setViewOrder(1000);
		newMenuItem.setOnAction(viewModel::newFile);
		openMenuItem.setOnAction(viewModel::openFile);
		exportMenuItem.setOnAction(viewModel::exportFile);
		saveMenuItem.setOnAction(viewModel::saveFile);
		saveAsMenuItem.setOnAction(viewModel::saveAsFile);
		closeMenuItem.setOnAction(viewModel::closeApplication);
	}

	private void setScene(Node view) {
		if (view == null) {
			return;
		}
		this.contentArea.getChildren().clear();
		this.contentArea.getChildren().add(view);
		VBox.setVgrow(view, Priority.ALWAYS);
	}
}
