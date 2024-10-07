package org.example;

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

    Deck getDeck() {
        return hand;
    }

    public void pickCards(List<Card> cards) {
        hand.add(cards);
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}
