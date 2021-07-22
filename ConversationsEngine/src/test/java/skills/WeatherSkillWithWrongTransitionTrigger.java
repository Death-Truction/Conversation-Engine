package skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language) {
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

	@Override
	public List<String> getExampleRequests(String currentState, Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

}
