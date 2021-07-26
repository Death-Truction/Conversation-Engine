module de.dai_labor.conversation_engine_gui {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires transitive javafx.graphics;

	opens de.dai_labor.conversation_engine_gui.controllers to javafx.fxml;

	exports de.dai_labor.conversation_engine_gui;
}
