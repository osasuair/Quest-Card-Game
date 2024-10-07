package org.example;

public class Game {
    final Player[] players;
    Deck adventureDeck, questDeck;

    public Game(int playersAmount) {
        players = new Player[playersAmount];
        for (int i = 0; i < playersAmount; ++i) {
            players[i] = new Player();
        }
        adventureDeck = new Deck();
        questDeck = new Deck();
    }

    public void start() {
        adventureDeck.initAdventureDeck();
        questDeck.initQuestDeck();
        adventureDeck.shuffle();
        questDeck.shuffle();

        initPlayers();
    }

    public void initPlayers() {
        for (Player player : players) {
            player.pickCards(adventureDeck.draw(12));
        }
    }
}
