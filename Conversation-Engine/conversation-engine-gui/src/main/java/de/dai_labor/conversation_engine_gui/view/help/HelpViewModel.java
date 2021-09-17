package de.dai_labor.conversation_engine_gui.view.help;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class HelpViewModel implements ViewModel {

	// <TreeItem, Node view for the help page>
	private Map<TreeItem<String>, Node> pageViews = new HashMap<>();
	private SimpleObjectProperty<Node> currentViewProperty = new SimpleObjectProperty<>();

	public void createTreeView(TreeItem<String> root) throws URISyntaxException {
		File pagesFolder = new File(this.getClass().getResource("./pages").toURI());
		for (File file : pagesFolder.listFiles()) {
			if (file.isDirectory()) {
				this.createTreeStructure(file, root);
			}
		}

	}

	public SimpleObjectProperty<Node> getHelpViewProperty() {
		return this.currentViewProperty;
	}

	public void setCurrentView(TreeItem<String> treeItem) {
		this.currentViewProperty.set(this.pageViews.get(treeItem));
	}

	private void createTreeStructure(File filepath, TreeItem<String> parent) {
		TreeItem<String> newTreeItem = new TreeItem<>();
		if (filepath.isDirectory()) {
			newTreeItem.setValue(filepath.getName());
			for (File file : filepath.listFiles()) {
				this.createTreeStructure(file, newTreeItem);
			}
		} else {
			newTreeItem.setValue(filepath.getName().substring(0, filepath.getName().lastIndexOf(".")));
			try {
				Node view = FXMLLoader.load(Paths.get(filepath.toString()).toUri().toURL());
				this.pageViews.put(newTreeItem, view);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		parent.getChildren().add(newTreeItem);
	}
}
