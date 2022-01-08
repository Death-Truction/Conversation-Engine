package de.dai_labor.dialog_modeling_tool.view.help;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.control.TreeItem;

public class HelpViewModel implements ViewModel {

	// <TreeItem, Node view for the help page>
	private Map<TreeItem<String>, File> pageViews = new HashMap<>();

	public String getCurrentView(TreeItem<String> treeItem) {
		File file = this.pageViews.get(treeItem);
		if (file == null) {
			return "";
		}
		String fileContent = "";
		try {
			fileContent = Files.readString(file.toPath());
		} catch (IOException e) {
			// should never occur
			e.printStackTrace();
		}
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(parser.parse(fileContent));
	}

	public void createTreeView(TreeItem<String> root) {
		try {
			File pagesFolder = new File(this.getClass().getResource("./pages").toURI());
			for (File file : pagesFolder.listFiles()) {
				if (file.isDirectory()) {
					this.createTreeStructure(file, root);
				}
			}
		} catch (URISyntaxException e) {
			// should never occur
			e.printStackTrace();
		}
	}

	public TreeItem<String> getTreeItem(String name) {
		for (TreeItem<String> treeItem : this.pageViews.keySet()) {
			if (treeItem.getValue().equals(name)) {
				return treeItem;
			}
		}
		return null;
	}

	private void createTreeStructure(File filepath, TreeItem<String> parent) {
		TreeItem<String> newTreeItem;
		if (this.parseInt(filepath.getName()) == null) {
			newTreeItem = new TreeItem<>();
		} else {
			newTreeItem = parent;
		}

		if (filepath.isDirectory()) {
			newTreeItem.setValue(filepath.getName());
			for (File file : filepath.listFiles()) {
				if (file.isFile() && filepath.getName().equals(this.removeFileExtension(file))) {
					this.pageViews.put(newTreeItem, file);
					continue;
				}
				this.createTreeStructure(file, newTreeItem);
			}
		} else {
			newTreeItem.setValue(this.removeFileExtension(filepath));
			this.pageViews.put(newTreeItem, filepath);
		}
		// if the folder name is number, then it is used to order the elements, but will
		// not be added to the tree view
		if (this.parseInt(filepath.getName()) == null) {
			parent.getChildren().add(newTreeItem);
		}
	}

	private String removeFileExtension(File filename) {
		return filename.getName().substring(0, filename.getName().lastIndexOf("."));
	}

	private Integer parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
