package org.example;

public class Game {
    Deck adventureDeck, questDeck;

    public Game() {
        adventureDeck = new Deck();
        questDeck = new Deck();
    }

    public void start() {
        adventureDeck.initAdventureDeck();
        questDeck.initQuestDeck();
        adventureDeck.shuffle();
        questDeck.shuffle();
    }
}
