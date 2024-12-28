package org.quest;

import static org.quest.TestGameStates.pickCardsFromDeck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private final int PLAYERS_AMOUNT = 4;
    private final Scanner input = new Scanner(""); // Simulate user input for sponsor questions
    private final StringWriter output = new StringWriter();

    Game game;
    GameState gs;

    @BeforeEach
    public void setUp() {
        gs = new GameState();
        game = new Game(input, new PrintWriter(output));
        game.gameState = gs;
    }

    private void startQuest(int questSize) {
        startQuest(questSize, 4);
    }

    private void startQuest(int questSize, int sponsor) {
        gs.currentCard = new Card("Quest", 'Q', questSize);
        GameLogic.startQuest(gs);
        GameLogic.sponsorQuest(gs, sponsor);
    }

    private static Map<Integer, List<Card>> getAttack1(List<Player> participants) {
        Map<Integer, List<Card>> attacks = new HashMap<>();
        attacks.put(participants.get(0).id, List.of(new Card("Adv", 'E', 30))); // attack > stage
        attacks.put(participants.get(1).id, List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(2).id, List.of(new Card("Adv", 'H', 10)));
        return attacks;
    }

    private static Map<Integer, List<Card>> getAttack2(List<Player> participants) {
        Map<Integer, List<Card>> attacks = new HashMap<>();
        attacks.put(participants.get(0).id, List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(1).id, List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(2).id, List.of(new Card("Adv", 'H', 10)));
        return attacks;
    }

    private static List<List<Card>> getQuestSetup(int stages) {
        List<List<Card>> questSetup = new ArrayList<>();
        for (int i = 0; i < stages; i++) {
            List<Card> stage = List.of(new Card("Adv", 'H', 10));
            questSetup.add(stage);
        }
        return questSetup;
    }

    private static void initSponsorHand(Player sponsor) {
        sponsor.getDeck()
                .add(List.of(new Card("Adv", 'F', 5),
                             new Card("Adv", 'F', 5),
                             new Card("Adv", 'S', 10),
                             new Card("Adv", 'H', 10),
                             new Card("Adv", 'E', 30)));
    }

    @Test
    @DisplayName("Check Adventure Deck Size after Initialization")
    void RESP_01_test_01() {
        // Arrange
        Deck deck = new Deck();

        // Act
        deck.initAdventureDeck();

        // Assert
        assertEquals(100, deck.size());
    }

    @Test
    @DisplayName("Check Quest Deck Size after Initialization")
    void RESP_01_test_02() {
        // Arrange
        Deck deck = new Deck();

        // Act
        deck.initQuestDeck();

        // Assert
        assertEquals(17, deck.size());
    }

    @Test
    @DisplayName("Check Adventure Deck has correct adventure cards")
    void RESP_01_test_03() {
        // Arrange
        Deck deck = new Deck();
        int totalFoes = 0;
        int totalWeapons = 0;

        // Act
        deck.initAdventureDeck();

        // Assert
        for (int i = 0; i < Deck.FOES_AMOUNT.length; ++i) {
            for (int j = 0; j < Deck.FOES_AMOUNT[i]; ++j) {
                Card card = deck.draw();
                assertEquals("F" + Deck.FOES_VALUES[i], card.toString());
                ++totalFoes;
            }
        }

        for (int i = 0; i < Deck.WEAPONS.length; ++i) {
            for (int j = 0; j < Deck.WEAPONS_AMOUNTS[i]; ++j) {
                Card card = deck.draw();
                assertEquals(String.valueOf(Deck.WEAPONS[i]) + Deck.WEAPONS_VALUES[i], card.toString());
                ++totalWeapons;
            }
        }
        assertEquals(50, totalFoes);
        assertEquals(50, totalWeapons);
    }

    @Test
    @DisplayName("Check Quest Deck has correct quest cards")
    void RESP_01_test_04() {
        // Arrange
        Deck deck = new Deck();
        int totalQuests = 0;
        int totalEvents = 0;

        // Act
        deck.initQuestDeck();

        // Assert
        for (int i = 0; i < Deck.QUEST_AMOUNTS.length; ++i) {
            for (int j = 0; j < Deck.QUEST_AMOUNTS[i]; ++j) {
                Card card = deck.draw();
                assertEquals("Q" + Deck.QUEST_VALUES[i], card.toString());
                ++totalQuests;
            }
        }

        for (int i = 0; i < Deck.EVENTS_AMOUNTS.length; ++i) {
            for (int j = 0; j < Deck.EVENTS_AMOUNTS[i]; ++j) {
                Card card = deck.draw();
                assertEquals(Deck.EVENTS[i], card.toString());
                ++totalEvents;
            }
        }

        assertEquals(12, totalQuests);
        assertEquals(5, totalEvents);
    }

    @Test
    @DisplayName("Check Decks can be shuffled")
    public void RESP_01_test_05() {
        // Arrange
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck1.initAdventureDeck();
        deck2.initAdventureDeck();

        // Act
        boolean same = true;
        deck2.shuffle();
        for (int i = 0; i < 100; ++i) {
            if (!deck1.draw().toString().equals(deck2.draw().toString())) {
                same = false;
                break;
            }
        }

        // Assert
        assertFalse(same);
    }

    @Test
    @DisplayName("Check Player deck contains 12 adventure cards")
    public void RESP_02_test_01() {
        // Arrange
        Player player = new Player(1);
        Deck adventureDeck = new Deck();
        adventureDeck.initAdventureDeck();

        // Act
        player.pickCards(adventureDeck.draw(12));

        // Assert
        assertEquals(12, player.getDeck().size());
        for (int i = 0; i < 12; ++i) {
            assertEquals("Adv", player.getDeck().draw().cardType);
        }
        assertEquals(88, adventureDeck.size());
    }

    @Test
    @DisplayName("Check all Players have different decks of 12 cards")
    public void RESP_02_test_02() {
        // Arrange & Act
        GameLogic.startGame(gs);

        // Assert
        for (int i = 0; i < PLAYERS_AMOUNT; ++i) {
            Deck playerDeck = gs.players[i].getDeck();
            for (int j = i + 1; j < PLAYERS_AMOUNT; ++j) {
                assertNotEquals(playerDeck, gs.players[j].getDeck());
            }
            assertEquals(12, playerDeck.size());
        }

        assertEquals(100 - PLAYERS_AMOUNT * 12, gs.adventureDeck.size());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - No shields given")
    public void RESP_03_test_01() {
        // Arrange
        GameLogic.startGame(gs);

        // Act
        GameLogic.updateWinners(gs);

        // Assert
        assertTrue(gs.winners.isEmpty());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 1 player has 7 shields")
    public void RESP_03_test_02() {
        // Arrange
        GameLogic.startGame(gs);
        gs.players[0].shields = 7;

        // Act
        GameLogic.updateWinners(gs);

        // Assert
        assertEquals(1, gs.winners.size());
        assertEquals(gs.players[0], gs.winners.getFirst());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 3 players have 7 shields")
    public void RESP_03_test_03() {
        // Arrange
        GameLogic.startGame(gs);
        gs.players[0].shields = 7;
        gs.players[1].shields = 15;
        gs.players[2].shields = 8;
        gs.players[3].shields = 6;

        // Act
        GameLogic.updateWinners(gs);

        // Assert
        assertEquals(3, gs.winners.size());
        assertEquals(gs.players[0], gs.winners.getFirst());
        assertEquals(gs.players[1], gs.winners.get(1));
        assertEquals(gs.players[2], gs.winners.getLast());
    }

    @Test
    @DisplayName("Game displays the ids of winners")
    public void RESP_04_test_01() {
        // Arrange
        GameLogic.startGame(gs);
        gs.players[0].shields = 7;
        gs.players[2].shields = 8;

        // Act
        GameLogic.updateWinners(gs);
        game.displayWinners(gs.winners);

        // Assert
        assertTrue(output.toString().strip().endsWith("Winners: P1, P3"));
    }

    @Test
    @DisplayName("Game returns a players hand with only foes in order")
    public void RESP_05_test_01() {
        // Arrange
        Player player = new Player(1);
        Deck adventureDeck = new Deck();
        adventureDeck.initAdventureDeck();
        adventureDeck.shuffle();
        List<Card> foes = new ArrayList<>();
        while (foes.size() < 12 && adventureDeck.size() != 0) {
            Card card = adventureDeck.draw();
            if (card.cardType.equals("Adv") && card.type == 'F') {
                foes.add(card);
                player.pickCards(List.of(card));
            }
        }
        foes.sort(Comparator.comparingInt(card -> card.value));

        // Act
        List<Card> hand = player.getDeck().asList();

        // Assert
        for (int i = 0; i < 12; ++i) {
            assertEquals(foes.get(i), hand.get(i));
        }
    }

    @Test
    @DisplayName("Game returns a players hand with only weapons in order")
    public void RESP_05_test_02() {
        // Arrange
        Player player = new Player(1);
        Deck adventureDeck = new Deck();
        adventureDeck.initAdventureDeck();
        adventureDeck.shuffle();
        List<Card> weapons = new ArrayList<>();
        while (weapons.size() < 12 && adventureDeck.size() != 0) {
            Card card = adventureDeck.draw();
            if (card.cardType.equals("Adv") && card.type != 'F' && card.type != 'H') {
                weapons.add(card);
                player.pickCards(List.of(card));
            }
        }
        weapons.sort(Comparator.comparingInt(card -> card.value));

        // Act
        List<Card> hand = player.getDeck().asList();

        // Assert
        for (int i = 0; i < 12; ++i) {
            assertEquals(weapons.get(i), hand.get(i));
        }
    }

    @Test
    @DisplayName("Game returns a players hand with mixed adventure cards in order")
    public void RESP_05_test_03() {
        // Arrange
        Player player = new Player(1);
        List<Card> cards = new ArrayList<>(Arrays.asList(new Card("Adv", 'F', 5),
                                                         new Card("Adv", 'F', 10),
                                                         new Card("Adv", 'F', 15),
                                                         new Card("Adv", 'S', 10),
                                                         new Card("Adv", 'H', 10),
                                                         new Card("Adv", 'B', 15)));
        List<Card> shuffled = new ArrayList<>(cards);
        Collections.shuffle(shuffled);
        player.pickCards(shuffled);

        // Act
        List<Card> hand = player.getDeck().asList();

        // Assert
        for (int i = 0; i < 6; ++i) {
            assertEquals(cards.get(i), hand.get(i));
        }
    }

    @Test
    @DisplayName("Game returns a players hand with swords adventure cards before horses")
    public void RESP_05_test_04() {
        // Arrange
        Player player = new Player(1);
        List<Card> cards = new ArrayList<>(Arrays.asList(new Card("Adv", 'S', 10),
                                                         new Card("Adv", 'S', 10),
                                                         new Card("Adv", 'H', 10),
                                                         new Card("Adv", 'H', 10),
                                                         new Card("Adv", 'H', 10),
                                                         new Card("Adv", 'H', 10)));
        List<Card> shuffled = new ArrayList<>(cards);
        Collections.shuffle(shuffled);
        player.pickCards(shuffled);

        // Act
        List<Card> hand = player.getDeck().asList();

        // Assert
        for (int i = 0; i < 6; ++i) {
            assertEquals(cards.get(i).type, hand.get(i).type);
            assertEquals(cards.get(i).value, hand.get(i).value);
        }
    }

    @Test
    @DisplayName("Game correctly indicates whose turn it is and their hand")
    public void RESP_06_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            public void playTurn(Player player) {
                print(player + "'s turn - Hand: " + player.getDeck());
                GameLogic.drawQuestCard(gameState);
                if (gameState.currentPlayer == 2) gameState.getPlayers()[0].shields = 7;
            }
        };

        // Act
        game.start();

        // Assert
        for (int i = 0; i < PLAYERS_AMOUNT - 1; ++i)
            assertTrue(output.toString()
                               .contains(String.format("P%d's turn - Hand: %s", i + 1, game.gameState.players[i].getDeck())));
        assertFalse(output.toString().contains("P4's turn"));
    }

    @Test
    @DisplayName("Game correctly loops through players")
    public void RESP_06_test_02() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            static int times = 0;

            public void playTurn(Player player) {
                print(player + "'s turn");
                GameLogic.drawQuestCard(gameState);
                if (gameState.currentPlayer == 0 && times++ == 1) gameState.players[0].shields = 7;
            }
        };

        // Act
        game.start();

        // Assert
        String outputStr = output.toString();
        for (int i = 0; i < PLAYERS_AMOUNT; ++i)
            assertTrue(outputStr.contains(String.format("P%d's turn", i + 1)));
        assertEquals(2, outputStr.split("P1's turn").length - 1);
        assertEquals(1, outputStr.split("P2's turn").length - 1);
    }

    @Test
    @DisplayName("Game should draw and display an Event card at the start of a player's turn")
    public void RESP_07_test_01() {
        // Arrange
        // Manipulate the quest deck to ensure the first card is an Event card
        game.input = new Scanner("\n"); // Simulate user pressing 'Enter' to clear hotseat
        gs.questDeck.add(List.of(new Card("Plague")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        String expectedOutput = "Player P1 drew Plague";
        assertTrue(output.toString().contains(expectedOutput));
    }

    @Test
    @DisplayName("Game carries out Plague Event card effect (more than 1 shield)")
    public void RESP_08_test_01() {
        // Arrange
        game.input = new Scanner("\n"); // Simulate user pressing 'Enter' to clear hotseat
        gs.players[0].shields = 6;
        gs.questDeck.add(List.of(new Card("Plague")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        assertEquals(4, gs.players[0].shields);
    }

    @Test
    @DisplayName("Game carries out Plague Event card effect (1 shield)")
    public void RESP_08_test_02() {
        // Arrange
        game.input = new Scanner("\n"); // Simulate user pressing 'Enter' to clear hotseat
        gs.players[0].shields = 1;
        gs.questDeck.add(List.of(new Card("Plague")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        assertEquals(0, gs.players[0].shields);
    }

    @Test
    @DisplayName("Game carries out Queen's Favor Event card effect")
    public void RESP_09_test_01() {
        // Arrange
        game.input = new Scanner("\n"); // Simulate user pressing 'Enter' to clear hotseat
        List<Card> advDeck = List.of(new Card("Adv", 'F', 10), new Card("Adv", 'F', 15));
        List<Card> orgHand = List.of(new Card("Adv", 'F', 10), new Card("Adv", 'F', 15));
        gs.adventureDeck.add(advDeck);
        gs.players[0].pickCards(orgHand);

        // Manipulate the quest deck to ensure the first card is a Queen's Favor card
        gs.questDeck.add(List.of(new Card("Queen's favor")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        // Verify that the player's hand contains the original cards and the two new picked cards from the adventure deck
        assertTrue(gs.players[0].getDeck()
                           .asList()
                           .containsAll(Stream.concat(advDeck.stream(), orgHand.stream()).toList()));
    }

    @Test
    @DisplayName("Game carries out Prosperity Event card effect")
    public void RESP_10_test_01() {
        // Arrange
        game.input = new Scanner("\n\n\n\n"); // Simulate user pressing 'Enter' to clear hotseat
        gs.adventureDeck.initAdventureDeck();
        gs.adventureDeck.shuffle();

        // Manipulate the quest deck to ensure the first card is a Prosperity card
        gs.questDeck.add(List.of(new Card("Prosperity")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        // Ensure all players have 2 more cards in their hand
        for (int i = 0; i < PLAYERS_AMOUNT; ++i) {
            assertEquals(2, gs.players[i].getDeck().size());
        }
    }

    @Test
    @DisplayName("Game indicates when a turn has ended and clears the hotseat")
    public void RESP_11_test_01() {
        // Arrange
        game.input = new Scanner("\n"); // Simulate user pressing 'Enter'
        gs.questDeck.add(List.of(new Card("Plague")));

        // Act
        game.playTurn(gs.players[0]);

        // Assert
        assertTrue(output.toString().contains("P1's turn is over, press Enter to continue"));
        assertTrue(output.toString().split("\n").length > 20);
        assertFalse(game.input.hasNextLine());  // Verify that 'Enter' key press is required to continue
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with more than 12 cards")
    public void RESP_12_test_01() {
        // Arrange
        int excessCards = 3;
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);
        gs.players[0].pickCards(gs.adventureDeck.draw(excessCards));

        // Act
        int trim = GameLogic.computeTrim(gs.players[0]);

        // Assert
        assertEquals(excessCards, trim);
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with 12 cards")
    public void RESP_12_test_02() {
        // Arrange
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);

        // Act
        int trim = GameLogic.computeTrim(gs.players[0]);

        // Assert
        assertEquals(0, trim);
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with less than 12 cards")
    public void RESP_12_test_03() {
        // Arrange
        gs.adventureDeck.initAdventureDeck();
        gs.players[0].pickCards(gs.adventureDeck.draw(10));

        // Act
        int trim = GameLogic.computeTrim(gs.players[0]);

        // Assert
        assertEquals(0, trim);
    }

    @Test
    @DisplayName("Game prompts player for a card position - valid position")
    public void RESP_13_test_01() {
        // Arrange
        game.input = new Scanner("0\n");
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);
        Card firstCard = gs.players[0].getDeck().asList().getFirst();

        // Act
        Card position = game.selectCard(gs.players[0]);

        // Assert
        assertFalse(game.input.hasNextLine());
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game prompts player for a card position - valid position")
    public void RESP_13_test_02() {
        // Arrange
        game.input = new Scanner("4\n");
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);
        Card firstCard = gs.players[0].getDeck().asList().get(4);

        // Act
        Card position = game.selectCard(gs.players[0]);

        // Assert
        assertFalse(game.input.hasNextLine());
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game prompts player for a card position - invalid position")
    public void RESP_13_test_03() {
        // Arrange
        game.input = new Scanner("13\n0\n");  // 13 is invalid, 0 is valid
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);
        Card firstCard = gs.players[0].getDeck().asList().getFirst();

        // Act
        Card position = game.selectCard(gs.players[0]);

        // Assert
        assertFalse(game.input.hasNextLine()); // Verify that the player was prompted twice
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game deletes n cards from a player's hand")
    public void RESP_14_test_01() {
        // Arrange
        game.input = new Scanner("0\n1\n2\n");
        GameLogic.startGame(gs);
        int excessCards = 3;
        gs.players[0].pickCards(gs.adventureDeck.draw(excessCards));

        // Act
        game.trimHand(gs.players[0]);

        // Assert
        assertEquals(12, gs.players[0].getDeck().size());
    }

    @Test
    @DisplayName("Game displays hand after each trimming")
    public void RESP_14_test_02() {
        // Arrange
        game.input = new Scanner("0\n0\n0\n");
        GameLogic.startGame(gs);
        int excessCards = 3;
        gs.players[0].pickCards(gs.adventureDeck.draw(excessCards));
        List<Card> orgHand = new ArrayList<>(gs.players[0].getDeck().asList());

        // Act
        game.trimHand(gs.players[0]);

        // Assert
        System.out.println(output);
        for (int i = 1; i <= 3; ++i) {
            int size = orgHand.size();
            assertTrue(output.toString().contains("P1's trimmed hand: " + orgHand.subList(i, size)));
        }
    }

    @Test
    @DisplayName("Game should refill decks with discard pile when empty")
    public void RESP_15_test_01() {
        // Arrange
        Deck adventureDeck = new Deck();
        adventureDeck.initAdventureDeck();
        adventureDeck.shuffle();
        adventureDeck.discard(adventureDeck.draw(100));

        // Act
        adventureDeck.draw();

        // Assert
        assertEquals(99, adventureDeck.size());
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest")
    public void RESP_16_test_01() {
        // Arrange
        game.input = new Scanner("y\n");  // Player 1 sponsors the quest
        gs.currentCard = new Card("Quest", 'Q', 2);
        gs.currentPlayer = 0;
        GameLogic.startQuest(gs);

        // Act
        int sponsor = game.findSponsor(gs.currentPlayer, gs.currentCard);
        Response response = GameLogic.sponsorQuest(gs, sponsor);

        // Assert
        assertTrue(output.toString().contains("P1: Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
        assertEquals(Response.SUCCESS, response);
        assertEquals(gs.players[0], gs.questState.sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - player declines")
    public void RESP_16_test_02() {
        // Arrange
        game.input = new Scanner("n\ny\n");  // Player 2 sponsors the quest
        gs.currentCard = new Card("Quest", 'Q', 5);
        gs.currentPlayer = 0;
        GameLogic.startQuest(gs);

        // Act
        int sponsor = game.findSponsor(gs.currentPlayer, gs.currentCard);
        Response response = GameLogic.sponsorQuest(gs, sponsor);

        // Assert
        assertTrue(output.toString().contains("P2: Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
        assertEquals(Response.SUCCESS, response);
        assertEquals(gs.players[1], gs.questState.sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - asks all players")
    public void RESP_16_test_03() {
        // Arrange
        game.input = new Scanner("n\nn\nn\ny\n");  // Player 4 sponsors the quest
        gs.currentCard = new Card("Quest", 'Q', 3);
        gs.currentPlayer = 0;
        GameLogic.startQuest(gs);

        // Act
        int sponsor = game.findSponsor(gs.currentPlayer, gs.currentCard);
        Response response = GameLogic.sponsorQuest(gs, sponsor);

        // Assert
        for (int i = gs.currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString()
                               .contains("P" + (i + 1) + ": Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        }
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
        assertEquals(Response.SUCCESS, response);
        assertEquals(gs.players[3], gs.questState.sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - all decline")
    public void RESP_16_test_04() {
        // Arrange
        game.input = new Scanner("n\nn\nn\nn\n");  // No player sponsors the quest
        gs.currentCard = new Card("Quest", 'Q', 4);
        gs.currentPlayer = 0;
        GameLogic.startQuest(gs);

        // Act
        int sponsor = game.findSponsor(gs.currentPlayer, gs.currentCard);

        // Assert
        for (int i = gs.currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString()
                               .contains("P" + (i + 1) + ": Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        }
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
        assertEquals(-1, sponsor);
        assertNull(gs.questState.sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - starts from non 0 position")
    public void RESP_16_test_05() {
        // Arrange
        game.input = new Scanner("n\nn\ny\n");  // Player 4 sponsors the quest
        gs.currentCard = new Card("Quest", 'Q', 4);
        gs.currentPlayer = 1;
        GameLogic.startQuest(gs);

        // Act
        int sponsor = game.findSponsor(gs.currentPlayer, gs.currentCard);
        Response response = GameLogic.sponsorQuest(gs, sponsor);

        // Assert
        for (int i = gs.currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString()
                               .contains("P" + (i + 1) + ": Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        }
        assertFalse(output.toString().contains("P1: Do you want to sponsor the quest " + gs.currentCard + "? (y/n)"));
        assertFalse(game.input.hasNextLine());  // Verify that the players was prompted
        assertEquals(Response.SUCCESS, response);
        assertEquals(gs.players[3], gs.questState.sponsor);
    }

    @Test
    @DisplayName("Game displays hand during stage setup")
    public void RESP_17_test_01() {
        // Arrange
        game.input = new Scanner("0\nQuit\n");
        GameLogic.startGame(gs);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        assertTrue(output.toString().contains(gs.players[0].getDeck().toString()));
    }

    @Test
    @DisplayName("Game prompts sponsor to select cards for stage")
    public void RESP_17_test_02() {
        // Arrange
        game.input = new Scanner("0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        GameLogic.initPlayers(gs);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        assertTrue(output.toString()
                           .contains("Select a card for stage " + (gs.questState.currentStage-1) + " or enter 'Quit' to finish stage setup"));
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - valid")
    public void RESP_18_test_01() {
        // Arrange
        game.input = new Scanner("0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, H10]");
        gs.players[0].getDeck().add(hand);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getFirst();
        assertEquals(2, stage.size());
        assertEquals(hand.getFirst(), stage.getFirst());
        assertEquals(hand.get(2), stage.getLast());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid number")
    public void RESP_18_test_02() {
        // Arrange
        game.input = new Scanner("-1\n0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, H10]");
        gs.players[0].getDeck().add(hand);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getFirst();
        assertTrue(output.toString().contains("Invalid card index"));
        assertEquals(2, stage.size());
        assertEquals(hand.getFirst(), stage.getFirst());
        assertEquals(hand.get(2), stage.getLast());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid foe")
    public void RESP_18_test_03() {
        // Arrange
        game.input = new Scanner("1\n0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, F15, H10]");
        gs.players[0].getDeck().add(hand);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getFirst();
        assertTrue(output.toString().contains("Invalid card, only one foe card is allowed (Sole foe)"));
        assertEquals(1, stage.size());
        assertEquals(hand.get(1), stage.getFirst());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid repeated weapon")
    public void RESP_18_test_04() {
        // Arrange
        game.input = new Scanner("0\n0\n0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F5, S10, S10]");
        gs.players[0].getDeck().add(hand);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getFirst();
        assertTrue(output.toString()
                           .contains("Invalid card, Weapon cards must be different (non-repeated weapon card)"));
        assertEquals(2, stage.size());
        assertEquals(hand.getFirst(), stage.getFirst());
        assertEquals(hand.get(2).value, stage.get(1).value);
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - stage empty")
    public void RESP_19_test_01() {
        // Arrange
        game.input = new Scanner("Quit\n0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, S10]");
        gs.players[0].getDeck().add(hand);
        startQuest(4, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        assertTrue(output.toString().contains("A stage cannot be empty"));
        List<Card> stage = gs.questState.stages.getFirst();
        assertEquals(1, stage.size());
        assertEquals(hand.getFirst(), stage.getFirst());
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - insufficient value")
    public void RESP_19_test_02() {
        // Arrange
        game.input = new Scanner("0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2, 1);
        gs.questState.stages.add(pickCardsFromDeck(gs.adventureDeck, "[F10, S10, H10]")); // Value = 30
        gs.questState.currentStage = 2;

        // Act
        assertThrows(NoSuchElementException.class, () -> game.setupStage(gs.players[0], gs.questState.currentStage));

        // Assert
        assertTrue(output.toString().contains("Insufficient value for this stage"));
    }
//
    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - insufficient value (equal)")
    public void RESP_19_test_03() {
        // Arrange
        game.input = new Scanner("0\n0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2, 1);
        gs.questState.stages.add(pickCardsFromDeck(gs.adventureDeck, "[S10, H10]")); // Value = 20
        gs.questState.currentStage = 2;

        // Act
        assertThrows(NoSuchElementException.class, () -> game.setupStage(gs.players[0], gs.questState.currentStage));

        // Assert
        assertTrue(output.toString().contains("Insufficient value for this stage"));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - valid (sufficient value)")
    public void RESP_19_test_04() {
        // Arrange
        game.input = new Scanner("0\n2\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2, 1);
        gs.questState.stages.add(pickCardsFromDeck(gs.adventureDeck, "[F10, S10, H10]")); // Value = 30
        gs.questState.currentStage = 2;

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getLast();
        assertEquals(2, gs.questState.stages.size());
        assertEquals(2, stage.size());
        assertEquals(hand.get(0), stage.get(0));
        assertEquals(hand.get(3), stage.get(1));
        assertTrue(output.toString().contains("Stage " + (gs.questState.currentStage-1) + ": " + stage));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - valid (first stage)")
    public void RESP_19_test_05() {
        // Arrange
        game.input = new Scanner("0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2, 1);

        // Act
        game.setupStage(gs.players[0], gs.questState.currentStage);

        // Assert
        List<Card> stage = gs.questState.stages.getFirst();
        assertFalse(output.toString().contains("Insufficient value for this stage"));
        assertFalse(output.toString().contains("A stage cannot be empty"));
        assertTrue(output.toString().contains("Stage " + (gs.questState.currentStage-1) + ": " + stage));
    }

    @Test
    @DisplayName("Game determines and displays eligible participants for a stage")
    public void RESP_20_test_01() {
        // Arrange
        game.input = new Scanner("");
        GameLogic.startGame(gs);

        // Act
        startQuest(2, 1);

        // Assert
        List<Player> expectedParticipants = List.of(gs.players[1], gs.players[2], gs.players[3]);
        assertEquals(expectedParticipants, gs.questState.participants);
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage")
    public void RESP_21_test_01() {
        // Arrange
        game.input = new Scanner("n\ny\nn\n");
        gs.adventureDeck.initAdventureDeck();
        startQuest(2, 1);
        List<Player> participants = new ArrayList<>(gs.questState.participants);

        // Act
        assertThrows(NoSuchElementException.class, () -> game.playStage());

        // Assert
        for (Player p : participants)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        assertEquals(List.of(gs.players[1], gs.players[3]), gs.questState.participants);
        assertFalse(game.input.hasNextLine());  // Verify that the players were prompted
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage - all withdraw")
    public void RESP_21_test_02() {
        // Arrange
        game.input = new Scanner("y\ny\ny\n");
        gs.adventureDeck.initAdventureDeck();
        startQuest(2, 3);
        List<Player> participants = new ArrayList<>(gs.questState.participants);

        // Act
        boolean result = game.playStage();

        // Assert
        assertFalse(result);
        for (Player p : participants)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        assertTrue(gs.questState.participants.isEmpty());
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage - all participate")
    public void RESP_21_test_03() {
        // Arrange
        game.input = new Scanner("n\nn\nn\n");
        gs.adventureDeck.initAdventureDeck();
        startQuest(2);
        List<Player> participants = new ArrayList<>(gs.questState.participants);

        // Act
        assertThrows(NoSuchElementException.class, () -> game.playStage());

        // Assert
        for (Player p : participants)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        assertEquals(participants, gs.questState.participants);
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game draws 1 adventure card for each participant in stage")
    public void RESP_22_test_01() {
        // Arrange
        game.input = new Scanner("n\nn\nn\n");
        gs.adventureDeck.initAdventureDeck();
        startQuest(2);
        int initDeckSize = gs.adventureDeck.size();
        List<Player> participants = new ArrayList<>(gs.questState.participants);

        // Act
        assertThrows(NoSuchElementException.class, () -> game.playStage());

        // Assert
        assertEquals(initDeckSize - participants.size(), gs.adventureDeck.size());
        for (Player p : participants)
            assertTrue(output.toString().contains(p + " draws 1 Adventure card"));
    }

    @Test
    @DisplayName("Game draws 1 adventure card for each participant in stage - trim required")
    public void RESP_22_test_02() {
        // Arrange
        game.input = new Scanner("n\n0\ny\ny\n");  // only P1 tackles the stage and trims first card
        GameLogic.startGame(gs);
        startQuest(2, 1);
        int initDeckSize = gs.adventureDeck.size();

        // Act
        assertThrows(NoSuchElementException.class, () -> game.playStage());

        // Assert
        assertEquals(initDeckSize - 1, gs.adventureDeck.size());
        assertEquals(12, gs.players[0].getDeck().size());
        assertTrue(output.toString().contains("P2's trimmed hand: "));
    }

    @Test
    @DisplayName("Game ends quest if there is no sponsor")
    public void RESP_23_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            int findSponsor(int currentPlayer, Card card) { return -1; } // Mock no sponsor found
        };
        game.gameState = gs;
        gs.currentCard = new Card("Quest", 'Q', 2);

        // Act
        game.handleQuestCard(gs.players[0]);

        // Assert
        assertTrue(output.toString().contains("No sponsor found for the Quest"));
        assertNull(gs.questState);
    }

    @Test
    @DisplayName("Game does not end quest if there is a sponsor")
    public void RESP_23_test_02() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            int findSponsor(int currentPlayer, Card card) { return 1; }  // Mock sponsor found
        };
        game.gameState = gs;
        GameLogic.startGame(gs);
        gs.currentCard = new Card("Quest", 'Q', 2);

        // Act
        assertThrows(NoSuchElementException.class, () -> game.handleQuestCard(gs.players[0]));

        // Assert
        assertFalse(output.toString().contains("No sponsor found for quest"));
        assertNotNull(gs.questState);
    }

    @Test
    @DisplayName("Game displays hand during attack setup")
    public void RESP_24_test_01() {
        // Arrange
        game.input = new Scanner("0\nQuit\n");
        GameLogic.startGame(gs);
        Player p1 = gs.players[0];
        startQuest(2);

        // Act
        game.setupAttack(p1);

        // Assert
        assertTrue(output.toString().contains(p1 + "'s Deck: " + p1.getDeck()));
    }

    @Test
    @DisplayName("Game prompts participant to select a card for attack")
    public void RESP_24_test_02() {
        // Arrange
        game.input = new Scanner("0\nQuit\n");
        GameLogic.startGame(gs);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        assertTrue(output.toString().contains("Select a card for the attack or enter 'Quit' to finish attack setup"));
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game adds a selected valid card to attack or re-prompts if invalid - valid")
    public void RESP_25_test_01() {
        // Arrange
        game.input = new Scanner("0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        assertEquals(2, attack.size());
        assertEquals(hand.getFirst(), attack.getFirst());
        assertEquals(hand.getLast(), attack.getLast());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid number")
    public void RESP_25_test_02() {
        // Arrange
        game.input = new Scanner("-1\n0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[S10, S10, E30]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        assertTrue(output.toString().contains("Invalid card index"));
        assertEquals(2, attack.size());
        assertEquals(hand.get(0), attack.get(0));
        assertEquals(hand.get(2), attack.get(1));
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - A Foe")
    public void RESP_25_test_03() {
        // Arrange
        game.input = new Scanner("0\n2\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[F10, F15, H10]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        assertTrue(output.toString().contains("Invalid card, Foe cards are not allowed in attack"));
        assertEquals(1, attack.size());
        assertEquals(hand.get(2), attack.getFirst());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid repeated weapon")
    public void RESP_25_test_04() {
        // Arrange
        game.input = new Scanner("0\n0\n0\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[D5, S10, S10]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        assertTrue(output.toString()
                           .contains("Invalid card, Weapon cards must be different (non-repeated weapon card)"));
        assertEquals(2, attack.size());
        assertEquals(hand.getFirst(), attack.getFirst());
        assertEquals(hand.get(2).value, attack.get(1).value);
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during attack setup - attack empty")
    public void RESP_26_test_01() {
        // Arrange
        game.input = new Scanner("Quit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[D5, S10, S10]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        assertEquals(0, attack.size());
        assertTrue(output.toString().contains(gs.players[0] + "'s attack: []"));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during attack setup - attack non-empty")
    public void RESP_26_test_02() {
        // Arrange
        game.input = new Scanner("0\n1\nQuit\n");
        gs.adventureDeck.initAdventureDeck();
        List<Card> hand = pickCardsFromDeck(gs.adventureDeck, "[D5, S10, S10]");
        gs.players[0].getDeck().add(hand);
        startQuest(2);

        // Act
        game.setupAttack(gs.players[0]);

        // Assert
        List<Card> attack = gs.questState.attacks.get(1);
        List<Card> expected = List.of(hand.get(0), hand.get(2));
        assertEquals(2, attack.size());
        assertEquals(expected, attack);
        assertTrue(output.toString().contains(gs.players[0] + "'s attack: " + expected));
    }

    @Test
    @DisplayName("Game sets up the attack for each participant in stage")
    public void RESP_27_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupAttack(Player p) {  // Mock setup attack to return an easily verifiable attack
                print(p.id);
            }
        };
        game.gameState = gs;
        GameLogic.startGame(gs);
        startQuest(2);

        // Act
        game.setupAttacks();

        // Assert
        List<Player> participants = new ArrayList<>(gs.questState.participants);
        for (Player p : participants) {
            assertTrue(output.toString().contains("" + p.id));
        }
    }

    @Test
    @DisplayName("Game resolves an attack against a stage")
    public void RESP_28_test_01() {
        // Arrange
        List<Card> attack = List.of(new Card("Adv", 'S', 10), new Card("Adv", 'H', 10));

        // Act
        boolean successEquals = GameLogic.resolveAttack(attack, 20);
        boolean successGreater = GameLogic.resolveAttack(attack, 15);
        boolean failureLess = GameLogic.resolveAttack(attack, 25);

        // Assert
        assertTrue(successEquals);
        assertTrue(successGreater);
        assertFalse(failureLess);
    }

    @Test
    @DisplayName("Game resolves the attack for each participant - 1 survivor")
    public void RESP_29_test_01() {
        // Arrange
        List<Player> participants = new ArrayList<>(Arrays.asList(gs.players).subList(0, 3));
        Map<Integer, List<Card>> attacks = getAttack1(participants);
        int stageValue = 20;

        // Act
        boolean continueQuest = GameLogic.resolveAttacks(participants, attacks, stageValue);

        // Assert
        assertTrue(continueQuest);
        assertEquals(List.of(gs.players[0]), participants);
    }

    @Test
    @DisplayName("Game resolves the attack for each participants - no survivors")
    public void RESP_29_test_02() {
        // Arrange
        List<Player> participants = new ArrayList<>(Arrays.asList(gs.players).subList(0, 3));
        Map<Integer, List<Card>> attacks = getAttack2(participants);
        int stageValue = 20;

        // Act
        boolean continueQuest = GameLogic.resolveAttacks(participants, attacks, stageValue);

        // Assert
        assertFalse(continueQuest);
        assertTrue(participants.isEmpty());
    }

    @Test
    @DisplayName("Game resolves attacks for participants in stage - all survive")
    public void RESP_29_test_03() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupAttacks() {
                gs.questState.attacks = getAttack1(gs.questState.participants);
            }
        };
        game.gameState = gs;
        game.input = new Scanner("n\n0\nn\n0\nn\n0\n");
        GameLogic.startGame(gs);
        startQuest(2);
        gs.questState.stages.add(List.of(new Card("Adv", 'H', 10)));

        // Act
        boolean continueQuest = game.playStage();

        // Assert
        assertTrue(continueQuest);
        assertEquals(new ArrayList<>(Arrays.asList(gs.players)).subList(0, 3), gs.questState.participants);
    }

    @Test
    @DisplayName("Game resolves attacks for participants in stage - no survivors")
    public void RESP_29_test_04() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupAttacks() {
                gs.questState.attacks = getAttack2(gs.questState.participants);
            }
        };
        game.gameState = gs;
        game.input = new Scanner("n\n0\nn\n0\nn\n0\n");
        GameLogic.startGame(gs);
        startQuest(2);
        gs.questState.stages.add(List.of(new Card("Adv", 'F', 70)));


        // Act
        boolean continueQuest = game.playStage();

        // Assert
        assertFalse(continueQuest);
        assertTrue(gs.questState.questCompleted);
        assertTrue(gs.questState.participants.isEmpty());
    }
//
    @Test
    @DisplayName("Game ends a stage if all participants withdraw")
    public void RESP_30_test_01() {
        // Arrange
        game.input = new Scanner("y\ny\ny\n");
        GameLogic.startGame(gs);
        startQuest(2);

        // Act
        boolean continueQuest = game.playStage();

        // Assert
        assertFalse(continueQuest);
        assertTrue(gs.questState.questCompleted);
    }

    @Test
    @DisplayName("Game discards all cards from participants used in attacks")
    public void RESP_31_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupAttacks() {
                gs.questState.attacks = getAttack2(gs.questState.participants);
            }
        };
        game.gameState = gs;
        game.input = new Scanner("n\n0\nn\n0\nn\n0\n");
        GameLogic.startGame(gs);
        startQuest(2);
        gs.questState.stages.add(List.of(new Card("Adv", 'F', 70)));

        // Act
        game.playStage();

        // Assert
        // (3) Participants attacks all have 1 card each, plus the trim for each player
        assertEquals(3 + 3, gs.adventureDeck.discardSize());
    }

    @Test
    @DisplayName("Game sets up stages of a quest")
    public void RESP_32_test_01() {
        // Arrange
        String inputStr = "0\nQuit\n" +    // Stage 1
                          "1\nQuit\n" +    // Stage 2
                          "0\n0\nQuit\n" + // Stage 3
                          "0\nQuit\n";     // Stage 4
        game.input = new Scanner(inputStr);
        gs.adventureDeck.initAdventureDeck();
        initSponsorHand(gs.players[0]);
        startQuest(4, 1);

        // Act
        game.setupQuest(gs.questState.sponsor, gs.questState.questSize);

        // Assert
        List<List<Card>> stages = gs.questState.stages;
        assertEquals(gs.questState.questSize, stages.size());
        int previous = 0;
        for (List<Card> stage : stages) {
            int value = stage.stream().mapToInt(c -> c.value).sum();
            assertTrue(value > previous);
            previous = value;
        }
    }

    @Test
    @DisplayName("Game sets up the quest Stages")
    public void RESP_32_test_02() {
        // Arrange
        game.input = new Scanner("y\n" +   // Sponsor
                                    "0\nQuit\n" +    // Stage 1
                                    "1\nQuit\n" +    // Stage 2
                                    "0\n0\nQuit\n" + // Stage 3
                                    "0\nQuit\n");    // Stage 4
        gs.adventureDeck.initAdventureDeck();
        initSponsorHand(gs.players[0]); // Sponsor has 5 cards
        startQuest(4, 1);

        // Act
        assertThrows(NoSuchElementException.class, () -> game.handleQuestCard(gs.players[0]));

        // Assert
        assertEquals(0, gs.players[0].getDeck().size());
    }

    @Test
    @DisplayName("Game determines winners of a quest - winners")
    public void RESP_33_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            boolean playStage() {
                gs.questState.participants.removeIf(p -> p.id == 3);  // P3 ailed the stage
                return true;
            }
        };
        gs.adventureDeck.initAdventureDeck();
        startQuest(4, 1);

        // Act
        game.playQuest(gs.questState.questSize);

        // Assert
        List<Player> winners = gs.questState.participants;
        assertEquals(2, winners.size());
        List<Player> expected = List.of(gs.players[1], gs.players[3]);
        assertEquals(expected, winners);
    }

    @Test
    @DisplayName("Game determines winners of a quest - no winners")
    public void RESP_33_test_02() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            boolean playStage() {
                print("playStage()");
                gs.questState.participants.clear();
                return false;
            }
        };
        gs.adventureDeck.initAdventureDeck();
        startQuest(4);

        // Act
        game.playQuest(gs.questState.questSize);

        // Assert
        List<Player> winners = gs.questState.participants;
        assertTrue(winners.isEmpty());
        assertTrue(output.toString().contains("playStage()"));
        assertEquals(1, output.toString().split("playStage/(/)").length);
    }

    @Test
    @DisplayName("Game gives shields to winners of a quest")
    public void RESP_34_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupQuest(Player sponsor, int stage) {}
            void playQuest(int stages) {
                gs.questState.participants = List.of(gs.players[1], gs.players[3]);
                gs.questState.currentAttackStage = gs.questState.stages.size()+1;
                gs.questState.questCompleted = true;
            }
        };
        game.gameState = gs;
        game.input = new Scanner("y\n");
        gs.adventureDeck.initAdventureDeck();
        Player sponsor = gs.players[0];
        initSponsorHand(sponsor);
        gs.currentCard = new Card("Quest", 'Q', 3);

        // Act
        game.handleQuestCard(sponsor);

        // Assert
        assertEquals(3, gs.players[1].shields);
        assertEquals(3, gs.players[3].shields);
        assertEquals(0, gs.players[0].shields);
        assertEquals(0, gs.players[2].shields);
    }

    @Test
    @DisplayName("Game gives shields to winners of a quest - no winners")
    public void RESP_34_test_02() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupQuest(Player sponsor, int stage) {}
            void playQuest(int stages) {
                gs.questState.participants = List.of();
                gs.questState.questCompleted = true;
            }
        };
        game.gameState = gs;
        game.input = new Scanner("y\n");
        gs.adventureDeck.initAdventureDeck();
        Player sponsor = gs.players[0];
        initSponsorHand(sponsor);
        gs.currentCard = new Card("Quest", 'Q', 3);

        // Act
        game.handleQuestCard(sponsor);

        // Assert
        for (Player p : gs.players)
            assertEquals(0, p.shields);
    }

    @Test
    @DisplayName("Game discards all cards used by sponsor to build the quest")
    public void RESP_35_test_01() {
        // Arrange
        gs.adventureDeck.initAdventureDeck();
        startQuest(4);
        gs.questState.stages = getQuestSetup(4);  // get Quest Setup adds 1 card per stage
        gs.questState.questCompleted = true;

        // Act
        game.cleanupQuest();

        // Assert
        assertEquals(4, gs.adventureDeck.discardSize());
    }

    @Test
    @DisplayName("Game draws cards for sponsor after quest - no trim")
    public void RESP_36_test_01() {
        // Arrange
        gs.adventureDeck.initAdventureDeck();
        int stages = 4;
        Player sponsor = gs.players[0];
        startQuest(stages, 1);
        gs.questState.stages = getQuestSetup(4);  // Creates stages with 1 card each
        gs.questState.questCompleted = true;

        // Act
        game.cleanupQuest();

        // Assert
        assertEquals(stages + stages, sponsor.getDeck().size()); // 1 card per stage + total num of stages
    }

    @Test
    @DisplayName("Game draws cards for sponsor after quest - with trim")
    public void RESP_36_test_02() {
        // Arrange
        game.input = new Scanner("0\n0\n0\n0\n");  // Trim cards
        gs.adventureDeck.initAdventureDeck();
        Player sponsor = gs.players[0];
        sponsor.pickCards(List.of(new Card("Adv", 'S', 10)));
        List<List<Card>> questSetup = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            questSetup.add(List.of(new Card("Adv", 'S', 10), new Card("Adv", 'S', 10)));
        }
        startQuest(5, 1);
        gs.questState.stages = questSetup;
        gs.questState.questCompleted = true;

        // Act
        game.cleanupQuest();

        // Assert
        assertEquals(12, sponsor.getDeck().size());
        assertFalse(game.input.hasNextLine());  // Verify that the player was prompted to trim
    }

    @Test
    @DisplayName("Game cleans up after quest is complete")
    public void RESP_37_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void setupQuest(Player sponsor, int stage) {
                gs.questState.stages = getQuestSetup(2);
            }
            void playQuest(int stages) {
                gs.questState.participants = List.of(gs.players[1], gs.players[3]);
                gs.questState.currentAttackStage = gs.questState.stages.size()+1;
                gs.questState.questCompleted = true;
            }
        };
        game.gameState = gs;
        game.input = new Scanner("y\n");
        gs.adventureDeck.initAdventureDeck();
        Player sponsor = gs.players[0];
        initSponsorHand(sponsor);
        gs.currentCard = new Card("Quest", 'Q', 2);

        // Act
        game.handleQuestCard(sponsor);

        // Assert
        assertEquals(5 + 2 + 2,
                     sponsor.getDeck().size()); // 5 (Sponsor's hand) + 2 (1 card per stage) + 2 (num of stages)
        assertEquals(2, gs.adventureDeck.discardSize());  // 2 cards used to build the quest
    }

    @Test
    @DisplayName("Game carries out actions triggered by Quest Card")
    public void RESP_38_test_01() {
        // Arrange
        game = new Game(input, new PrintWriter(output)) {
            void handleQuestCard(Player player) {
                print("handleQuestCard(" + gs.currentCard + ")");
            }
        };
        game.input = new Scanner("\n");
        game.gameState = gs;
        Card questCard = new Card("Quest", 'Q', 4);
        gs.questDeck.add(List.of(questCard));

        // Act
        game.playTurn(gs.players[2]);

        // Assert
        assertTrue(output.toString().contains("handleQuestCard(" + questCard + ")"));
    }

    @Test
    @DisplayName("A-Test JP-Scenario")
    public void A_test_JP() {
        // Arrange
        String inputStr = "n\n" + // P1 declines Q4 quest
                          "y\n" + // P2 accepts Q4 quest
                          "0\n7\nQuit\n" + // P2 builds stage 1
                          "1\n4\nQuit\n" + // P2 builds stage 2
                          "1\n2\n3\nQuit\n" + // P2 builds stage 3
                          "1\n2\nQuit\n" + // P2 builds stage 4
                          "n\n0\n" + // P1 is asked to participate and trims F5
                          "n\n0\n" + // P3 is asked to participate and trims F5
                          "n\n0\n" + // P4 is asked to participate and trims F5
                          "4\n4\nQuit\n" + // P1 builds attack
                          "4\n3\nQuit\n" + // P3 builds attack
                          "3\n5\nQuit\n" + // P4 builds attack
                          "n\n" + // P1 is asked to participate
                          "n\n" + // P3 is asked to participate
                          "n\n" + // P4 is asked to participate
                          "6\n5\nQuit\n" + // P1 builds attack
                          "8\n3\nQuit\n" + // P3 builds attack
                          "5\n5\nQuit\n" + // P4 builds attack
                          "n\n" + // P3 is asked to participate
                          "n\n" + // P4 is asked to participate
                          "8\n5\n3\nQuit\n" + // P3 builds attack
                          "6\n4\n5\nQuit\n" + // P4 builds attack
                          "n\n" + // P3 is asked to participate
                          "n\n" + // P4 is asked to participate
                          "6\n5\n5\nQuit\n" + // P3 builds attack
                          "3\n3\n3\n4\nQuit\n" + // P4 builds attack
                          "0\n0\n0\n0\n"; // P2 trims hand
        game = new Game(input, new PrintWriter(output)) {
            void startGameState() { GameLogic.setupGameState(gs, "a1_scenario"); }
        };
        game.gameState = gs;
        game.input = new Scanner(inputStr);

        // Act
        assertThrows(NoSuchElementException.class, game::start); // Game would normally continue as no player has 7 shields

        // Assert
        assertEquals(0, gs.players[0].shields);
        assertEquals("[F5, F10, F15, F15, F30, H10, B15, B15, L20]", gs.players[0].getDeck().toString());
        assertEquals(0, gs.players[2].shields);
        assertEquals("[F5, F5, F15, F30, S10]", gs.players[2].getDeck().toString());
        assertEquals(4, gs.players[3].shields);
        assertEquals("[F15, F15, F40, L20]", gs.players[3].getDeck().toString());
        assertEquals(12, gs.players[1].getDeck().size());
    }

}