module de.dai_labor.conversation_engine_gui {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires transitive javafx.graphics;
	requires de.saxsys.mvvmfx;
	requires transitive easy.di;
	requires javax.inject;
	requires org.json;
	requires java.prefs;

	opens de.dai_labor.conversation_engine_gui.view.main to de.saxsys.mvvmfx, javafx.fxml, easy.di;
	opens de.dai_labor.conversation_engine_gui.view.dialogue to de.saxsys.mvvmfx, javafx.fxml, easy.di;
	opens de.dai_labor.conversation_engine_gui.view.settings to de.saxsys.mvvmfx, javafx.fxml, easy.di;
	opens de.dai_labor.conversation_engine_gui.models to easy.di;

	exports de.dai_labor.conversation_engine_gui;
}
