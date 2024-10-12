package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.example.Main.PLAYERS_AMOUNT;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    Scanner input = new Scanner("n\nn\nn\nn\n"); // Simulate user input for sponsor questions
    PrintWriter output = new PrintWriter(System.out);

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
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.questDeck.initQuestDeck();
        game.adventureDeck.shuffle();
        game.questDeck.shuffle();

        // Act
        game.initPlayers();

        // Assert
        for (int i = 0; i < PLAYERS_AMOUNT; ++i) {
            Deck playerDeck = game.players[i].getDeck();
            for (int j = i + 1; j < PLAYERS_AMOUNT; ++j) {
                assertNotEquals(playerDeck, game.players[j].getDeck());
            }
            assertEquals(12, playerDeck.size());
        }

        assertEquals(100 - PLAYERS_AMOUNT * 12, game.adventureDeck.size());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - No shields given")
    public void RESP_03_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();

        // Act
        List<Player> winners = game.checkWinners();

        // Assert
        assertTrue(winners.isEmpty());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 1 player has 7 shields")
    public void RESP_03_test_02() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        game.players[0].shields = 7;

        // Act
        List<Player> winners = game.checkWinners();

        // Assert
        assertEquals(1, winners.size());
        assertEquals(game.players[0], winners.getFirst());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 3 players have 7 shields")
    public void RESP_03_test_03() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        game.players[0].shields = 7;
        game.players[1].shields = 15;
        game.players[2].shields = 8;
        game.players[3].shields = 6;

        // Act
        List<Player> winners = game.checkWinners();

        // Assert
        assertEquals(3, winners.size());
        assertEquals(game.players[0], winners.getFirst());
        assertEquals(game.players[1], winners.get(1));
        assertEquals(game.players[2], winners.getLast());
    }

    @Test
    @DisplayName("Game displays the ids of winners")
    public void RESP_04_test_01() {
        // Arrange
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.players[0].shields = 7;
        game.players[2].shields = 8;

        // Act
        game.displayWinners(game.checkWinners());

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
        List<Card> cards = new ArrayList<>(Arrays.asList(
                new Card("Adv", 'F', 5),
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
        List<Card> cards = new ArrayList<>(Arrays.asList(
                new Card("Adv", 'S', 10),
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
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            public void playTurn(Player player) {
                print(player + "'s turn - Hand: " + player.getDeck());
                if (currentPlayer == 2) players[0].shields = 7;
            }
        };

        // Act
        game.start();

        // Assert
        for (int i = 0; i < PLAYERS_AMOUNT - 1; ++i)
            assertTrue(output.toString()
                               .contains(String.format("P%d's turn - Hand: %s", i + 1, game.players[i].getDeck())));
        assertFalse(output.toString().contains("P4's turn"));
    }

    @Test
    @DisplayName("Game correctly loops through players")
    public void RESP_06_test_02() {
        // Arrange
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            static int times = 0;

            public void playTurn(Player player) {
                print(player + "'s turn");
                if (currentPlayer == 0 && times++ == 1) players[0].shields = 7;
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
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));

        // Manipulate the quest deck to ensure the first card is an Event card
        Card eventCard = new Card("Plague"); // Example Event card
        game.questDeck.add(List.of(eventCard));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        String expectedOutput = "Player P1 drew Plague";
        assertTrue(output.toString().contains(expectedOutput));
    }

    @Test
    @DisplayName("Game carries out Plague Event card effect (more than 1 shield)")
    public void RESP_08_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.players[0].shields = 6;

        // Manipulate the quest deck to ensure the first card is a Plague card
        Card eventCard = new Card("Plague"); // Example Event card
        game.questDeck.add(List.of(eventCard));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        assertEquals(4, game.players[0].shields);
    }

    @Test
    @DisplayName("Game carries out Plague Event card effect (1 shield)")
    public void RESP_08_test_02() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.players[0].shields = 1;

        // Manipulate the quest deck to ensure the first card is a Plague card
        Card eventCard = new Card("Plague"); // Example Event card
        game.questDeck.add(List.of(eventCard));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        assertEquals(0, game.players[0].shields);
    }

    @Test
    @DisplayName("Game carries out Queen's Favor Event card effect")
    public void RESP_09_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        List<Card> advDeck = List.of(new Card("Adv", 'F', 10), new Card("Adv", 'F', 15));
        List<Card> orgHand = List.of(new Card("Adv", 'F', 10), new Card("Adv", 'F', 15));
        game.adventureDeck.add(advDeck);
        game.players[0].pickCards(orgHand);

        // Manipulate the quest deck to ensure the first card is a Queen's Favor card
        Card eventCard = new Card("Queen’s favor"); // Example Event card
        game.questDeck.add(List.of(eventCard));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        // Verify that the player's hand contains the original cards and the two new picked cards from the adventure deck
        assertTrue(game.players[0].getDeck()
                           .asList()
                           .containsAll(Stream.concat(advDeck.stream(), orgHand.stream()).toList()));
    }

    @Test
    @DisplayName("Game carries out Prosperity Event card effect")
    public void RESP_10_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.adventureDeck.shuffle();

        // Manipulate the quest deck to ensure the first card is a Prosperity card
        game.questDeck.add(List.of(new Card("Prosperity")));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        // Ensure all players have 2 more cards in their hand
        for (int i = 0; i < PLAYERS_AMOUNT; ++i) {
            assertEquals(2, game.players[i].getDeck().size());
        }
    }

    @Test
    @DisplayName("Game indicates when a turn has ended and clears the hotseat")
    public void RESP_11_test_01() {
        // Arrange
        StringWriter output = new StringWriter();
        Scanner input = new Scanner("\n"); // Simulate user input for sponsor questions
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)){
            public void clearHotseat() { // Override clearHotseat to print a message that can be verified
                print("Test Clear Hotseat");
            }
        };
        game.questDeck.add(List.of(new Card("Plague")));

        // Act
        game.playTurn(game.players[0]);

        // Assert
        assertTrue(output.toString().contains("P1's turn is over, press Enter to continue"));
        assertFalse(input.hasNextLine());  // Verify that 'Enter' key press is required to continue
        assertTrue(output.toString().contains("Test Clear Hotseat"));
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with more than 12 cards")
    public void RESP_12_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        int excessCards = 3;
        game.players[0].pickCards(game.adventureDeck.draw(excessCards));

        // Act
        int trim = game.computeTrim(game.players[0]);

        // Assert
        assertEquals(excessCards, trim);
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with 12 cards")
    public void RESP_12_test_02() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();

        // Act
        int trim = game.computeTrim(game.players[0]);

        // Assert
        assertEquals(0, trim);
    }

    @Test
    @DisplayName("Game computes number of cards to trim for a player with less than 12 cards")
    public void RESP_12_test_03() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.players[0].pickCards(game.adventureDeck.draw(10));

        // Act
        int trim = game.computeTrim(game.players[0]);

        // Assert
        assertEquals(0, trim);
    }

    @Test
    @DisplayName("Game prompts player for a card position - valid position")
    public void RESP_13_test_01() {
        // Arrange
        Scanner input = new Scanner("0\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        Card firstCard = game.players[0].getDeck().asList().getFirst();

        // Act
        Card position = game.selectCard(game.players[0]);

        // Assert
        assertFalse(input.hasNextLine());
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game prompts player for a card position - valid position")
    public void RESP_13_test_02() {
        // Arrange
        Scanner input = new Scanner("4\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        Card firstCard = game.players[0].getDeck().asList().get(4);

        // Act
        Card position = game.selectCard(game.players[0]);

        // Assert
        assertFalse(input.hasNextLine());
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game prompts player for a card position - invalid position")
    public void RESP_13_test_03() {
        // Arrange
        Scanner input = new Scanner("13\n0\n");  // 13 is invalid, 0 is valid
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        Card firstCard = game.players[0].getDeck().asList().getFirst();

        // Act
        Card position = game.selectCard(game.players[0]);

        // Assert
        assertFalse(input.hasNextLine()); // Verify that the player was prompted twice
        assertEquals(firstCard, position);
    }

    @Test
    @DisplayName("Game deletes n cards from a player's hand")
    public void RESP_14_test_01() {
        // Arrange
        Scanner input = new Scanner("0\n1\n2\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.adventureDeck.shuffle();
        game.initPlayers();
        int excessCards = 3;
        game.players[0].pickCards(game.adventureDeck.draw(excessCards));

        // Act
        game.trimHand(game.players[0]);

        // Assert
        assertEquals(12, game.players[0].getDeck().size());
    }

    @Test
    @DisplayName("Game displays hand after each trimming")
    public void RESP_14_test_02() {
        // Arrange
        StringWriter output = new StringWriter();
        Scanner input = new Scanner("0\n0\n0\n");
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        game.adventureDeck.shuffle();
        game.initPlayers();
        int excessCards = 3;
        game.players[0].pickCards(game.adventureDeck.draw(excessCards));
        List<Card> orgHand = new ArrayList<>(game.players[0].getDeck().asList());

        // Act
        game.trimHand(game.players[0]);

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
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        game.adventureDeck.shuffle();
        game.adventureDeck.discard(game.adventureDeck.draw(100));

        // Act
        game.adventureDeck.draw();

        // Assert
        assertEquals(99, game.adventureDeck.size());
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest")
    public void RESP_16_test_01() {
        // Arrange
        Scanner input = new Scanner("y\n");  // Player 1 sponsors the quest
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        Card card = new Card("Quest", 'Q', 2);
        int currentPlayer = 0;

        // Act
        Player sponsor = game.findSponsor(currentPlayer, card);

        // Assert
        assertTrue(output.toString().contains("P1: Do you want to sponsor the quest " + card + "? (y/n)"));
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
        assertEquals(game.players[0], sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - player declines")
    public void RESP_16_test_02() {
        // Arrange
        Scanner input = new Scanner("n\ny\n");  // Player 2 sponsors the quest
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        Card card = new Card("Quest", 'Q', 5);
        int currentPlayer = 0;

        // Act
        Player sponsor = game.findSponsor(currentPlayer, card);

        // Assert
        assertTrue(output.toString().contains("P2: Do you want to sponsor the quest " + card + "? (y/n)"));
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
        assertEquals(game.players[1], sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - asks all players")
    public void RESP_16_test_03() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\ny\n");  // Player 4 sponsors the quest
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        Card card = new Card("Quest", 'Q', 3);
        int currentPlayer = 0;

        // Act
        Player sponsor = game.findSponsor(currentPlayer, card);

        // Assert
        for (int i = currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString().contains("P" + (i + 1) + ": Do you want to sponsor the quest " + card + "? (y/n)"));
        }
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
        assertEquals(game.players[3], sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - all decline")
    public void RESP_16_test_04() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\nn\n");  // No player sponsors the quest
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        Card card = new Card("Quest", 'Q', 4);
        int currentPlayer = 0;

        // Act
        Player sponsor = game.findSponsor(currentPlayer, card);

        // Assert
        for (int i = currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString().contains("P" + (i + 1) + ": Do you want to sponsor the quest " + card + "? (y/n)"));
        }
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
        assertNull(sponsor);
    }

    @Test
    @DisplayName("Game prompts players to sponsor a quest - starts from non 0 position")
    public void RESP_16_test_05() {
        // Arrange
        Scanner input = new Scanner("n\nn\ny\n");  // Player 4 sponsors the quest
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        Card card = new Card("Quest", 'Q', 4);
        int currentPlayer = 1;

        // Act
        Player sponsor = game.findSponsor(currentPlayer, card);

        // Assert
        for (int i = currentPlayer; i < PLAYERS_AMOUNT; ++i) {
            assertTrue(output.toString().contains("P" + (i + 1) + ": Do you want to sponsor the quest " + card + "? (y/n)"));
        }
        assertFalse(output.toString().contains("P1: Do you want to sponsor the quest " + card + "? (y/n)"));
        assertFalse(input.hasNextLine());  // Verify that the players was prompted
        assertEquals(game.players[3], sponsor);
    }

    @Test
    @DisplayName("Game displays hand during stage setup")
    public void RESP_17_test_01() {
        // Arrange
        Scanner input = new Scanner("0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        int stage = 1;

        // Act
        game.setupStage(game.players[0], stage, /*previousStage*/ null);

        // Assert
        assertTrue(output.toString().contains(game.players[0].getDeck().toString()));
    }

    @Test
    @DisplayName("Game prompts sponsor to select cards for stage")
    public void RESP_17_test_02() {
        // Arrange
        Scanner input = new Scanner("0\n1\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        int stage = 1;

        // Act
        game.setupStage(game.players[0], stage, /*previousStage*/ null);

        // Assert
        assertTrue(output.toString().contains("Select a card for stage " + stage + " or enter 'Quit' to finish stage setup"));
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }
}