package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.example.Main.PLAYERS_AMOUNT;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    @DisplayName("Check Adventure Deck Size after Initialization")
    void RESP_01_test_01() {
        Deck deck = new Deck();

        deck.initAdventureDeck();

        assertEquals(100, deck.size());
    }

    @Test
    @DisplayName("Check Quest Deck Size after Initialization")
    void RESP_01_test_02() {
        Deck deck = new Deck();

        deck.initQuestDeck();

        assertEquals(17, deck.size());
    }

    @Test
    @DisplayName("Check Adventure Deck has correct adventure cards")
    void RESP_01_test_03() {
        Deck deck = new Deck();
        int totalFoes = 0;
        int totalWeapons = 0;

        deck.initAdventureDeck();

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
        Deck deck = new Deck();
        int totalQuests = 0;
        int totalEvents = 0;

        deck.initQuestDeck();

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
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck1.initAdventureDeck();
        deck2.initAdventureDeck();

        boolean same = true;
        deck2.shuffle();
        for (int i = 0; i < 100; ++i) {
            if (!deck1.draw().toString().equals(deck2.draw().toString())) {
                same = false;
                break;
            }
        }

        assertFalse(same);
    }

    @Test
    @DisplayName("Check Player deck contains 12 adventure cards")
    public void RESP_02_test_01() {
        Player player = new Player();
        Deck adventureDeck = new Deck();
        adventureDeck.initAdventureDeck();

        player.pickCards(adventureDeck.draw(12));

        assertEquals(12, player.getDeck().size());
        for (int i = 0; i < 12; ++i) {
            assertEquals("Adv", player.getDeck().draw().cardType);
        }
        assertEquals(88, adventureDeck.size());
    }

    @Test
    @DisplayName("Check all Players have different decks of 12 cards")
    public void RESP_02_test_02() {
        Game game = new Game(PLAYERS_AMOUNT);
        game.adventureDeck.initAdventureDeck();
        game.questDeck.initQuestDeck();
        game.adventureDeck.shuffle();
        game.questDeck.shuffle();

        game.initPlayers();

        for (int i = 0; i < PLAYERS_AMOUNT; ++i) {
            Deck playerDeck = game.players[i].getDeck();
            for (int j = i + 1; j < PLAYERS_AMOUNT; ++j) {
                assertNotEquals(playerDeck, game.players[j].getDeck());
            }
            assertEquals(12, playerDeck.size());
        }

        assertEquals(100-PLAYERS_AMOUNT*12, game.adventureDeck.size());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - No shields given")
    public void RESP_03_test_01() {
        Game game = new Game(PLAYERS_AMOUNT);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();

        List<Player> winners = game.checkWinners();

        assertTrue(winners.isEmpty());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 1 player has 7 shields")
    public void RESP_03_test_02() {
        Game game = new Game(PLAYERS_AMOUNT);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        game.players[0].shields = 7;

        List<Player> winners = game.checkWinners();

        assertEquals(1, winners.size());
        assertEquals(game.players[0], winners.getFirst());
    }

    @Test
    @DisplayName("Game determines winners with 7 or more shields - 3 players have 7 shields")
    public void RESP_03_test_03() {
        Game game = new Game(PLAYERS_AMOUNT);
        game.adventureDeck.initAdventureDeck();
        game.initPlayers();
        game.players[0].shields = 7;
        game.players[1].shields = 15;
        game.players[2].shields = 8;
        game.players[3].shields = 6;

        List<Player> winners = game.checkWinners();

        assertEquals(3, winners.size());
        assertEquals(game.players[0], winners.getFirst());
        assertEquals(game.players[1], winners.get(1));
        assertEquals(game.players[2], winners.getLast());
    }

}
