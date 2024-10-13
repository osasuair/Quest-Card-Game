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
        game.setupStage(game.players[0], stage, /*previousStage*/ 0);

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
        game.setupStage(game.players[0], stage, /*previousStage*/ 0);

        // Assert
        assertTrue(output.toString().contains("Select a card for stage " + stage + " or enter 'Quit' to finish stage setup"));
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - valid")
    public void RESP_18_test_01() {
        // Arrange
        Scanner input = new Scanner("0\n1\nQuit\n");
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'H', 10));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertEquals(2, selected.size());
        assertEquals(hand.getFirst(), selected.getFirst());
        assertEquals(hand.get(2), selected.getLast());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid number")
    public void RESP_18_test_02() {
        // Arrange
        StringWriter output = new StringWriter();
        Scanner input = new Scanner("-1\n0\n1\nQuit\n");
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'H', 10));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertTrue(output.toString().contains("Invalid card index"));
        assertEquals(2, selected.size());
        assertEquals(hand.get(0), selected.get(0));
        assertEquals(hand.get(2), selected.get(1));
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid foe")
    public void RESP_18_test_03() {
        // Arrange
        Scanner input = new Scanner("1\n0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'F', 15),
                                  new Card("Adv", 'H', 10));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertTrue(output.toString().contains("Invalid card, only one foe card is allowed (Sole foe)"));
        assertEquals(1, selected.size());
        assertEquals(hand.get(1), selected.getFirst());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid repeated weapon")
    public void RESP_18_test_04() {
        // Arrange
        Scanner input = new Scanner("0\n0\n0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertTrue(output.toString().contains("Invalid card, Weapon cards must be different (non-repeated weapon card)"));
        assertEquals(2, selected.size());
        assertEquals(hand.getFirst(), selected.getFirst());
        assertEquals(hand.get(2).value, selected.get(1).value);
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - stage empty")
    public void RESP_19_test_01() {
        // Arrange
        Scanner input = new Scanner("Quit\n0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertTrue(output.toString().contains("A stage cannot be empty"));
        assertEquals(1, selected.size());
        assertEquals(hand.getFirst(), selected.getFirst());
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - insufficient value")
    public void RESP_19_test_02() {
        // Arrange
        Scanner input = new Scanner("0\n1\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);
        int stage = 3;

        // Act
        assertThrows(NoSuchElementException.class, () -> game.setupStage(game.players[0], stage, 30));

        // Assert
        assertTrue(output.toString().contains("Insufficient value for this stage"));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - insufficient value (equal)")
    public void RESP_19_test_03() {
        // Arrange
        Scanner input = new Scanner("0\n0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);
        int stage = 3;

        // Act
        assertThrows(NoSuchElementException.class, () -> game.setupStage(game.players[0], stage, 20));

        // Assert
        assertTrue(output.toString().contains("Insufficient value for this stage"));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - valid (sufficient value)")
    public void RESP_19_test_04() {
        // Arrange
        Scanner input = new Scanner("0\n2\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);
        int stage = 3;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 30);

        // Assert
        assertEquals(2, selected.size());
        assertEquals(hand.get(0), selected.get(0));
        assertEquals(hand.get(3), selected.get(1));
        assertTrue(output.toString().contains("Stage " + stage + ": " + selected));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during stage setup - valid (first stage)")
    public void RESP_19_test_05() {
        // Arrange
        Scanner input = new Scanner("0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);
        int stage = 1;

        // Act
        List<Card> selected = game.setupStage(game.players[0], stage, 0);

        // Assert
        assertFalse(output.toString().contains("Insufficient value for this stage"));
        assertFalse(output.toString().contains("A stage cannot be empty"));
        assertTrue(output.toString().contains("Stage " + stage + ": " + selected));
    }

    @Test
    @DisplayName("Game determines and displays eligible participants for a stage")
    public void RESP_20_test_01() {
        // Arrange
        Scanner input = new Scanner("0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players));

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        assertTrue(output.toString().contains("Eligible Players: " + participants));
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage")
    public void RESP_21_test_01() {
        // Arrange
        Scanner input = new Scanner("n\ny\nn\nn\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players));

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        for (Player p : game.players)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        List<Player> expectedParticipants = new ArrayList<>(Arrays.asList(game.players[0], game.players[2], game.players[3]));
        assertEquals(expectedParticipants, participants);
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage - all withdraw")
    public void RESP_21_test_02() {
        // Arrange
        Scanner input = new Scanner("y\ny\ny\ny\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players));

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        for (Player p : game.players)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        assertTrue(participants.isEmpty());
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game prompts eligible participants to withdraw or participate in a stage - all participate")
    public void RESP_21_test_03() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\nn\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players));

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        for (Player p : game.players)
            assertTrue(output.toString().contains(p + ": Do you want to withdraw from the quest? (y/n)"));
        assertEquals(Arrays.asList(game.players), participants);
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game draws 1 adventure card for each participant in stage")
    public void RESP_22_test_01() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        game.adventureDeck.initAdventureDeck();
        int deckSize = game.adventureDeck.size();

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        assertEquals(deckSize - participants.size(), game.adventureDeck.size());
        for (Player p : participants)
            assertTrue(output.toString().contains(p + " draws 1 Adventure card"));
    }

    @Test
    @DisplayName("Game draws 1 adventure card for each participant in stage - trim required")
    public void RESP_22_test_02() {
        // Arrange
        Scanner input = new Scanner("n\ny\ny\n0\n");  // only P1 tackles the stage and trims first card
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        int deckSize = game.adventureDeck.size();

        // Act
        try {
            game.playStage(participants, new ArrayList<>());
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        assertEquals(deckSize - 1, game.adventureDeck.size());
        assertEquals(12, game.players[0].getDeck().size());
        assertTrue(output.toString().contains("P1's trimmed hand: "));
    }

    @Test
    @DisplayName("Game ends quest if there is no sponsor")
    public void RESP_23_test_01() {
        // Arrange
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            Player findSponsor(int currentPlayer, Card card) { return null; }  // Mock no sponsor found
        };

        // Act
        game.handleQuestCard(game.players[0], new Card("Quest", 'Q', 2));

        // Assert
        System.out.println(output);
        assertTrue(output.toString().contains("No sponsor found for the Quest"));
    }

    @Test
    @DisplayName("Game does not end quest if there is a sponsor")
    public void RESP_23_test_02() {
        // Arrange
        Scanner input = new Scanner("");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            Player findSponsor(int currentPlayer, Card card) { return new Player(0); }  // Mock sponsor found
        };

        // Act
        try {
            game.handleQuestCard(game.players[0], new Card("Quest", 'Q', 2));
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        assertFalse(output.toString().contains("No sponsor found for quest"));
    }

    @Test
    @DisplayName("Game displays hand during attack setup")
    public void RESP_24_test_01() {
        // Arrange
        Scanner input = new Scanner("0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        Player p1 = game.players[0];

        // Act
        game.setupAttack(p1);

        // Assert
        assertTrue(output.toString().contains(p1 + "'s Deck: " + p1.getDeck()));
    }

    @Test
    @DisplayName("Game prompts participant to select a card for attack")
    public void RESP_24_test_02() {
        // Arrange
        Scanner input = new Scanner("0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        Player p1 = game.players[0];

        // Act
        game.setupAttack(p1);

        // Assert
        assertTrue(output.toString().contains("Select a card for the attack or enter 'Quit' to finish attack setup"));
        assertFalse(input.hasNextLine());  // Verify that the player was prompted
    }

    @Test
    @DisplayName("Game adds a selected valid card to attack or re-prompts if invalid - valid")
    public void RESP_25_test_01() {
        // Arrange
        Scanner input = new Scanner("0\n1\nQuit\n");
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> attack = game.setupAttack(game.players[0]);

        // Assert
        assertEquals(2, attack.size());
        assertEquals(hand.getFirst(), attack.getFirst());
        assertEquals(hand.getLast(), attack.getLast());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid number")
    public void RESP_25_test_02() {
        // Arrange
        StringWriter output = new StringWriter();
        Scanner input = new Scanner("-1\n0\n1\nQuit\n");
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'E', 30));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> attack = game.setupAttack(game.players[0]);

        // Assert
        assertTrue(output.toString().contains("Invalid card index"));
        assertEquals(2, attack.size());
        assertEquals(hand.get(0), attack.get(0));
        assertEquals(hand.get(2), attack.get(1));
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - A Foe")
    public void RESP_25_test_03() {
        // Arrange
        Scanner input = new Scanner("0\n2\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'F', 10),
                                  new Card("Adv", 'F', 15),
                                  new Card("Adv", 'H', 10));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> attack = game.setupAttack(game.players[0]);

        // Assert
        assertTrue(output.toString().contains("Invalid card, Foe cards are not allowed in attack"));
        assertEquals(1, attack.size());
        assertEquals(hand.get(2), attack.getFirst());
    }

    @Test
    @DisplayName("Game adds a selected valid card to stage or re-prompts if invalid - invalid repeated weapon")
    public void RESP_25_test_04() {
        // Arrange
        Scanner input = new Scanner("0\n0\n0\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        game.adventureDeck.initAdventureDeck();
        List<Card> hand = List.of(new Card("Adv", 'D', 5),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> selected = game.setupAttack(game.players[0]);

        // Assert
        System.out.println(output);
        assertTrue(output.toString().contains("Invalid card, Weapon cards must be different (non-repeated weapon card)"));
        assertEquals(2, selected.size());
        assertEquals(hand.getFirst(), selected.getFirst());
        assertEquals(hand.get(2).value, selected.get(1).value);
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during attack setup - attack empty")
    public void RESP_26_test_01() {
        // Arrange
        Scanner input = new Scanner("Quit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'D', 5),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> attack = game.setupAttack(game.players[0]);

        // Assert
        assertEquals(0, attack.size());
        assertTrue(output.toString().contains(game.players[0] + "'s attack: []"));
    }

    @Test
    @DisplayName("Game manages when 'Quit' is entered during attack setup - attack non-empty")
    public void RESP_26_test_02() {
        // Arrange
        Scanner input = new Scanner("0\n1\nQuit\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Card> hand = List.of(new Card("Adv", 'D', 5),
                                  new Card("Adv", 'S', 10),
                                  new Card("Adv", 'S', 10));
        game.players[0].getDeck().add(hand);

        // Act
        List<Card> attack = game.setupAttack(game.players[0]);

        // Assert
        List<Card> expected = List.of(hand.get(0), hand.get(2));
        assertEquals(2, attack.size());
        assertEquals(expected, attack);
        assertTrue(output.toString().contains(game.players[0] + "'s attack: " + expected));
    }

    @Test
    @DisplayName("Game sets up the attack for each participant in stage")
    public void RESP_27_test_01() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            List<Card> setupAttack(Player p) {  // Mock setup attack to return an easily verifiable attack
                if (p.id == 1)
                    return List.of(new Card("Adv", 'S', 10));
                return p.getDeck().asList().subList(0, 2);
            }
        };
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));

        // Act
        Map<Player, List<Card>> attacks = game.setupAttacks(participants);

        // Assert
        assertEquals(3, attacks.size());
        for (Player p : participants) {
            assertTrue(attacks.containsKey(p));
            assertEquals(p.id == 1 ? 1 : 2, attacks.get(p).size());
        }
    }

    @Test
    @DisplayName("Game sets up attacks for participants in stage")
    public void RESP_27_test_02() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n0\n0\n0\n");
        StringWriter output = new StringWriter();
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output)) {
            List<Card> setupAttack(Player p) {  // Mock setup attack to return an easily verifiable attack
                return List.of(p.playCard(p.getCard(0)), p.playCard(p.getCard(1)));
            }
        };
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));

        // Act
        game.playStage(participants, new ArrayList<>());

        // Assert
        for (Player p : participants)
            assertEquals(10, p.getDeck().size());
    }

    @Test
    @DisplayName("Game resolves an attack against a stage")
    public void RESP_28_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        List<Card> attack = List.of(new Card("Adv", 'S', 10),
                                    new Card("Adv", 'H', 10));

        // Act
        boolean successEquals = game.resolveAttack(attack, 20);
        boolean successGreater = game.resolveAttack(attack, 15);
        boolean failureLess = game.resolveAttack(attack, 25);

        // Assert
        assertTrue(successEquals);
        assertTrue(successGreater);
        assertFalse(failureLess);
    }

    @Test
    @DisplayName("Game resolves the attack for each participant - 1 survivor")
    public void RESP_29_test_01() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        Map<Player, List<Card>> attacks = getAttack1(participants);
        int stageValue = 20;

        // Act
        boolean continueQuest = game.resolveAttacks(participants, attacks, stageValue);

        // Assert
        assertTrue(continueQuest);
        assertEquals(List.of(game.players[0]), participants);
    }

    @Test
    @DisplayName("Game resolves the attack for each participants - no survivors")
    public void RESP_29_test_02() {
        // Arrange
        Game game = new Game(PLAYERS_AMOUNT, input, new PrintWriter(output));
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        Map<Player, List<Card>> attacks = getAttack2(participants);
        int stageValue = 20;

        // Act
        boolean continueQuest = game.resolveAttacks(participants, attacks, stageValue);

        // Assert
        assertFalse(continueQuest);
        assertTrue(participants.isEmpty());
    }

    @Test
    @DisplayName("Game resolves attacks for participants in stage - all survive")
    public void RESP_29_test_03() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n0\n0\n0\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output) {
            Map<Player, List<Card>> setupAttacks(List<Player> participants) {
                return getAttack1(participants);
            }
        };
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        List<Card> stage = List.of(new Card("Adv", 'H', 10));

        // Act
        boolean continueQuest = game.playStage(participants, stage);

        // Assert
        assertTrue(continueQuest);
        assertEquals(new ArrayList<>(Arrays.asList(game.players)).subList(0, 3), participants);
    }

    @Test
    @DisplayName("Game resolves attacks for participants in stage - no survivors")
    public void RESP_29_test_04() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n0\n0\n0\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output) {
            Map<Player, List<Card>> setupAttacks(List<Player> participants) {
                return getAttack2(participants);
            }
        };
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        List<Card> stage = List.of(new Card("Adv", 'F', 70));

        // Act
        boolean continueQuest = game.playStage(participants, stage);

        // Assert
        assertFalse(continueQuest);
        assertTrue(participants.isEmpty());
    }

    @Test
    @DisplayName("Game ends a stage if all participants withdraw")
    public void RESP_30_test_01() {
        // Arrange
        Scanner input = new Scanner("y\ny\ny\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));

        // Act
        boolean continueQuest = game.playStage(participants, new ArrayList<>());

        // Assert
        assertFalse(continueQuest);
    }

    @Test
    @DisplayName("Game discards all cards from participants used in attacks")
    public void RESP_31_test_01() {
        // Arrange
        Scanner input = new Scanner("n\nn\nn\n0\n0\n0\n");
        Game game = new Game(PLAYERS_AMOUNT, input, output) {
            Map<Player, List<Card>> setupAttacks(List<Player> participants) {
                return getAttack2(participants);
            }
        };
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        List<Player> participants = new ArrayList<>(Arrays.asList(game.players).subList(0, 3));
        List<Card> stage = List.of(new Card("Adv", 'F', 70));

        // Act
        game.playStage(participants, stage);

        // Assert
        // (3) Participants attacks all have 1 card each, plus the trim for each player
        assertEquals(3 + 3, game.adventureDeck.discardSize());
    }

    @Test
    @DisplayName("Game sets up stages of a quest")
    public void RESP_32_test_01() {
        // Arrange
        String inputStr = "0\nQuit\n" +    // Stage 1
                          "1\nQuit\n" +    // Stage 2
                          "0\n0\nQuit\n" + // Stage 3
                          "0\nQuit\n";     // Stage 4
        Scanner input = new Scanner(inputStr);
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        Player sponsor = game.players[0];
        initSponsorHand(sponsor);
        Card card = new Card("Quest", 'Q', 4);

        // Act
        List<List<Card>> questSetup = game.setupQuest(sponsor, card);

        // Assert
        assertEquals(card.value, questSetup.size());
        int previous = 0;
        for (List<Card> stage : questSetup) {
            int value = stage.stream().mapToInt(c -> c.value).sum();
            assertTrue(value > previous);
            previous = value;
        }
    }

    @Test
    @DisplayName("Game sets up the quest Stages")
    public void RESP_32_test_02() {
        // Arrange
        Scanner input = new Scanner("y\n" +   // Sponsor
                                    "0\nQuit\n" +    // Stage 1
                                    "1\nQuit\n" +    // Stage 2
                                    "0\n0\nQuit\n" + // Stage 3
                                    "0\nQuit\n");    // Stage 4
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.adventureDeck.initAdventureDeck();
        Player sponsor = game.players[0];
        initSponsorHand(sponsor);  // Sponsor has 5 cards

        // Act
        try {
            // Sponsor uses 5 cards to set up the quest
            game.handleQuestCard(sponsor, new Card("Quest", 'Q', 4));
        } catch (NoSuchElementException e) {
            // Ignore NoSuchElementException
        }

        // Assert
        assertEquals(0, sponsor.getDeck().size());
    }


    private static Map<Player, List<Card>> getAttack1(List<Player> participants) {
        Map<Player, List<Card>> attacks = new HashMap<>();
        attacks.put(participants.get(0), List.of(new Card("Adv", 'E', 30))); // attack > stage
        attacks.put(participants.get(1), List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(2), List.of(new Card("Adv", 'H', 10)));
        return attacks;
    }

    private static Map<Player, List<Card>> getAttack2(List<Player> participants) {
        Map<Player, List<Card>> attacks = new HashMap<>();
        attacks.put(participants.get(0), List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(1), List.of(new Card("Adv", 'H', 10)));
        attacks.put(participants.get(2), List.of(new Card("Adv", 'H', 10)));
        return attacks;
    }

    private static void initSponsorHand(Player sponsor) {
        sponsor.getDeck().add(List.of(new Card("Adv", 'F', 5),
                                      new Card("Adv", 'F', 5),
                                      new Card("Adv", 'S', 10),
                                      new Card("Adv", 'H', 10),
                                      new Card("Adv", 'E', 30)));
    }
}