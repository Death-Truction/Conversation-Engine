package de.dai_labor.conversation_engine_core.conversation_engine;

/**
 * Represents a question and the corresponding entity name
 * 
 * @author Marcel Engelmann
 *
 */
class QuestionTuple {
	private String entityName;
	private String question;

	/**
	 * Creates a new QuestionTuple object
	 * 
	 * @param entityName the name of the entity
	 * @param question   the question
	 */
	QuestionTuple(String entityName, String question) {
		this.entityName = entityName;
		this.question = question;
	}

	/**
	 * Returns the name of the entity
	 * 
	 * @return the name of the entity
	 */
	String getEntityName() {
		return this.entityName;
	}

	/**
	 * Returns the question
	 * 
	 * @return the question
	 */
	String getQuestion() {
		return this.question;
	}
}
