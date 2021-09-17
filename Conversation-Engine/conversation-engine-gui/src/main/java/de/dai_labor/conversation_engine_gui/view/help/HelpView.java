package de.dai_labor.conversation_engine_gui.view.help;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class HelpView implements FxmlView<HelpViewModel>, Initializable {

	@InjectViewModel
	private HelpViewModel viewModel;
	@FXML
	private TreeView<String> navigationTreeView;
	@FXML
	private BorderPane pageView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TreeItem<String> root = new TreeItem<>();
		root.setExpanded(true);
		this.navigationTreeView.setShowRoot(false);
		try {
			this.viewModel.createTreeView(root);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.navigationTreeView.setRoot(root);
		this.pageView.centerProperty().bindBidirectional(this.viewModel.getHelpViewProperty());
		this.navigationTreeView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					this.viewModel.setCurrentView(newValue);
				});

	}

}
