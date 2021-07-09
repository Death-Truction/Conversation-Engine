package skills;

import java.util.ArrayList;

import org.json.JSONObject;

import interfaces.ISkill;
import interfaces.ISkillAnswer;
import interfaces_implementation.SkillAnswer;

/**
 * A {@link ISkill skill} created only for test coverage
 * 
 * @author Marcel Engelmann
 *
 */
public class WeatherSkillWithWrongTransitionTrigger implements ISkill {

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState) {
		if (intent.equals("weather")) {
			return new SkillAnswer("STAY", new ArrayList<>(), false);
		}
		return null;
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
