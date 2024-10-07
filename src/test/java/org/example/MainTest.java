package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
