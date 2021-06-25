package interfaces;

import java.util.List;

import org.json.JSONObject;

public interface INLPComponent {

	void addUsedEntities(List<String> entities);

	void addUsedIntents(List<String> intents);

	INLPAnswer understandInput(String input, String entityName, JSONObject contextObject);

}