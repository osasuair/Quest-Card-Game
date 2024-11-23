package org.example;

import java.util.Collections;
import java.util.List;

public class Player {
    int id;
    int shields;
    private final Deck hand;

    public Player(int id) {
        this.id = id;
        hand = new Deck();
        shields = 0;
    }

    public Card getCard(int index) {
        if (index < 0 || index >= hand.size()) {
            return null;
        }
        return hand.asList().get(index);
    }

    public Card playCard(Card card) {
        hand.asList().remove(card);
        return card;
    }

    Deck getDeck() {
        sortHand();
        return hand;
    }

    public void pickCards(List<Card> cards) {
        hand.add(cards);
    }

    public void sortHand() {
        Collections.sort(hand.asList());
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}
