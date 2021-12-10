package de.dai_labor.conversation_engine_core.conversation_engine;

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

import de.dai_labor.conversation_engine_core.interfaces.INLPAnswer;
import de.dai_labor.conversation_engine_core.interfaces.INLPComponent;
import de.dai_labor.conversation_engine_core.interfaces.ISkill;
import de.dai_labor.conversation_engine_core.interfaces.ISkillAnswer;

/**
 * ConversationEngine is a framework that is supposed to be used as a dialog
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
public class ConversationEngine {

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
	 * Creates a new {@link ConversationEngine} object
	 *
	 * @param nlpComponent      the NLPComponent that handles the user input
	 * @param timeoutInSeconds  the number of seconds after which the
	 *                          {@link ConversationEngine} will transition into the
	 *                          sleepState. The timer refreshes after each
	 *                          interaction.
	 * @param jsonContextObject the contextObject as JSON-String to start the
	 *                          {@link ConversationEngine} with
	 * @param defaultLanguage   the default language to use as backup
	 * @throws IllegalArgumentException if the {@link INLPComponent} is null or the
	 *                                  timeout value is less than 1
	 */
	public ConversationEngine(INLPComponent nlpComponent, int timeoutInSeconds, String jsonContextObject,
			Locale defaultLanguage) throws IllegalArgumentException {
		if (nlpComponent == null) {
			Logging.error("INLPComponent is null");
			throw new IllegalArgumentException("INLPComponent is null");
		}

		if (timeoutInSeconds <= 0) {
			Logging.error("Timeout value must be greater than 0");
			throw new IllegalArgumentException("Timeout value must be greater than 0");
		}

		if (defaultLanguage == null) {
			Logging.error("The default language locale is null");
			throw new IllegalArgumentException("The default language locale is null");
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
		I18n.setDefaultLanguage(defaultLanguage);
		// the list of intents the ConversationEngine uses itself (as trigger words)
		List<String> triggerIntents = new ArrayList<>();
		triggerIntents.add("abort");
		triggerIntents.add("last");
		triggerIntents.add("all");
		triggerIntents.add("yes");
		triggerIntents.add("no");
		this.nlpComponent.addUsedIntents(triggerIntents);
		this.timer = new Timer();
		this.scheduleNewTimeoutTask();
	}

	/**
	 * Creates a new {@link ConversationEngine} object with a default timeout of 300
	 * seconds and an empty context object
	 *
	 * @param nlpComponent    the NLPComponent that handles the user input
	 * @param defaultLanguage the default language to use as backup
	 */
	public ConversationEngine(INLPComponent nlpComponent, Locale defaultLanguage) {
		this(nlpComponent, DEFAULTTIMEOUTVALUE, EMPTYCONTEXTOBJECT, defaultLanguage);
	}

	/**
	 * Creates a new {@link ConversationEngine} object with an empty context object
	 *
	 * @param nlpComponent     the NLPComponent that handles the user input
	 * @param timeoutInSeconds the number of seconds after which the
	 *                         {@link ConversationEngine} will transition into the
	 *                         sleepState. The timer refreshes after each
	 *                         interaction.
	 * @param defaultLanguage  the default language to use as backup
	 */
	public ConversationEngine(INLPComponent nlpComponent, int timeoutInSeconds, Locale defaultLanguage) {
		this(nlpComponent, timeoutInSeconds, EMPTYCONTEXTOBJECT, defaultLanguage);
	}

	/**
	 * Creates a new {@link ConversationEngine} object with a default timeout of 300
	 * seconds
	 *
	 * @param nlpComponent      the NLPComponent that handles the user input
	 * @param jsonContextObject the contextObject as JSON-String to start the
	 *                          {@link ConversationEngine} with
	 * @param defaultLanguage   the default language to use as backup
	 */
	public ConversationEngine(INLPComponent nlpComponent, String jsonContextObject, Locale defaultLanguage) {
		this(nlpComponent, DEFAULTTIMEOUTVALUE, jsonContextObject, defaultLanguage);
	}

	/**
	 * Returns the state machine's current state
	 *
	 * @return the state machine's current state
	 */
	public String getState() {
		if (this.closed) {
			this.logIllegalAccess();
			return "";
		}
		if (this.currentSkillStateMachine != null && !"sleepState".equalsIgnoreCase(this.currentState.getName())) {
			return this.currentSkillStateMachine.getCurrentState().getName();
		}
		return this.currentState.getName();
	}

	/**
	 * Add a new Skill to the {@link ConversationEngine}
	 *
	 * @param skill            the skill to add to the {@link ConversationEngine}
	 * @param jsonStateMachine the skill's state machine in JSON-Format. For the
	 *                         JSON-Schema please check out the <a href=
	 *                         "file:../../resources/SkillStateMachine_Schema.json">Schema.json</a>
	 *                         file
	 */
	public void addSkill(ISkill skill, String jsonStateMachine) {
		if (this.closed) {
			this.logIllegalAccess();
			return;
		}
		if (skill == null) {
			Logging.error("The skill to add to the Conversation Engine is null");
			return;
		}

		if (jsonStateMachine.isBlank()) {
			Logging.error("The JSON-String for the skill to add to the Conversation Engine is blank", skill);
			return;
		}
		SkillStateMachine newSkillStateMachine = GenerateSkillStateMachine.fromJson(skill, jsonStateMachine,
				this.nlpComponent);
		if (newSkillStateMachine == null) {
			Logging.error("Could not add the skill from the jsonString {}", jsonStateMachine);
			return;
		}

		if (this.allSkillStateMachines.stream().anyMatch(
				skillStateMachine -> skillStateMachine.getName().equalsIgnoreCase(newSkillStateMachine.getName()))) {
			Logging.error("The skill {} already exists", newSkillStateMachine.getName());
			return;
		}

		this.allSkillStateMachines.add(newSkillStateMachine);
	}

	/**
	 * Shuts this ConversationEngine object down and invokes the given Consumer
	 * operation with the current context object as a StringBuilder
	 *
	 * @param operation the operation to call, with the context object passed as
	 *                  parameter, after shutting down.
	 */
	public void shutdown(Consumer<StringBuilder> operation) {
		if (this.closed) {
			this.logIllegalAccess();
			return;
		}
		Logging.debug("Shutting down the Conversation Engine {}", this);
		this.timer.cancel();
		this.closed = true;
		if (operation == null) {
			Logging.warn("The consumer passed to the shutdown function was null");
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(this.contextObject);
		this.contextObject = new JSONObject();
		operation.accept(sb);
	}

	/**
	 * Processes a new input and returns a {@link List} of answers
	 *
	 * @param input the input to process
	 * @return a {@link List} of answers
	 */
	public List<String> userInput(String input) {
		if (this.closed) {
			this.logIllegalAccess();
			return new ArrayList<>();
		}
		if (input == null || input.isBlank()) {
			Logging.warn("The user input was null or blank");
			this.defaultErrorUserOuput();
			return UserOutput.popNextOutput();
		}
		Logging.userInput(input);
		this.leaveSleepState();
		if (this.wasLastQuestionSkillQuestion) {
			this.processSkillQuestion(input);
		} else if (this.wasLastQuestionChooseSkill) {
			this.processChooseSkillQuestion(input);
		} else {
			this.processNormalRequest(input);
		}
		String withinCurrentSkill = "";
		if (this.currentSkillStateMachine != null) {
			withinCurrentSkill = " within the skill " + this.currentSkillStateMachine.getName();
		}
		Logging.debug("The Conversation Engine is currently in the state: {}{}", this.getState(), withinCurrentSkill);
		return UserOutput.popNextOutput();
	}

	/**
	 * Processes the input normally as a new input not related to a question asked
	 * previously
	 *
	 * @param input the user input
	 */
	private void processNormalRequest(String input) {
		this.processINLPAnswer(this.nlpComponent.understandInput(input, this.contextObject));

	}

	/**
	 * Processes the returned {@link INLPAnswer} of the {@link INLPComponent}
	 *
	 * @param processedInput the {@link INLPAnswer} of the {@link INLPComponent}
	 */
	private void processINLPAnswer(INLPAnswer processedInput) {
		if (processedInput == null) {
			Logging.error("NLP Component's returned INLPAnswer is null");
			this.defaultErrorUserOuput();
			return;
		}
		List<String> intents = processedInput.getIntents();
		Locale foundLanguage = processedInput.getInputLanguage();
		boolean addedEntities = processedInput.hasAddedEntities();
		if (foundLanguage == null) {
			Logging.warn("NLPComponent did not return a language");
		} else {
			I18n.setLanguage(foundLanguage);
		}
		// If the NLPAnswer has no result -> treat it as bad input
		if (!addedEntities && (intents == null || intents.isEmpty())) {
			this.defaultErrorUserOuput();
			return;
		}
		if (intents != null && !intents.isEmpty()) {
			// keep the correct input order for the stack/dequeue
			Collections.reverse(intents);
			this.pendingIntents.addAll(intents);
			if (this.wasLastQuestionAbortQuestion || this.wasLastQuestionReturnToPreviousSkill) {
				this.processSpecialQuestion();
				return;
			}
			if ("abort".equalsIgnoreCase(intents.get(0))) {
				this.pendingIntents.removeLast();
				this.abortRequested();
				return;
			}
		}
		this.evaluateNextAction();
	}

	private void processSpecialQuestion() {
		if (this.wasLastQuestionAbortQuestion) {
			this.wasLastQuestionAbortQuestion = false;
			this.processAbortQuestion();
		} else if (this.wasLastQuestionReturnToPreviousSkill) {
			this.wasLastQuestionReturnToPreviousSkill = false;
			this.processReturnToPreviousSkillQuestion();
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
			this.resetCurrentSkillStateMachine(false);
			UserOutput.addOutputMessageFromLocalizationKey("BackToSkill", this.currentSkillStateMachine.getName());
			this.evaluateNextAction();
			return;
		}
		if ("all".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			this.clearPipeline();
			return;
		}
		this.askAbortQuestion();
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
			this.evaluateNextAction();
			return;
		}
		if ("No".equalsIgnoreCase(intent)) {
			this.pendingIntents.removeLast();
			this.resetCurrentSkillStateMachine(true);
			return;
		}
		this.askContinueLastSkill();
	}

	/**
	 * Tries to process the input as an answer to a skill question. If the question
	 * was not answered, the corresponding skill will have to ask the question again
	 *
	 * @param input the input to process
	 */
	private void processSkillQuestion(String input) {
		INLPAnswer processedInput;
		String entityName = this.pendingSkillQuestions.getTopEntity(this.currentSkillStateMachine.getName());
		processedInput = this.nlpComponent.understandInput(input, entityName, this.contextObject);
		if (processedInput.hasAddedEntities()) {
			// Remove last asked question. If the question was not answered, then the
			// corresponding skill will have to ask the same question again
			this.wasLastQuestionSkillQuestion = false;
			this.pendingSkillQuestions.removeTopQuestionAndEntity(this.currentSkillStateMachine.getName());
		}
		this.processINLPAnswer(processedInput);
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
			this.askChooseSkillQuestion();
		} else {
			this.wasLastQuestionChooseSkill = false;
			this.leaveCurrentSkillStateMachine();
			this.currentSkillStateMachine = this.allSkillStateMachines.stream()
					.filter(skill -> skill.getName().equals(nextSkill)).findFirst().orElse(null);
			this.evaluateNextAction();
			this.possibleSkillsForChooseSkillQuestion.clear();
		}
	}

	/**
	 * Processes the next {@link #pendingIntents intent}
	 *
	 */
	private void processNextIntent() {
		String intent = this.pendingIntents.peekLast();
		Logging.debug("Processing the intent '{}'", intent);
		this.lastIntent = intent;
		SkillStateMachine nextSkillStateMachine = this.getNextSkillStateMachine(intent);
		if (nextSkillStateMachine == null) {
			return;
		}
		this.currentSkillStateMachine = nextSkillStateMachine;

		ISkillAnswer answer = this.currentSkillStateMachine.execute(intent, this.contextObject, I18n.getLanguage());
		if (answer == null) {
			this.defaultErrorUserOuput();
			return;
		}

		this.processSkillAnswer(answer);
		boolean skillMachineEnded = this.hasSkillStateMachineEnded();
		if (skillMachineEnded && this.currentSkillStateMachine != null) {
			this.wasLastQuestionReturnToPreviousSkill = true;
			this.askContinueLastSkill();
			return;
		}
		this.evaluateNextAction();
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
			String entityName = entry.getKey();
			String question = entry.getValue();
			this.pendingSkillQuestions.addQuestion(this.currentSkillStateMachine.getName(), entityName, question);
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
			this.askNextQuestion();
			return;

		}

		if (!this.pendingIntents.isEmpty()) {
			this.processNextIntent();
		}
	}

	/**
	 * If {@link #lastUsedSkillStateMachine} is null, then the {@link #clearPipeline
	 * pipeline will be cleared} <br>
	 * If not then an {@link #askAbortQuestion abort question} will be asked
	 */
	private void abortRequested() {
		// can only abort current skill state machine
		if (this.lastUsedSkillStateMachine == null) {
			this.clearPipeline();
			return;
		}
		this.wasLastQuestionAbortQuestion = true;
		this.askAbortQuestion();
	}

	/**
	 * Resets the {@link ConversationEngine} to the initial state, except for the
	 * {@link #contextObject}
	 */
	private void clearPipeline() {
		// reset both, the current and last skillStateMachine
		this.resetCurrentSkillStateMachine(true);
		this.pendingIntents.clear();
		this.pendingSkillQuestions.clear();
		this.wasLastQuestionChooseSkill = false;
		this.wasLastQuestionSkillQuestion = false;
		this.wasLastQuestionAbortQuestion = false;
		this.wasLastQuestionReturnToPreviousSkill = false;
		this.possibleSkillsForChooseSkillQuestion.clear();
		this.lastIntent = "";
	}

	/**
	 * Creates a new {@link Timer} that transitions the {@link ConversationEngine}
	 * into the sleepState after {@link #timeoutInSeconds } seconds and if an old
	 * timer exists, cancels it
	 */
	private void scheduleNewTimeoutTask() {
		// This could be extended
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if ("defaultState".equals(ConversationEngine.this.currentState.getName())) {
					Logging.debug("Entering Sleep State");
					ConversationEngine.this.currentState = ConversationEngine.this.currentState.getNextState("SLEEP");
				}
			}
		};
		this.timer.cancel();
		this.timer = new Timer();
		this.timer.schedule(timerTask, this.timeoutInSeconds * 1000L);
	}

	/**
	 * Resets the {@link #timer}.<br>
	 * If the {@link ConversationEngine} is currently in the sleepState, then it
	 * will transition to the defaultState
	 */
	private void leaveSleepState() {
		if ("sleepState".equals(this.currentState.getName())) {
			Logging.debug("Leaving Sleep State");
			this.currentState = this.currentState.getNextState("WAKEUP");
			UserOutput.addOutputMessageFromLocalizationKey("WelcomeBack");
		}

		this.scheduleNewTimeoutTask();
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
	 * Adds the question, whether the user wants to abort the last or all skills, to
	 * the {@link UserOutput}
	 */
	private void askAbortQuestion() {
		UserOutput.addOutputMessageFromLocalizationKey("AbortQuestion");
	}

	/**
	 * Adds the question whether the user wants to continue the last skill, to the
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
		for (SkillStateMachine ssm : this.allSkillStateMachines) {
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
			this.askChooseSkillQuestion();
			Logging.debug("The intent {} can be processed by multiple skills: {}", intent,
					this.possibleSkillsForChooseSkillQuestion);
			this.wasLastQuestionChooseSkill = true;
			return null;
		}

		if (this.currentSkillStateMachine != null) {
			this.leaveCurrentSkillStateMachine();
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
		this.currentSkillStateMachine = null;
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
		this.removeIntentsOfCurrentSkill();
		this.currentSkillStateMachine.reset();
		this.pendingSkillQuestions.removeAllSkillQuestions(this.currentSkillStateMachine.getName());
		this.wasLastQuestionSkillQuestion = false;
		this.currentSkillStateMachine = this.lastUsedSkillStateMachine;
		this.lastUsedSkillStateMachine = null;
		if (alsoResetLastUsedSkillStateMachine) {
			this.resetCurrentSkillStateMachine(false);
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
	 * Checks whether the {@link #currentSkillStateMachine} has ended or not
	 *
	 * @return true if the {@link #currentSkillStateMachine} has ended
	 */
	private boolean hasSkillStateMachineEnded() {
		boolean currentSkillStateMachineEnded = this.currentSkillStateMachine.hasEnded();
		if (currentSkillStateMachineEnded) {
			Logging.debug("The Skill {} Ended", this.currentSkillStateMachine.getName());
			this.resetCurrentSkillStateMachine(false);
		}

		return currentSkillStateMachineEnded;
	}

	/**
	 * Logs that a method on this object was called after it has been shut down
	 */
	private void logIllegalAccess() {
		Logging.error("The Conversation Engine was invoked after it has been shut down");
	}

	private void defaultErrorUserOuput() {
		UserOutput.addDefaultErrorMessage();
		if (this.currentSkillStateMachine != null) {
			List<String> possibleRequest = this.currentSkillStateMachine.getExampleRequests(I18n.getLanguage());
			if (possibleRequest != null && !possibleRequest.isEmpty()) {
				UserOutput.addOutputMessages(possibleRequest);
			}
		} else {
			// Get all possible requests from all skills
			List<String> allPossibleRequests = new ArrayList<>(this.allSkillStateMachines.size());
			List<String> selectedPossibleRequests = new ArrayList<>(3);
			for (SkillStateMachine skill : this.allSkillStateMachines) {
				allPossibleRequests.addAll(skill.getExampleRequests(I18n.getLanguage()));
			}
			// Select 3 possible requests
			Collections.shuffle(allPossibleRequests);
			for (String possibleRequest : allPossibleRequests) {
				selectedPossibleRequests.add(possibleRequest);
				if (selectedPossibleRequests.size() == 3) {
					break;
				}
			}
			UserOutput.addOutputMessageFromLocalizationKey("PossibleInputs");
			UserOutput.addOutputMessages(selectedPossibleRequests);

		}
		if (this.wasLastQuestionSkillQuestion) {
			this.askNextQuestion();
		}
	}

}
