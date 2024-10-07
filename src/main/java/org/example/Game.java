package org.example;

import java.util.ArrayList;
import java.util.List;

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

        List<Player> winners = checkWinners();
        while (true) {
            if (!(winners = checkWinners()).isEmpty()) {
                break;
            }
            break;
        }
    }

    public void initPlayers() {
        for (Player player : players) {
            player.pickCards(adventureDeck.draw(12));
        }
    }

    List<Player> checkWinners() {
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.shields >= 7) {
                winners.add(player);
            }
        }
        return winners;
    }

}
