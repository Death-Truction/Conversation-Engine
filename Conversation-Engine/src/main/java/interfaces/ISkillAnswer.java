package interfaces;

import java.util.List;
import java.util.Map;

import conversation_engine.ConversationsEngine;

/**
 * This is an interface for the ISkillAnswer that is required for the
 * {@link ISkill skills} used by the {@link ConversationsEngine}
 * 
 * @author Marcel Engelmann
 *
 */
public interface ISkillAnswer {

	/**
	 * Returns the transition trigger that defines in which state the skill is
	 * supposed to transition to
	 * 
	 * @return the transition trigger that defines in which state the skill is
	 *         supposed to transition to
	 */
	String getTransitionTrigger();

	/**
	 * Returns a {@link Map} of questions that are supposed to be asked of the user.
	 * This Map has the format: &lt;entityName, question&gt;, where the entityName
	 * defines the entity that is supposed to be filled
	 * 
	 * @return a {@link Map} of questions that are supposed to be asked of the user.
	 *         This Map has the format: &lt;entityName, question&gt;, where the
	 *         entityName defines the entity that is supposed to be filled
	 */
	Map<String, String> requiredQuestionsToBeAnswered();

	/**
	 * Returns a {@link List list} of answers that are all returned back to the user
	 * 
	 * @return a {@link List list} of answers that are all returned back to the user
	 */
	List<String> answers();

	/**
	 * If the skill's state machine is supposed to transition from one state to
	 * another without responding to the user
	 * 
	 * @return true to skip the response to the user and rerun the skill (after the
	 *         transition to the next state)
	 */
	boolean skipUserOutput();

}
