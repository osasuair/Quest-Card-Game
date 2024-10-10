package org.example;

import java.util.Collections;
import java.util.List;

class Player {
    int id;
    Deck hand;
    int shields;

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
