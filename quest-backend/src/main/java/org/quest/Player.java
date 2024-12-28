package org.quest;

import java.util.Collections;
import java.util.List;

public class Player {
    final int id;
    private final Deck hand;
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

    public Card playCard(Card card) {
        hand.asList().remove(card);
        return card;
    }

    boolean hasCard(Card card) {
        return hand.asList().contains(card);
    }

    public int getId() {
        return id;
    }

    public int getShields() {
        return shields;
    }

    public Deck getDeck() {
        sortHand();
        return hand;
    }

    public void pickCards(List<Card> cards) {
        hand.add(cards);
        sortHand();
    }

    public void sortHand() {
        Collections.sort(hand.asList());
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}
