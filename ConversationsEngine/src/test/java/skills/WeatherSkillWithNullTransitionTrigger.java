package skills;

import java.util.ArrayList;

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
public class WeatherSkillWithNullTransitionTrigger implements ISkill {

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState) {
		if (intent.equals("weather")) {

			return new SkillAnswer(null, new ArrayList<>(), false);
		}
		return new SkillAnswer("SUCCESS", new ArrayList<>(), false);
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