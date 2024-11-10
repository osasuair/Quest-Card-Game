package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSteps {

    private static final int NUM_PLAYERS = 4;
    private static final int MAX_HAND_SIZE = 12;
    private static final String NO_INPUT = "n\n";
    private static final String YES_INPUT = "y\n";
    private static final String QUIT_INPUT = "Quit\n";

    private Game game;
    private final Scanner input = new Scanner(System.in);
    private final PrintWriter output = new PrintWriter(System.out);
    private final int[] previousShields = new int[4];
    private final int[] previousCards = new int[4];
    private QuestDetails questDetails;

    /**
     * Generates input for players building the stages of a quest.
     *
     * @param sponsor      The player sponsoring the quest.
     * @param stages       The number of stages in the quest.
     * @param stageCardMap A list of maps representing the cards for each stage.
     * @return A string representing the input for the game.
     */
    private static String getStageBuildInput(Player sponsor, int stages, List<Map<String, String>> stageCardMap) {
        StringBuilder input = new StringBuilder();
        List<Card> tempHand = new ArrayList<>(sponsor.getDeck().asList());

        for (int i = 0; i < stages; i++) {
            String cards = stageCardMap.get(i).get("cards");
            String indicesInput = convertCardListToInput(tempHand, cards);  // Convert to string with newline separator
            input.append(indicesInput).append(QUIT_INPUT);
        }

        return input.toString();
    }

    /**
     * Converts a string of cards to an input string of indices in the player's hand.
     *
     * @param tempHand The player's hand.
     * @param cards    A string representing the cards to find (e.g., "[F5, F10]").
     * @return A string representing the indices of the cards in the player's hand.
     */
    private static String convertCardListToInput(List<Card> tempHand, String cards) {
        return findCardIndicesInHand(tempHand, cards).stream()
                .map(String::valueOf) // Convert to List of indices strings
                .reduce("", (a, b) -> a + b + "\n");
    }

    /**
     * Finds the indices of the specified cards in the player's hand assuming the cards are removed.
     *
     * @param hand   The player's hand.
     * @param cards  A string representing the cards to find (e.g., "[F5, F10]").
     * @return A list of unique integers representing the indices of the cards in the player's hand.
     */
    private static List<Integer> findCardIndicesInHand(List<Card> hand, String cards) {
        List<Integer> cardIndices = new ArrayList<>();
        List<String> cardList = new ArrayList<>(hand.stream().map(Card::toString).toList());

        for (String card : parseStringList(cards)) {
            if (cardList.contains(card)) {
                int index = cardList.indexOf(card);
                cardIndices.add(index);
                hand.remove(index);
                cardList.remove(index);
            }
        }
        return cardIndices;
    }

    /**
     * Parses a string representing a list of items into a list of strings.
     *
     * @param listStr A string representing a list of items (e.g., "[F5, F10]").
     * @return A list of strings.
     */
    private static List<String> parseStringList(String listStr) {
        listStr = listStr.substring(1, listStr.length()-1);
        if (listStr.isEmpty()) {
            return List.of(); // Return an empty list
        } else {
            return List.of(listStr.split(", "));
        }
    }

    /**
     * Generates input for players building their attacks for a quest stage.
     *
     * @param players    A list of players in the game.
     * @param attackMap  A list of maps representing the attacks for each player. e.g [{"player": "P1", "cards": "F5, F10"}]
     * @return A string representing the input for the game.
     */
    private static String getAttackBuildInput(Player[] players, List<Map<String, String>> attackMap) {
        StringBuilder input = new StringBuilder();

        for (Map<String, String> attack: attackMap) {
            String player = attack.get("player");
            String cards = attack.get("attack");
            List<Card> tempHand = new ArrayList<>(players[getPlayerId(player)-1].getDeck().asList());

            String indicesInput = convertCardListToInput(tempHand, cards);  // Convert to string with newline separator
            input.append(indicesInput).append(QUIT_INPUT);
        }

        return input.toString();
    }

    /**
     * Generates input for players participating in a quest stage.
     *
     * @param participants A list of players participating in the quest stage.
     * @param discardTable A list of maps representing the cards a player should discard.
     * @return A string representing the input for the game.
     */
    private String getParticipationInput(List<String> participants, List<Map<String, String>> discardTable) {
        StringBuilder input = new StringBuilder();
        Map<String, String> playerTrimIndices = getDiscardInputMap(discardTable, List.of(game.players));

        for (Player player: questDetails.players) {
            if (participants.contains(player.toString()))
                input.append(NO_INPUT).append(playerTrimIndices.get(player.toString()));
            else
                input.append(YES_INPUT);
        }
        return input.toString();
    }

    /**
     * Generates input for the specified player to sponsor the quest.
     *
     * @param player The player sponsoring the quest (1-indexed).
     * @return A string representing the input for the game.
     */
    private String getSponsorInput(int player) {
        int noCount = (player - 1 - game.currentPlayer + NUM_PLAYERS) % NUM_PLAYERS;
        return NO_INPUT.repeat(noCount) + YES_INPUT;
    }

    /**
     * Generates a map of players to their discard input based on the discard table.
     *
     * @param discardTable A list of maps representing the discard table.
     * @param players      A list of players in the game.
     * @return A map where the key is the player's name and the value is the discard input.
     */
    private static Map<String, String> getDiscardInputMap(List<Map<String, String>> discardTable, List<Player> players) {
        Map<String, String> playerTrimIndices = new HashMap<>();
        discardTable
                .forEach(row -> playerTrimIndices.put(row.get("player"), parseDiscardList(players, row)));  // Otherwise, discard the specified card
        return playerTrimIndices;
    }

    /**
     * Parses the discard list for a player based on the row in the discard table.
     *
     * @param players A list of players in the game.
     * @param row     A map representing the row in the discard table.
     * @return A string representing the indices of the cards to discard.
     */
    private static String parseDiscardList(List<Player> players, Map<String, String> row) {
        String discard = row.get("discard");
        // If table specifies "first", discard the first n cards; format: "first[n]"
        if (discard.contains("first")) {
            int numCards = Integer.parseInt(discard.substring(discard.indexOf("[") + 1, discard.indexOf("]")));
            return "0\n".repeat(numCards);
        }

        StringBuilder inputBuilder = new StringBuilder();
        int playerId = getPlayerId(row.get("player"))-1;
        List<Card> tempHand = new ArrayList<>(players.get(playerId).getDeck().asList());
        findCardIndicesInHand(tempHand, row.get("discard")).forEach(integer -> inputBuilder.append(integer).append("\n"));
        return inputBuilder.toString();
    }

    /**
     * Helper function to pick cards from the adventure deck based on a string representation.
     *
     * @param adventureDeck The adventure deck to pick cards from.
     * @param hand          A string representing the cards to pick (e.g., "F5, F5, F10").
     * @return A list of cards picked from the deck.
     */
    private List<Card> pickCardsFromDeck(Deck adventureDeck, String hand) {
        List<Card> result = new ArrayList<>();
        for (String card : parseStringList(hand)) {
            char type = card.charAt(0);
            int value = Integer.parseInt(card.substring(1));
            Card c = new Card("Adv", type, value);
            int index = adventureDeck.asList().indexOf(c);
            if (index != -1) result.add(adventureDeck.asList().remove(index));
        }
        return result;
    }

    private static int getPlayerId(String player) {
        return Integer.parseInt(player.substring(1));
    }

    private void updateHistory() {
        for (int i = 0; i < 4; i++) {
            previousShields[i] = game.players[i].shields;
            previousCards[i] = game.players[i].getDeck().size();
        }
    }

    /**
     * Sets up a rigged game of Quest based on the jp_scenario from A1.
     */
    @Given("a rigged game of Quest starts based on the jp_scenario from A1")
    public void aRiggedJp_scenarioGameOfQuestStarts() {
        game = ATestHelper.rigGameSetupATest1(input, output);
        game.setupGame();
    }

    /**
     * Checks if the specified players earn or lose the correct number of shields.
     *
     * @param playersStr A string representing the players to check (e.g., "P1, P2").
     * @param action     The action performed ("earn" or "lose").
     * @param numShields The number of shields earned or lost.
     */
    @Then("Players {string} should {string} {int} shields")
    public void playersShouldActionShields(String playersStr, String action, int numShields) {
        int shieldChange = action.equals("earn") ? numShields : -numShields;
        List<String> players = parseStringList(playersStr);
        for (int i = 0; i < 4; i++) {
            if (players.contains("P" + i+1)) {
                assertEquals(previousShields[i] + shieldChange, game.players[i].shields);
            }
        }
        updateHistory();
    }

    /**
     * Simulates a player drawing a quest card with a specified number of stages.
     *
     * @param player The player drawing the quest card (1-indexed).
     * @param stage  The number of stages in the quest.
     */
    @When("P{int} draws a {int} stage quest")
    public void DrawsAStageQuest(int player, int stage) {
        game.questDeck.asList().addFirst(new Card("Quest", 'Q', stage));
        game.currentPlayer = player - 1;

        questDetails = new QuestDetails();
        questDetails.card = game.questDeck.draw(); // Draw the quest card
        questDetails.sponsorHand = game.players[player-1].getDeck().asList();
        updateHistory();
    }

    /**
     * Simulates a player sponsoring the current quest.
     *
     * @param player The player sponsoring the quest (1-indexed).
     */
    @Then("P{int} sponsors the quest")
    public void pSponsorsTheQuest(int player) {
        game.input = new Scanner(getSponsorInput(player));
        questDetails.sponsor = game.findSponsor(game.currentPlayer, questDetails.card);
        questDetails.players = new ArrayList<>(Arrays.asList(game.players));
        questDetails.players.remove(questDetails.sponsor);
    }

    /**
     * Simulates players building the stages of the current quest.
     *
     * @param player  The player building the stages (1-indexed).
     * @param stages  The number of stages in the quest.
     * @param table   A table representing the cards for each stage.
     */
    @And("P{int} builds the {int} stages")
    public void pBuildsTheStages(int player, int stages, DataTable table) {
        Player sponsor = game.players[player - 1];
        game.input = new Scanner(getStageBuildInput(sponsor, stages, table.asMaps()));
        questDetails.stages = game.setupQuest(sponsor, questDetails.card);
        updateHistory();
    }

    /**
     * Simulates players participating in a specific stage of the quest.
     *
     * @param players      A string representing the players participating (e.g., "P1, P2").
     * @param discardTable A table representing the cards each player should discard if necessary.
     */
    @And("Players {string} participate in stage")
    public void playersParticipateInStage(String players, DataTable discardTable) {
        List<String> participants = parseStringList(players);
        List<Map<String, String>> rows = discardTable.asMaps();

        game.input = new Scanner(getParticipationInput(participants, rows));
        game.withdrawPlayers(questDetails.players);

        assertEquals(participants.size(), questDetails.players.size());
    }

    /**
     * Simulates players building their attacks for a specific stage of the quest.
     *
     * @param stage       The stage of the quest.
     * @param attackTable A table representing the attacks for each player.
     */
    @And("Players build their stage {int} attack")
    public void playersBuildTheirAttack(int stage, DataTable attackTable) {
        List<Map<String, String>> attackMap = attackTable.asMaps();
        game.input = new Scanner(getAttackBuildInput(game.players, attackMap));
    }

    /**
     * Simulates players attacking the stage of the quest.
     *
     * @param stage The stage of the quest.
     */
    @And("Players attack the stage {int}")
    public void playersAttackTheStage(int stage) {
        Map<Player, List<Card>> attacks = game.setupAttacks(questDetails.players);
        int stageValue = Game.getStageValue(questDetails.stages.get(stage-1));
        game.resolveAttacks(questDetails.players, attacks, stageValue);
    }

    /**
     * Checks if the specified players pass a specific stage of the quest.
     *
     * @param playersStr A string representing the players who should pass (e.g., "P1, P2").
     * @param stage      The stage of the quest.
     */
    @And("Players {string} should pass stage {int}")
    public void playersShouldPassStages(String playersStr, int stage) {
        List<String> players = parseStringList(playersStr);
        assertEquals(players.size(), questDetails.players.size());
        for (Player passers: questDetails.players) {
            assertTrue(players.contains("P" + passers.id));
        }
    }

    /**
     * Simulates the game cleanup after a quest.
     *
     * @param player        The sponsor of the quest (1-indexed).
     * @param discardTable  A table representing the cards the player should discard.
     */
    @And("P{int}'s quest is cleaned up")
    public void pQuestIsCleanedUp(int player, DataTable discardTable) {
        Map<String, String> discardMap = getDiscardInputMap(discardTable.asMaps(), List.of(game.players));
        game.input = new Scanner(discardMap.get("P" + player));
        game.cleanupQuest(questDetails.sponsor, questDetails.stages);
    }

    /**
     * Checks if the sponsor of a quest discards the correct number of cards and draws the correct number of cards.
     *
     * @param sponsor     The sponsor of the quest (1-indexed).
     * @param numCards    The number of cards the sponsor should draw/discard.
     */
    @And("P{int} should discard and draw {int} cards")
    public void pShouldDiscardAndDrawCards(int sponsor, int numCards) {
        int expectedHandSize = previousCards[sponsor-1]  + numCards;
        int trim = Math.max(0, expectedHandSize - MAX_HAND_SIZE);
        List<Card> sponsorNewHand = questDetails.sponsor.getDeck().asList();

        assertEquals(expectedHandSize-trim, sponsorNewHand.size()); // Could only be true if player drew cards
        assertTrue(game.adventureDeck.discardSize() > numCards);
    }

    /**
     * Checks if the player has the correct cards in their hand in order.
     *
     * @param player The player to check (e.g., "P1").
     * @param cards  A string representing the cards in the player's hand (e.g., "[F5, F10]").
     */
    @And("Player {string} should match cards {string}")
    public void playerPShouldMatchCards(String player, String cards) {
        List<Card> expectedHand = parseStringList(cards).stream()
                .map(s -> new Card("Adv", s.charAt(0), Integer.parseInt(s.substring(1))))
                .toList();
        List<Card> actualHand = game.players[getPlayerId(player)-1].getDeck().asList();

        assertEquals(expectedHand, actualHand);
    }

    /**
     * Checks if the player has the correct number of cards in their hand.
     *
     * @param player   The player to check (e.g., "P1").
     * @param numCards The number of cards in the player's hand.
     */
    @And("Player P{int} should have {int} cards")
    public void playerPShouldHaveNumCards(int player, int numCards) {
        assertEquals(numCards, game.players[player-1].getDeck().size());
    }

    /**
     * Sets up a rigged game of Quest with 4 players where P1 and P2 are set up to win.
     * This includes initializing the adventure and quest decks, and setting up the players' hands.
     */
    @Given("a rigged 2winner game of Quest starts")
    public void aRigged2WinnerGameOfQuestStarts() {
        game = new Game(NUM_PLAYERS, input, output);

        // Set Adventure deck
        game.adventureDeck.initAdventureDeck();

        // Set players' hands
        game.players[0].pickCards(pickCardsFromDeck(game.adventureDeck, "[F5, F5, F10, F10, F15, F15, F20, D5, D5, H10, H10, B15]"));
        game.players[1].pickCards(pickCardsFromDeck(game.adventureDeck, "[F5, S10, S10, S10, S10, H10, H10, B15, B15, L20, L20, E30]"));
        game.players[2].pickCards(pickCardsFromDeck(game.adventureDeck, "[F5, F10, F15, F40, D5, D5, S10, H10, H10, B15, L20, L20]"));
        game.players[3].pickCards(pickCardsFromDeck(game.adventureDeck, "[F5, S10, S10, S10, S10, H10, H10, B15, B15, L20, L20, E30]"));

        updateHistory();
    }

    /**
     * Inner class to store details about the current quest.
     */
    public static class QuestDetails {
        Player sponsor;
        Card card;
        List<List<Card>> stages;
        List<Player> players;
        List<Card> sponsorHand;
    }
}
