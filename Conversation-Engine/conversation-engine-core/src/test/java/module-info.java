module de.dai_labor.conversation_engine_core {
	exports de.dai_labor.conversation_engine_core.playground;
	exports de.dai_labor.conversation_engine_core.interfaces;
	exports de.dai_labor.conversation_engine_core.conversation_engine;
	exports de.dai_labor.conversation_engine_core.skills;

	requires logback.classic;
	requires logback.core;
	requires org.everit.json.schema;
	requires org.json;
	requires org.junit.jupiter.api;
	requires org.junit.jupiter.params;
	requires org.slf4j;
}