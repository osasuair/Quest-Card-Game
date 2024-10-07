package org.example;

public class Main {
    public static void main(String[] args) {
        Deck adventureDeck, questDeck;
        adventureDeck = new Deck();
        questDeck = new Deck();

        adventureDeck.initAdventureDeck();
        questDeck.initQuestDeck();
        adventureDeck.shuffle();
        questDeck.shuffle();
    }
}