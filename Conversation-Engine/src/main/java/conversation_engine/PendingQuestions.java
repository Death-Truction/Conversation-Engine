package conversation_engine;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all pending questions, for each skill, which need to be answered by
 * the user
 * 
 * @author Marcel Engelmann
 *
 */
class PendingQuestions {
	private Map<String, ArrayDeque<QuestionTuple>> questions;

	/**
	 * Creates a new PendingQuestions object
	 */
	PendingQuestions() {
		this.questions = new HashMap<>();
	}

	/**
	 * Add a new question to the list
	 * 
	 * @param skillName  the skill that asked the question
	 * @param entityName the entity name corresponding to the question
	 * @param question   the question to be asked of the user
	 */
	void addQuestion(String skillName, String entityName, String question) {
		QuestionTuple newQuestion = new QuestionTuple(entityName, question);
		this.questions.computeIfAbsent(skillName, key -> new ArrayDeque<>());
		this.questions.get(skillName).add(newQuestion);
	}

	/**
	 * Returns the first question for a given skill
	 * 
	 * @param skillName the name of the skill
	 * @return the first question to be asked of the user
	 */
	String getTopQuestion(String skillName) {
		return this.questions.get(skillName).peekLast().getQuestion();
	}

	/**
	 * Returns the first entity name for a given skill
	 * 
	 * @param skillName the name of the skill
	 * @return the entity name of the first question
	 */
	String getTopEntity(String skillName) {
		return this.questions.get(skillName).peekLast().getEntityName();
	}

	/**
	 * Removes the first question and entity name for a given skill
	 * 
	 * @param skillName the name of the skill
	 */
	void removeTopQuestionAndEntity(String skillName) {
		this.questions.get(skillName).removeLast();
	}

	/**
	 * Removes all questions and entity names for a given skill
	 * 
	 * @param skillName the name of the skill
	 */
	void removeAllSkillQuestions(String skillName) {
		this.questions.remove(skillName);
	}

	/**
	 * Returns the number of open questions for a given skill
	 * 
	 * @param skillName the name of the skill
	 * @return the number of open questions
	 */
	int getNumberOfQuestions(String skillName) {
		if (this.questions.get(skillName) != null) {
			return this.questions.get(skillName).size();
		}
		return 0;
	}

	/**
	 * Clears all open questions
	 */
	public void clear() {
		this.questions.clear();

	}
}
