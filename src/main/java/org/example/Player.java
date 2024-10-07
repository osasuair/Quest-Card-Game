package org.example;

import java.util.List;

class Player {
    Deck hand;
    int shields;

    public Player() {
        hand = new Deck();
        shields = 0;
    }

    Deck getDeck() {
        return hand;
    }

    public void pickCards(List<Card> cards) {
        hand.add(cards);
    }
}
