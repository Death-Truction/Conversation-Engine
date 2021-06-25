package interfaces;

import org.json.JSONObject;

public interface ISkill {

	/**
	 * 
	 * @param intent the intent of the user-input
	 * @return a confidence value between 0 and 100, whether the skill could be the
	 *         intended one based on the collected data
	 */
	public boolean canExecute(String intent, String currentState);

	public ISkillAnswer execute(String intent, JSONObject contextObject, JSONObject newEntities, String currentState);

	public void abort();

}
