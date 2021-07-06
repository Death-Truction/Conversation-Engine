package skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import data.SkillAnswer;
import interfaces.ISkill;
import interfaces.ISkillAnswer;

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
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState) {
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

}
