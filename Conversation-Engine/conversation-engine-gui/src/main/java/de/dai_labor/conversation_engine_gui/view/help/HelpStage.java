package de.dai_labor.conversation_engine_gui.view.help;

import de.dai_labor.conversation_engine_gui.util.Util;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpStage {
	private final Stage windowStage = new Stage();

	public HelpStage(String source) {
		ViewTuple<HelpView, HelpViewModel> viewTuple = FluentViewLoader.fxmlView(HelpView.class).load();
		this.windowStage.getIcons().add(Util.getIcon());
		viewTuple.getView().getStylesheets().add(Util.getStyleSheetPath());
		viewTuple.getCodeBehind().selectMenuItem(source);
		this.windowStage.minHeightProperty().set(640);
		this.windowStage.minWidthProperty().set(480);
		this.windowStage.setHeight(700);
		this.windowStage.setWidth(1000);
		this.windowStage.setScene(new Scene(viewTuple.getView()));
		this.windowStage.show();
	}

	public HelpStage() {
		this("Dialogue Model");
	}
}
