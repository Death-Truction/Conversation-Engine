package de.dai_labor.conversation_engine_core.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_core.interfaces.ISkillAnswer;
import de.dai_labor.conversation_engine_core.interfaces.SkillAnswer;

/**
 * A {@link ISkill skill} created only for test coverage
 * 
 * @author Marcel Engelmann
 *
 */
public class WeatherSkillWithEmptyResponseAndQuestions implements ISkill {

	public WeatherSkillWithEmptyResponseAndQuestions() {
	}

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language) {
		if (intent.equals("weather")) {
			return new SkillAnswer("SUCCESS", new ArrayList<String>(), false);
		}
		Map<String, String> questions = new HashMap<>();

		return new SkillAnswer("SUCCESS", questions);
	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return true;
	}

	@Override
	public void reset() {
		// nothing to do
	}

	@Override
	public List<String> getExampleRequests(String currentState, Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

}
