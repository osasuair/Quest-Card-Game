package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Deck {
    static final int[] FOES_AMOUNT = {8, 7, 8, 7, 7, 4, 4, 2, 2, 1};
    static final int[] FOES_VALUES = {5, 10, 15, 20, 25, 30, 35, 40, 50, 70};
    static final char[] WEAPONS = {'D', 'H', 'S', 'B', 'L', 'E'};
    static final int[] WEAPONS_AMOUNTS = {6, 12, 16, 8, 6, 2};
    static final int[] WEAPONS_VALUES = {5, 10, 10, 15, 20, 30};

    static final int[] QUEST_AMOUNTS = {3, 4, 3, 2};
    static final int[] QUEST_VALUES = {2, 3, 4, 5};
    static final int[] EVENTS_AMOUNTS = {1, 2, 2};
    static final String[] EVENTS = {"Plague", "Queen’s favor", "Prosperity"};

    private final ArrayList<Card> deck = new ArrayList<>();

    void initAdventureDeck() {
        for (int i = 0; i < FOES_AMOUNT.length; ++i) {
            for (int j = 0; j < FOES_AMOUNT[i]; ++j) {
                deck.add(new Card("Adv", 'F', FOES_VALUES[i]));
            }
        }

        for (int i = 0; i < WEAPONS.length; ++i) {
            for (int j = 0; j < WEAPONS_AMOUNTS[i]; ++j) {
                deck.add(new Card("Adv", WEAPONS[i], WEAPONS_VALUES[i]));
            }
        }
    }

    void initQuestDeck() {
        for (int i = 0; i < QUEST_AMOUNTS.length; ++i) {
            for (int j = 0; j < QUEST_AMOUNTS[i]; ++j) {
                deck.add(new Card("Quest", 'Q', QUEST_VALUES[i]));
            }
        }

        for (int i = 0; i < EVENTS_AMOUNTS.length; ++i) {
            for (int j = 0; j < EVENTS_AMOUNTS[i]; ++j) {
                deck.add(new Card(EVENTS[i]));
            }
        }
    }

    void shuffle() {
        Collections.shuffle(deck);
    }

    Card draw() {
        if (deck.isEmpty()) {
            return null;
        }
        return deck.removeFirst();
    }

    List<Card> draw(int amount) {
        if (deck.size() < amount) {
            return null;
        }
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            cards.add(draw());
        }
        return cards;
    }

    void add(List<Card> cards) {
        deck.addAll(cards);
    }

    List<Card> asList() {
        return deck;
    }

    int size() {
        return deck.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Deck other)) {
            return false;
        }
        return deck.equals(other.deck);
    }
}
