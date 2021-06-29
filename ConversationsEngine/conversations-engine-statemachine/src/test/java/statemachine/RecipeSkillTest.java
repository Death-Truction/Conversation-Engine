package statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.NLPComponent;
import interfaces.INLPComponent;
import skills.RecipeCookingSkill;
import skills.RecipeSearchSkill;
import skills.RecipeSelectSkill;

class RecipeSkillTest {

	private ConversationsEngine myStateMachine;
	private INLPComponent nlp = new NLPComponent();

	@BeforeEach
	void init() {
		this.myStateMachine = new ConversationsEngine(nlp);
		RecipeSearchSkill recipeSkill = new RecipeSearchSkill();
		String recipeSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("RecipeSearch.json");
		this.myStateMachine.addSkill(recipeSkill, recipeSkillStateMachine);
		RecipeSelectSkill recipeSelectSkill = new RecipeSelectSkill();
		String recipeSelectSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("RecipeSelect.json");
		this.myStateMachine.addSkill(recipeSelectSkill, recipeSelectSkillStateMachine);
		RecipeCookingSkill recipeCookingSkill = new RecipeCookingSkill();
		String recipeCookingSkillStateMachine = TestHelperFunctions.loadJsonFileAsString("RecipeCooking.json");
		this.myStateMachine.addSkill(recipeCookingSkill, recipeCookingSkillStateMachine);
	}

	@Test
	@DisplayName("Request for 'Paprika'")
	void simpleCheck() {
		String answer = this.sendUserInput("Welche Rezepte mit Paprika gibt es?").get(0);
		assertTrue(answer.contains("Paprika") && answer.contains("Rezepte"));
	}

	@Test
	@DisplayName("Request for 'Paprika' and 'Kartoffeln'")
	void twoIngredients() {
		String answer = this.sendUserInput("Welche Rezepte mit Paprika und Kartoffeln gibt es?").get(0);
		assertTrue(answer.contains("Paprika") && answer.contains("Kartoffeln") && answer.contains("Rezepte"));
	}

	@Test
	@DisplayName("Missing Entities")
	void missingEntities() {
		String answer = sendUserInput("Welche Rezepte gibt es?").get(0);
		assertEquals("Welche Zutaten soll das Rezept beinhalten?", answer);
	}

	@Test
	@DisplayName("Missing Entities with user response")
	void missingEntitiesWithResponse() {
		String answer = sendUserInput("Welche Rezepte gibt es?").get(0);
		assertEquals("Welche Zutaten soll das Rezept beinhalten?", answer);
		answer = sendUserInput("Paprika").get(0);
		assertTrue(answer.contains("Paprika") && answer.contains("Rezepte"));
	}

	@Test
	@DisplayName("Select a Recipe")
	void selectARecipe() {
		String answer = sendUserInput("Zeige mir das Rezept Paprika mit Kartoffeln und Erbsen").get(0);
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answer);
	}

	@Test
	@DisplayName("Select a Recipe without giving a name")
	void selectARecipeWithoutName() {
		String answer = sendUserInput("Zeige mir das Rezept").get(0);
		assertEquals("Ich konnte das Rezept leider nicht finden!", answer);
	}

	@Test
	@DisplayName("Select a correct Recipe, then one without a name and approve to use the last selected one")
	void selectCorrectRecipeThenWithoutNameAndSelectLastUsed() {
		String answer = sendUserInput("Zeige mir das Rezept Paprika mit Kartoffeln und Erbsen").get(0);
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answer);
		answer = sendUserInput("Zeige mir das Rezept").get(0);
		assertEquals("Wollen Sie das zuletzt genutzte Rezept nutzen?", answer);
		answer = sendUserInput("Ja").get(0);
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answer);
	}

	@Test
	@DisplayName("Select a correct Recipe, then one without a name and disapprove to use the last selected one")
	void selectCorrectRecipeThenWithoutNameAndNotSelectLastUsed() {
		String answer = sendUserInput("Zeige mir das Rezept Paprika mit Kartoffeln und Erbsen").get(0);
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answer);
		answer = sendUserInput("Zeige mir das Rezept").get(0);
		assertEquals("Wollen Sie das zuletzt genutzte Rezept nutzen?", answer);
		answer = sendUserInput("Nein").get(0);
		assertEquals("Das zuletzt genutzte Rezept wurde NICHT ausgewählt!", answer);
	}

	@Test
	@DisplayName("Select a wrong Recipe")
	void selectAWrongRecipe() {
		String answer = sendUserInput("Zeige mir das Rezept ich existiere nicht.").get(0);
		assertEquals("Ich konnte das Rezept leider nicht finden!", answer);
	}

	@Test
	@DisplayName("Search with ingredient that belongs to no recipe")
	void searchIngredientsNoRecipe() {
		String answer = sendUserInput("Welche Rezepte gibt es mit Toast?").get(0);
		assertEquals("Ich konnte leider keine passende Rezepte finden!", answer);
	}

	@Test
	@DisplayName("Request cooking a recipe")
	void cookingRequest() {
		List<String> answer = sendUserInput("Ich möchte das Rezept Paprika mit Kartoffeln und Erbsen kochen");
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answer.get(0));
		assertEquals("Haben Sie die Zutat Kartoffeln zu Hause?", answer.get(1));
	}

	@Test
	@DisplayName("Request cooking a recipe and answering available ingredients questions")
	void cookingRequestAndAnsweringQuestions() {
		List<String> answers = sendUserInput("Ich möchte das Rezept Paprika mit Kartoffeln und Erbsen kochen");
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answers.get(0));
		assertEquals("Haben Sie die Zutat Kartoffeln zu Hause?", answers.get(1));
		answers = sendUserInput("Ja");
		assertEquals("Haben Sie die Zutat Erbsen zu Hause?", answers.get(0));
		answers = sendUserInput("Ja");
		assertEquals("Haben Sie die Zutat Paprika zu Hause?", answers.get(0));
		answers = sendUserInput("Ja");
		assertEquals("Sie haben alle benötigten Zutaten", answers.get(0));
		assertEquals("Der 1. Schritt: Kartoffeln schälen und kochen", answers.get(1));
	}

	@Test
	@DisplayName("add Ingredients, then request cooking a recipe")
	void addIngredientsThenCookingRequest() {
		List<String> answers = sendUserInput("Ich habe die Zutaten Kartoffeln, Paprika und Erbsen");
		assertEquals("Was möchten Sie als nächstes machen?", answers.get(0));
		answers = sendUserInput("Ich möchte das Rezept Paprika mit Kartoffeln und Erbsen kochen");
		assertEquals("Das Rezept \"paprika mit kartoffeln und erbsen\" wurde erfolgreich ausgewählt.", answers.get(0));
		assertEquals("Sie haben alle benötigten Zutaten", answers.get(1));
		assertEquals("Der 1. Schritt: Kartoffeln schälen und kochen", answers.get(2));
		answers = sendUserInput("Nächster Schritt");
		assertEquals("Der 2. Schritt: Paprika waschen und klein schneiden", answers.get(0));
		answers = sendUserInput("Nächster Schritt");
		assertEquals("Der letzte Schritt: Alles kochen", answers.get(0));
		assertEquals("Guten Appetit!", answers.get(1));
	}

	private List<String> sendUserInput(String input) {
		return this.myStateMachine.userInput(input);
	}
}
