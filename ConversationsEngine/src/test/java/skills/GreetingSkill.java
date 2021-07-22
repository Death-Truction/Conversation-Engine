package skills;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import interfaces.ISkill;
import interfaces.ISkillAnswer;
import interfaces_implementation.SkillAnswer;

/**
 * A {@link ISkill skill} that generates a greeting message
 * 
 * @author Marcel Engelmann
 *
 */
public class GreetingSkill implements ISkill {

	@Override
	public ISkillAnswer execute(String intent, JSONObject contextObject, String currentState, Locale language) {
		if (intent.equals("greeting")) {
			int currentHour = Calendar.HOUR_OF_DAY;
			List<String> dayTime = new ArrayList<>();
			if (currentHour <= 10) {
				dayTime.add("Guten Morgen!");
			} else if (currentHour <= 16) {
				dayTime.add("Guten Tag!");
			} else if (currentHour <= 19) {
				dayTime.add("Guten Abend!");
			} else {
				dayTime.add("Hallo!");
			}

			return new SkillAnswer("SUCCESS", dayTime, false);
		}

		return null;
	}

	@Override
	public boolean canExecute(String intent, String currentState) {
		return intent.equalsIgnoreCase("greeting");
	}

	@Override
	public void reset() {
		// nothing to do
	}

	@Override
	public List<String> getExampleRequests(String currentState, Locale locale) {
		ArrayList<String> possibleRequests = new ArrayList<>();
		possibleRequests.add("Hallo");
		return possibleRequests;
	}
}
