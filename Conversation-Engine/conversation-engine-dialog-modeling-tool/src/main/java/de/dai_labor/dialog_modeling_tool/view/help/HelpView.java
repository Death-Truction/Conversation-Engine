package de.dai_labor.dialog_modeling_tool.view.help;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class HelpView implements FxmlView<HelpViewModel>, Initializable {

	@InjectViewModel
	private HelpViewModel viewModel;
	@FXML
	private TreeView<String> navigationTreeView;
	@FXML
	private WebView pageView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TreeItem<String> root = new TreeItem<>();
		root.setExpanded(true);
		this.navigationTreeView.setShowRoot(false);
		this.viewModel.createTreeView(root);
		this.navigationTreeView.setRoot(root);
		this.navigationTreeView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					newValue.setExpanded(true);
					String newContent = this.viewModel.getCurrentView(newValue);
					if (!newContent.isBlank()) {
						this.pageView.getEngine().loadContent(newContent);
					}
				});
		this.navigationTreeView.getSelectionModel().select(0);
	}

	public void selectMenuItem(String name) {
		this.navigationTreeView.getSelectionModel().select(this.viewModel.getTreeItem(name));
	}

}
