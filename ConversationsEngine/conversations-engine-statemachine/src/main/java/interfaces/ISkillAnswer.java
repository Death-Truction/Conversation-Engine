package interfaces;

import java.util.List;
import java.util.Map;

public interface ISkillAnswer {

	public String getTransitionTrigger();

	public Map<String, String> requiredQuestionsToBeAnswered();

	public List<String> answers();

	/**
	 * If the skill's state machine is supposed to transition from one state to
	 * another without responding to the user
	 * 
	 * @return true to skip user output and rerun the skill (after the transition to
	 *         the next state)
	 */
	public boolean skipUserOutput();

}
