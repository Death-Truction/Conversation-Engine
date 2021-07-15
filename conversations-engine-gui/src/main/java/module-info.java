module de.dai_labor.conversations_engine_gui {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;

	opens de.dai_labor.conversations_engine_gui.controllers to javafx.fxml;

	exports de.dai_labor.conversations_engine_gui;
}
