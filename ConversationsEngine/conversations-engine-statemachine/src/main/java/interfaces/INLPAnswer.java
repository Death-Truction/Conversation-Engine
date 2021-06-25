package interfaces;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

public interface INLPAnswer {

	List<String> getIntents();

	JSONObject getNewEntities();

	Locale getInputLanguage();

}