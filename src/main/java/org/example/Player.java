package org.example;

import java.util.List;

class Player {
    Deck hand;

    public Player() {
        hand = new Deck();
    }

    Deck getDeck() {
        return hand;
    }

    public void pickCards(List<Card> cards) {
        hand.add(cards);
    }
}
