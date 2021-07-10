package conversations_engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.json.JSONObject;

import interfaces.INLPAnswer;
import interfaces.INLPComponent;
import interfaces.ISkill;
import interfaces.ISkillAnswer;

/**
 * ConversationsEngine is a framework that is supposed to be used as a dialog
 * management system. It is designed to simplify the creation of chatbots by
 * combining {@link ISkill skills} and a single {@link INLPComponent
 * NLP-Component} into an all in one system.<br>
 * <br>
 * The system is based on a finite state machine to create an accessible testing
 * environment.
 * 
 * @author Marcel Engelmann
 *
 */
public class ConversationsEngine {

	private static final int DEFAULTTIMEOUTVALUE = 300;
	private static final String EMPTYCONTEXTOBJECT = "{}";

	private State currentState;
	private SkillStateMachine currentSkillStateMachine;
	private SkillStateMachine lastUsedSkillStateMachine;
	private PendingQuestions pendingSkillQuestions;
	private JSONObject contextObject;
	private Timer timer;
	private String lastIntent;
	private INLPComponent nlpComponent;
	private List<String> possibleSkillsForChooseSkillQuestion;
	private List<SkillStateMachine> allSkillStateMachines;
	private Deque<String> pendingIntents;
	private boolean wasLastQuestionChooseSkill;
	private boolean wasLastQuestionSkillQuestion;
	private boolean wasLastQuestionAbortQuestion;
	private boolean wasLastQuestionReturnToPreviousSkill;
	private boolean closed;
	private int timeoutInSeconds;

	/**
	 * Creates a new {@link ConversationsEngine} object
	 * 
	 * @param nlpComponent      the NLPComponent that handles the user input
	 * @param timeoutInSeconds  the number of seconds after which the
	 *                          {@link ConversationsEngine} will transition into the
	 *                          sleepState. The timer refreshes after each
	 *                          interaction.
	 * @param jsonContextObject the contextObject as JSON-String to start the
	 *                          {@link ConversationsEngine} with
	 * @throws IllegalArgumentException if the {@link INLPComponent} is null or the
	 *                                  timeout value is less than 1
	 */
	public ConversationsEngine(INLPComponent nlpComponent, int timeoutInSeconds, String jsonContextObject)
			throws IllegalArgumentException {
		if (nlpComponent == null) {
			Logging.error("INLPComponent is null");
			throw new IllegalArgumentException("INLPComponent is null");
		}

		if (timeoutInSeconds <= 0) {
			Logging.error("Timeout value must be greater than 0");
			throw new IllegalArgumentException("Timeout value must be greater than 0");
		}
		State defaultState = new State("defaultState");
		State sleepState = new State("sleepState");
		defaultState.addTransition(new Transition(sleepState, "SLEEP"));
		sleepState.addTransition(new Transition(defaultState, "WAKEUP"));
		this.currentState = defaultState;
		this.nlpComponent = nlpComponent;
		this.contextObject = new JSONObject(jsonContextObject);
		this.allSkillStateMachines = new ArrayList<>();
		this.possibleSkillsForChooseSkillQuestion = new ArrayList<>();
		this.pendingSkillQuestions = new PendingQuestions();
		this.pendingIntents = new ArrayDeque<>();
		this.lastIntent = "";
		this.wasLastQuestionChooseSkill = false;
		this.wasLastQuestionSkillQuestion = false;
		this.wasLastQuestionAbortQuestion = false;
		this.wasLastQuestionReturnToPreviousSkill = false;
		this.closed = false;
		this.timeoutInSeconds = timeoutInSeconds;
		I18n.setDefaultLanguage(new Locale("de", "DE"));
		// the list of intents the ConversationsEngine uses itself (as trigger words)
		List<String> triggerIntents = new ArrayList<>();
		triggerIntents.add("abort");
		triggerIntents.add("last");
		triggerIntents.add("all");
		triggerIntents.add("yes");
		triggerIntents.add("no");
		this.nlpComponent.addUsedIntents(triggerIntents);
		this.timer = new Timer();
		scheduleNewTimeoutTask();
	}

	/**
	 * Creates a new {@link ConversationsEngine} object with a default timeout of
	 * 300 seconds and an empty context object
	 * 
	 * @param nlpComponent the NLPComponent that handles the user input
	 */
	public ConversationsEngine(INLPComponent nlpComponent) {
		this(nlpComponent, DEFAULTTIMEOUTVALUE, EMPTYCONTEXTOBJECT);
	}

	/**
	 * Creates a new {@link ConversationsEngine} object with an empty context object
	 * 
	 * @param nlpComponent     the NLPComponent that handles the user input
	 * @param timeoutInSeconds the number of seconds after which the
	 *                         {@link ConversationsEngine} will transition into the
	 *                         sleepState. The timer refreshes after each
	 *                         interaction.
	 */
	public ConversationsEngine(INLPComponent nlpComponent, int timeoutInSeconds) {
		this(nlpComponent, timeoutInSeconds, EMPTYCONTEXTOBJECT);
	}

	/**
	 * Creates a new {@link ConversationsEngine} object with a default timeout of
	 * 300 seconds
	 * 
	 * @param nlpComponent      the NLPComponent that handles the user input
	 * @param jsonContextObject the contextObject as JSON-String to start the
	 *                          {@link ConversationsEngine} with
	 */
	public ConversationsEngine(INLPComponent nlpComponent, String jsonContextObject) {
		this(nlpComponent, DEFAULTTIMEOUTVALUE, jsonContextObject);
	}

	/**
	 * Returns the state machine's current state
	 * 
	 * @return the state machine's current state
	 */
	public String getState() {
		if (this.closed) {
			logIllegalAccess();
			return "";
		}
		if (this.currentSkillStateMachine != null && !"sleepState".equalsIgnoreCase(this.currentState.getName()))
			return this.currentSkillStateMachine.getCurrentState().getName();
		return this.currentState.getName();
	}

	/**
	 * Add a new Skill to the {@link ConversationsEngine}
	 * 
	 * @param skill            the skill to add to the {@link ConversationsEngine}
	 * @param jsonStateMachine the skill's state machine in JSON-Format. For the
	 *                         JSON-Schema please check out the <a href=
	 *                         "file:../../resources/SkillStateMachine_Schema.json">Schema.json</a>
	 *                         file
	 */
	public void addSkill(ISkill skill, String jsonStateMachine) {
		if (this.closed) {
			logIllegalAccess();
			return;
		}
		if (skill == null) {
			Logging.error("The skill to add to the ConversationsEngine is null");
			return;
		}

		if (jsonStateMachine.isBlank()) {
			Logging.error("The JSON-String for the skill to add to the ConversationsEngine is blank", skill);
			return;
		}
		SkillStateMachine newSkillStateMachine = GenerateSkillStateMachine.fromJson(skill, jsonStateMachine,
				nlpComponent);
		if (newSkillStateMachine == null) {
			Logging.error("Could not add the skill from the jsonString {}", jsonStateMachine);
			return;
		}

		if (allSkillStateMachines.stream().anyMatch(
				skillStateMachine -> skillStateMachine.getName().equalsIgnoreCase(newSkillStateMachine.getName()))) {
			Logging.error("The skill {} already exists", newSkillStateMachine.getName());
			return;
		}

		allSkillStateMachines.add(newSkillStateMachine);
	}

	/**
	 * Shuts this ConversationsEngine object down and returns the current context
	 * object to the given operation
	 * 
	 * @param operation the operation to call, with the context object passed as
	 *                  parameter, after shutting down.
	 */
	public void shutdown(Consumer<StringBuffer> operation) {
		if (this.closed) {
			logIllegalAccess();
			return;
		}
		Logging.debug("Shutting down the ConversationsEngine {}", this);
		this.timer.cancel();
		this.closed = true;
		if (operation == null) {
			Logging.warn("The consumer passed to the shutdown function was null");
			return;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.contextObject);
		this.contextObject = new JSONObject();
		operation.accept(buffer);
	}

	/**
	 * Processes a new input and returns a {@link List} of answers
	 * 
	 * @param input the input to process
	 * @return a {@link List} of answers
	 */
	public List<String> userInput(String input) {
		if (this.closed) {
			logIllegalAccess();
			return new ArrayList<>();
		}
		if (input == null || input.isBlank()) {
			Logging.warn("The user input was null or blank");
			UserOutput.addDefaultErrorMessage();
			return UserOutput.popNextOutput();
		}
		Logging.userInput(input);
		leaveSleepState();
		if (this.wasLastQuestionSkillQuestion) {
			processSkillQuestion(input);
		} else if (this.wasLastQuestionChooseSkill) {
			processChooseSkillQuestion(input);
		} else {
			processNormalRequest(input);
		}

		return UserOutput.popNextOutput();
	}

	/**
	 * Processes the input normally as a new input not relating to a question asked
	 * previously
	 * 
	 * @param input the user input
	 */
	private void processNormalRequest(String input) {
		processINLPAnswer(this.nlpComponent.understandInput(input, this.contextObject));

	}

	/**
	 * Processes the input normally as a new input not relating to a question asked
	 * previously
	 * 
	 * @param processedInput the {@link INLPAnswer} of a {@link INLPComponent}
	 */
	private void processINLPAnswer(INLPAnswer processedInput) {
		if (processedInput == null) {
			Logging.error("NLP Component's returned INLPAnswer is null");
			UserOutput.addDefaultErrorMessage();
			return;
		}
		List<String> intents = processedInput.getIntents();
		Locale foundLanguage = processedInput.getInputLanguage();
		boolean addedEntities = processedInput.hasAddedEntities();
		if (foundLanguage == null) {
			Logging.error("NLPComponent did not return a language");
		} else {
			I18n.setLanguage(foundLanguage);
		}
		// If the NLPAnswer has no result -> treat it as bad input
		if (!addedEntities && (intents == null || intents.isEmpty())) {
			UserOutput.addDefaultErrorMessage();
			return;
		}
		if (intents != null && !intents.isEmpty()) {
			// keep the correct input order for the stack/dequeue
			Collections.reverse(intents);
			this.pendingIntents.addAll(intents);
			if (this.wasLastQuestionAbortQuestion || this.wasLastQuestionReturnToPreviousSkill) {
				processSpecialQuestion();
				return;
			}
			if ("abort".equalsIgnoreCase(intents.get(0))) {
				this.pendingIntents.removeLast();
				abortRequested();
				return;
			}
		}
		evaluateNextAction();
	}

	private void processSpecialQuestion() {
		if (this.wasLastQuestionAbortQuestion) {
			this.wasLastQuestionAbortQuestion = false;
			processAbortQuestion();
		} else if (wasLastQuestionReturnToPreviousSkill) {
			this.wasLastQuestionReturnToPreviousSkill = false;
			processReturnToPreviousSkillQuestion();
		}

	}

	/**
	 * Tries to process the intent as an answer to an abort question <br>
	 * <br>
	 * If the answer was to abort the last skill, then the
	 * {@link #currentSkillStateMachine} will be
	 * {@link #resetCurrentSkillStateMachine reset} and the next action will be
	 * {@link #evaluateNextAction() evaluated} <br>
	 * 
	 * If the answer was to abort all skills, then the pipeline will be
	 * {@link #clearPipeline cleared} <br>
	 * If the abort question was not answered, the input will be processed
	 * {@link #processNormalRequest(String)}
	 * 
	 * @param intent the intent to process
	 */
	private void processAbortQuestion() {
		String intent = this.pendingIntents.peekLast();
		if ("last".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			resetCurrentSkillStateMachine(false);
			UserOutput.addOutputMessageFromLocalizationKey("BackToSkill", this.currentSkillStateMachine.getName());
			evaluateNextAction();
			return;
		}
		if ("all".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			this.clearPipeline();
			return;
		}
		askAbortQuestion();
	}

	/**
	 * Tries to process the intent as an answer to the "Return to the previous
	 * skill" question<br>
	 * <br>
	 * 
	 * If the answer was to return to the next skill, then the
	 * {@link #currentSkillStateMachine} will be
	 * {@link #resetCurrentSkillStateMachine(boolean) reset}<br>
	 * 
	 * If the answer was to abort all skills, then the pipeline will be
	 * {@link #clearPipeline() cleared}<br>
	 * 
	 * If the abort question was not answered, the input will be processed
	 * {@link #processNormalRequest normally}
	 * 
	 * @param intent the intent to process
	 * 
	 */
	private void processReturnToPreviousSkillQuestion() {
		String intent = this.pendingIntents.peekLast();
		if ("Yes".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			UserOutput.addOutputMessageFromLocalizationKey("BackToSkill", this.currentSkillStateMachine.getName());
			evaluateNextAction();
			return;
		}
		if ("No".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			resetCurrentSkillStateMachine(true);
			return;
		}
		askContinueLastSkill();
	}

	/**
	 * Tries to process the input as an answer to a skill question. If the question
	 * was not answered, the corresponding skill will have to ask the question again
	 * 
	 * @param input the input to process
	 */
	private void processSkillQuestion(String input) {
		this.wasLastQuestionSkillQuestion = false;
		String entityName = "";
		INLPAnswer processedInput;
		entityName = this.pendingSkillQuestions.getTopEntity(this.currentSkillStateMachine.getName());
		processedInput = this.nlpComponent.understandInput(input, entityName, this.contextObject);
		// Remove last asked question. If the question was not answered, then the
		// corresponding skill will have to ask the same question again
		this.pendingSkillQuestions.removeTopQuestionAndEntity(this.currentSkillStateMachine.getName());
		processINLPAnswer(processedInput);
	}

	/**
	 * Tries to process the input as an answer to a question to choose a skill<br>
	 * <br>
	 * If the input equals to a fitting skill, that skill will be executed and the
	 * last intent will be processed Otherwise the input will be processed as a
	 * {@link #processNormalRequest normal request}
	 * 
	 * @param input the input to process
	 */
	private void processChooseSkillQuestion(String input) {
		// match input to possible skills to choose from
		String nextSkill = this.possibleSkillsForChooseSkillQuestion.stream().filter(input::equalsIgnoreCase)
				.findFirst().orElse("");

		if (nextSkill.isEmpty()) {
			Logging.debug(
					"The question to choose a skill was not answered!\nPossible skills are: {}\nUser input is: {}",
					this.possibleSkillsForChooseSkillQuestion, input);
			askChooseSkillQuestion();
		} else {
			this.wasLastQuestionChooseSkill = false;
			final String skillName = nextSkill;
			leaveCurrentSkillStateMachine();
			this.currentSkillStateMachine = this.allSkillStateMachines.stream()
					.filter(skill -> skill.getName().equals(skillName)).findFirst().orElse(null);
			evaluateNextAction();
		}
	}

	/**
	 * Processes the next {@link #pendingIntents intent}
	 *
	 */
	private void processNextIntent() {
		String intent = pendingIntents.peekLast();
		Logging.debug("Processing the intent '{}'", intent);
		this.lastIntent = intent;
		SkillStateMachine nextSkillStateMachine = getNextSkillStateMachine(intent);
		if (nextSkillStateMachine == null) {
			return;
		}
		this.currentSkillStateMachine = nextSkillStateMachine;

		ISkillAnswer answer = this.currentSkillStateMachine.execute(intent, this.contextObject);
		if (answer == null) {
			UserOutput.addDefaultErrorMessage();
			return;
		}

		this.processSkillAnswer(answer);
		boolean skillMachineEnded = this.hasSkillStateMachineEnded();
		if (skillMachineEnded && this.currentSkillStateMachine != null) {
			wasLastQuestionReturnToPreviousSkill = true;
			askContinueLastSkill();
			return;
		}
		evaluateNextAction();
	}

	/**
	 * Processes a {@link ISkillAnswer} of a skill
	 * 
	 * @param skillAnswer the answer of a skill
	 */
	private void processSkillAnswer(ISkillAnswer skillAnswer) {
		boolean answerIsEmpty = true;
		if (skillAnswer.answers() != null && !skillAnswer.answers().isEmpty()) {
			UserOutput.addOutputMessages(skillAnswer.answers());
			answerIsEmpty = false;
		}
		if (skillAnswer.skipUserOutput()) {
			Logging.debug("Skipping user Output");
			return;
		}
		if (skillAnswer.requiredQuestionsToBeAnswered() == null
				|| skillAnswer.requiredQuestionsToBeAnswered().isEmpty()) {
			// remove last processed intent
			this.pendingIntents.removeLast();
			// no skill answer and no skill question
			if (answerIsEmpty) {
				Logging.error("The Skill {} returned no answer and no questions",
						this.currentSkillStateMachine.getName());
			}
			return;
		}
		Map<String, String> newQuestions = skillAnswer.requiredQuestionsToBeAnswered();
		for (Entry<String, String> entry : newQuestions.entrySet()) {
			this.pendingSkillQuestions.addQuestion(this.currentSkillStateMachine.getName(), entry.getKey(),
					entry.getValue());
		}
	}

	/**
	 * Evaluates the next possible action. <br>
	 */
	private void evaluateNextAction() {

		// if the intent is still the same and the skill for the intent has questions
		// left to ask of the user
		if (this.currentSkillStateMachine != null && !this.pendingIntents.isEmpty()
				&& this.lastIntent.equals(this.pendingIntents.peekLast())
				&& this.pendingSkillQuestions.getNumberOfQuestions(this.currentSkillStateMachine.getName()) > 0) {
			askNextQuestion();
			return;

		}

		if (!this.pendingIntents.isEmpty()) {
			processNextIntent();
		}
	}

	/**
	 * If {@link #lastUsedSkillStateMachine} is null, then the {@link #clearPipeline
	 * pipeline will be cleared} <br>
	 * If not then an abort question will be asked
	 */
	private void abortRequested() {
		// can only abort current skillSM
		if (this.lastUsedSkillStateMachine == null) {
			this.clearPipeline();
			return;
		}
		this.wasLastQuestionAbortQuestion = true;
		askAbortQuestion();
	}

	/**
	 * Resets the {@link ConversationsEngine} to the initial state, except for the
	 * {@link #contextObject}
	 */
	private void clearPipeline() {
		// reset both, the current and last skillStateMachine
		resetCurrentSkillStateMachine(true);
		this.pendingIntents.clear();
		this.pendingSkillQuestions.clear();
		wasLastQuestionChooseSkill = false;
		wasLastQuestionSkillQuestion = false;
		wasLastQuestionAbortQuestion = false;
		wasLastQuestionReturnToPreviousSkill = false;
		possibleSkillsForChooseSkillQuestion.clear();
		lastIntent = "";
	}

	/**
	 * Creates a new {@link Timer} that transitions the {@link ConversationsEngine}
	 * into the sleepState after {@link #timeoutInSeconds } seconds and if an old
	 * timer exists, cancels it
	 */
	private void scheduleNewTimeoutTask() {
		// This could be extended
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if ("defaultState".equals(currentState.getName())) {
					Logging.debug("Entering Sleep State");
					currentState = currentState.getNextState("SLEEP");
				}
			}
		};
		this.timer.cancel();
		this.timer = new Timer();
		this.timer.schedule(timerTask, this.timeoutInSeconds * 1000L);
	}

	/**
	 * Resets the {@link #timer}.<br>
	 * If the {@link ConversationsEngine} is currently in the sleepState, then it
	 * will transition to the defaultState
	 */
	private void leaveSleepState() {
		if ("sleepState".equals(this.currentState.getName())) {
			Logging.debug("Leaving Sleep State");
			this.currentState = this.currentState.getNextState("WAKEUP");
			UserOutput.addOutputMessageFromLocalizationKey("WelcomeBack");
		}

		scheduleNewTimeoutTask();
	}

	/**
	 * Adds the next question of a skill to the {@link UserOutput}
	 */
	private void askNextQuestion() {
		String nextQuestion = this.pendingSkillQuestions.getTopQuestion(this.currentSkillStateMachine.getName());
		UserOutput.addOutputQuestion(nextQuestion);
		this.wasLastQuestionSkillQuestion = true;
	}

	/**
	 * Adds the question of choosing a skill to the {@link UserOutput}
	 */
	private void askChooseSkillQuestion() {
		UserOutput.addOutputMessageFromLocalizationKey("MultipleSkills",
				this.possibleSkillsForChooseSkillQuestion.stream().collect(Collectors.joining(",")));
	}

	/**
	 * Adds the question, weather the user wants to abort the last or all skills, to
	 * the {@link UserOutput}
	 */
	private void askAbortQuestion() {
		UserOutput.addOutputMessageFromLocalizationKey("AbortQuestion");
	}

	/**
	 * Adds the question weather the user wants to continue the last skill, to the
	 * {@link UserOutput}
	 */
	private void askContinueLastSkill() {
		UserOutput.addOutputMessageFromLocalizationKey("ContinueLastSkill", this.currentSkillStateMachine.getName());
	}

	/**
	 * Determines the possible skills for a given intent
	 * 
	 * @param intent the intent thats supposed to be processed by a skill
	 * @return the {@link SkillStateMachine} to the corresponding skill, that can
	 *         execute the given intent. Or returns null if no or more than one
	 *         skill was found to process the intent
	 */
	private SkillStateMachine getNextSkillStateMachine(String intent) {

		if (this.currentSkillStateMachine != null && this.currentSkillStateMachine.canExecute(intent)) {
			return this.currentSkillStateMachine;
		}

		List<SkillStateMachine> possibleSkills = new ArrayList<>();
		for (SkillStateMachine ssm : allSkillStateMachines) {
			if (ssm.canExecute(intent)) {
				possibleSkills.add(ssm);
			}
		}

		if (possibleSkills.isEmpty()) {
			UserOutput.addOutputMessageFromLocalizationKey("NoSkillFound");
			Logging.debug("Could not find a skill to process the intent {}", intent);
			return null;
		}

		if (possibleSkills.size() > 1) {
			for (SkillStateMachine ssm : possibleSkills) {
				this.possibleSkillsForChooseSkillQuestion.add(ssm.getName());
			}
			askChooseSkillQuestion();
			Logging.debug("The intent {} can be processed by multiple skills: {}", intent,
					this.possibleSkillsForChooseSkillQuestion);
			this.wasLastQuestionChooseSkill = true;
			return null;
		}

		if (this.currentSkillStateMachine != null
				&& !possibleSkills.get(0).getName().equals(currentSkillStateMachine.getName())) {
			leaveCurrentSkillStateMachine();
		}
		return possibleSkills.get(0);

	}

	/**
	 * leaves the {@link #currentSkillStateMachine} and sets it as the
	 * {@link #lastUsedSkillStateMachine}
	 */
	private void leaveCurrentSkillStateMachine() {
		if (this.currentSkillStateMachine != null) {
			Logging.debug("Leaving the skill {}", this.currentSkillStateMachine.getName());
		}
		this.lastUsedSkillStateMachine = this.currentSkillStateMachine;
	}

	/**
	 * Resets the {@link #currentSkillStateMachine} and removes all pending
	 * questions relating to the skill
	 * 
	 * @param alsoResetLastUsedSkillStateMachine if true, then also resets the
	 *                                           {@link #lastUsedSkillStateMachine}
	 *                                           and removes all pending questions
	 *                                           relating to that skill
	 */
	private void resetCurrentSkillStateMachine(boolean alsoResetLastUsedSkillStateMachine) {
		if (this.currentSkillStateMachine == null) {
			return;
		}
		removeIntentsOfCurrentSkill();
		this.currentSkillStateMachine.reset();
		this.pendingSkillQuestions.removeAllSkillQuestions(this.currentSkillStateMachine.getName());
		this.wasLastQuestionSkillQuestion = false;
		this.currentSkillStateMachine = this.lastUsedSkillStateMachine;
		if (alsoResetLastUsedSkillStateMachine) {
			resetCurrentSkillStateMachine(false);
		}
	}

	/**
	 * Removes all {@link #pendingIntents} that the
	 * {@link #currentSkillStateMachine} can execute
	 */
	private void removeIntentsOfCurrentSkill() {
		Iterator<String> iterator = this.pendingIntents.iterator();
		while (iterator.hasNext()) {
			String intent = iterator.next();
			if (this.currentSkillStateMachine.canExecute(intent)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Checks weather the {@link #currentSkillStateMachine} has ended or not
	 * 
	 * @return true if the {@link #currentSkillStateMachine} has ended
	 */
	private boolean hasSkillStateMachineEnded() {
		boolean currentSkillStateMachineEnded = this.currentSkillStateMachine.hasEnded();
		if (currentSkillStateMachineEnded) {
			Logging.debug("The Skill {} Ended", this.currentSkillStateMachine.getName());
			resetCurrentSkillStateMachine(false);
			this.currentSkillStateMachine = this.lastUsedSkillStateMachine;
			this.lastUsedSkillStateMachine = null;
		}

		return currentSkillStateMachineEnded;
	}

	/**
	 * Logs that a method on this object was called after it has been shut down
	 */
	private void logIllegalAccess() {
		Logging.error("The ConversationsEngine was invoked after it has been shut down");
	}

}
