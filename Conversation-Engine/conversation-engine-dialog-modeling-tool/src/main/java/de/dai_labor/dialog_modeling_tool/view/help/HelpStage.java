package de.dai_labor.dialog_modeling_tool.view.help;

import de.dai_labor.dialog_modeling_tool.util.Util;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpStage {
	private static Stage windowStage;
	private static ViewTuple<HelpView, HelpViewModel> viewTuple;

	private HelpStage() {
	}

	public static void show(String entryPage) {
		if (windowStage == null) {
			create();
		}
		viewTuple.getCodeBehind().selectMenuItem(entryPage);
		windowStage.show();
	}

	public static void show() {
		show("Dialog Model");
	}

	public static void create() {
		viewTuple = FluentViewLoader.fxmlView(HelpView.class).load();
		windowStage = new Stage();
		windowStage.getIcons().add(Util.getIcon());
		viewTuple.getView().getStylesheets().add(Util.getStyleSheetPath());
		windowStage.minHeightProperty().set(640);
		windowStage.minWidthProperty().set(480);
		windowStage.setHeight(700);
		windowStage.setWidth(1000);
		windowStage.setScene(new Scene(viewTuple.getView()));
	}
}
