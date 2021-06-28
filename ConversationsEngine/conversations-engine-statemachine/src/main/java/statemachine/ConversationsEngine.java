package statemachine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
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
 * The ConversationsEngineStateMachine is a ChatBot-Framework to TODO: finish
 * this
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
	private JSONObject newEntities;
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
	 */
	public ConversationsEngine(INLPComponent nlpComponent, int timeoutInSeconds, String jsonContextObject) {
		if (nlpComponent == null) {
			Logging.error("INLPComponent is null");
			throw new IllegalArgumentException("INLPComponent is null");
		}

		if (timeoutInSeconds < 0) {
			Logging.error("Timeout value is negative");
			throw new IllegalArgumentException("Timeout is negative");
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
		this.timer = new Timer();
		scheduleNewTimeoutTask();
		I18n.setLanguage(new Locale("de", "DE"));
	}

	/**
	 * Creates a new {@link ConversationsEngine} object
	 * 
	 * @param nlpComponent the NLPComponent that handles the user input
	 */
	public ConversationsEngine(INLPComponent nlpComponent) {
		this(nlpComponent, DEFAULTTIMEOUTVALUE, EMPTYCONTEXTOBJECT);
	}

	/**
	 * Creates a new {@link ConversationsEngine} object
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
	 * Creates a new {@link ConversationsEngine} object
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
		if (this.currentSkillStateMachine != null)
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
		this.timer.cancel();
		this.closed = true;
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.contextObject);
		this.contextObject = new JSONObject();
		operation.accept(buffer);
	}

	/**
	 * Processes a new input and returns a list of answers
	 * 
	 * @param input the input to process
	 * @return a list of answers
	 */
	public List<String> userInput(String input) {
		if (this.closed) {
			logIllegalAccess();
			return new ArrayList<>();
		}
		Logging.userInput(input);
		leaveSleepState();
		if (this.wasLastQuestionSkillQuestion) {
			this.wasLastQuestionSkillQuestion = false;
			processSkillQuestion(input);
		} else if (this.wasLastQuestionChooseSkill) {
			this.wasLastQuestionChooseSkill = false;
			processChooseSkillQuestion(input);
		} else {
			processNormalRequest(input);
		}

		return UserOutput.popNextOutput();
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
	 * {@link #checkNormalRequest}
	 * 
	 * @param intent the intent to process
	 */
	private void processAbortQuestion() {
		String intent = this.pendingIntents.peekLast();
		if (intent.equalsIgnoreCase("last")) {
			this.pendingIntents.removeLast();
			resetCurrentSkillStateMachine(false);
			UserOutput.addOutputMessageFromLocalizationKey("BackToSkill", this.currentSkillStateMachine.getName());
			evaluateNextAction();
			return;
		}
		if (intent.equalsIgnoreCase("all")) {
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
		if (intent.equalsIgnoreCase("Yes")) {
			this.pendingIntents.removeLast();
			UserOutput.addOutputMessageFromLocalizationKey("BackToSkill", this.currentSkillStateMachine.getName());
			evaluateNextAction();
			return;
		}
		if (intent.equalsIgnoreCase("No")) {
			this.pendingIntents.removeLast();
			resetCurrentSkillStateMachine(true);
			return;
		}
		askContinueLastSkill();
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
		this.newEntities = processedInput.getNewEntities();
		List<String> intents = processedInput.getIntents();
		Locale foundLanguage = processedInput.getInputLanguage();
		if (foundLanguage == null) {
			Logging.error("NLPComponent did not return a language");
		} else {
			I18n.setLanguage(foundLanguage);
		}

		// check whether the nlp component edited the contextObject wrong
		if (this.contextObject == null) {
			Logging.error("NLPComponent returned null as context object");
			UserOutput.addDefaultErrorMessage();
			return;
		}
		// If the NLPAnswer has no result -> treat it as bad input
		if ((this.newEntities == null || this.newEntities.isEmpty()) && (intents == null || intents.isEmpty())) {
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
			if (intents.get(0).equalsIgnoreCase("abort")) {
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
	 * Tries to process the input as an answer to a skill question. If the question
	 * was not answered, the corresponding skill will have to ask the question again
	 * 
	 * @param input the input to process
	 */
	private void processSkillQuestion(String input) {
		String entityName = "";
		INLPAnswer processedInput;
		entityName = this.pendingSkillQuestions.getTopEntity(this.currentSkillStateMachine.getName());
		processedInput = this.nlpComponent.understandInput(input, entityName, this.contextObject);
		if (processedInput == null) {
			Logging.error("NLP Component's returned INLPAnswer is null");
			UserOutput.addDefaultErrorMessage();
			return;
		}
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
		String nextSkill = this.possibleSkillsForChooseSkillQuestion.stream().filter(input::equals).findFirst()
				.orElse("");

		if (nextSkill.isEmpty()) {
			Logging.debug(
					"The question to choose a skill was not answered!\nPossible skills are: {}\nUser input is: {}",
					this.possibleSkillsForChooseSkillQuestion, input);
			askChooseSkillQuestion();
		} else {
			final String skillName = nextSkill;
			leaveCurrentSkillStateMachine();
			this.currentSkillStateMachine = this.allSkillStateMachines.stream()
					.filter(skill -> skill.getName().equals(skillName)).findFirst().orElse(null);
			evaluateNextAction();
		}
	}

	/**
	 * Tries to process the next {@link #pendingIntents intent}
	 * 
	 * @return true if successfully
	 */
	private boolean processNextIntentSuccessfully() {
		String intent = pendingIntents.peekLast();
		Logging.debug("Processing the intent '{}'", intent);
		this.lastIntent = intent;
		SkillStateMachine nextSkillStateMachine = getNextSkillStateMachine(intent);
		if (nextSkillStateMachine == null) {
			return false;
		}
		this.currentSkillStateMachine = nextSkillStateMachine;

		ISkillAnswer answer = this.currentSkillStateMachine.execute(intent, this.contextObject, this.newEntities);
		if (answer == null) {
			UserOutput.addDefaultErrorMessage();
			return false;
		}
		// check whether the skill edited the contextObject wrong
		if (this.contextObject == null) {
			Logging.error("The skill {} returned null as context object", this.currentSkillStateMachine.getName());
		}

		this.processSkillAnswer(answer);
		if (hasSkillStateMachineEnded() && this.currentSkillStateMachine != null) {
			wasLastQuestionReturnToPreviousSkill = true;
			askContinueLastSkill();
			return false;
		}
		return true;
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
				|| skillAnswer.requiredQuestionsToBeAnswered().size() == 0) {
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

		// a skill returned question or a choose skill question is about to be asked
		if (this.wasLastQuestionChooseSkill || this.wasLastQuestionSkillQuestion) {
			return;
		}
		if (!this.pendingIntents.isEmpty() && !this.lastIntent.equals(this.pendingIntents.peekLast())
				&& !processNextIntentSuccessfully()) {
			return;
		}
		if (this.currentSkillStateMachine == null
				|| this.pendingSkillQuestions.getNumberOfQuestions(this.currentSkillStateMachine.getName()) == 0) {
			if (this.pendingIntents.isEmpty() || !processNextIntentSuccessfully()) {
				return;
			}
		} else {
			askNextQuestion();
		}
		evaluateNextAction();
	}

	/**
	 * If {@link #lastUsedSkillStateMachine} is null, then pipeline will be
	 * {@link #clearPipeline cleared} <br>
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
				if (currentState.getName().equals("defaultState")) {
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
		if (this.currentState.getName().equals("sleepState")) {
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
		this.currentSkillStateMachine.reset();
		this.pendingSkillQuestions.removeAllSkillQuestions(this.currentSkillStateMachine.getName());
		this.wasLastQuestionSkillQuestion = false;
		this.currentSkillStateMachine = this.lastUsedSkillStateMachine;
		if (alsoResetLastUsedSkillStateMachine) {
			resetCurrentSkillStateMachine(false);
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
