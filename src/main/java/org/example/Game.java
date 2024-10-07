package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Game {
    final Player[] players;
    Deck adventureDeck, questDeck;
    PrintWriter output;

    public Game(int playersAmount, PrintWriter output) {
        this.output = output;
        adventureDeck = new Deck();
        questDeck = new Deck();
        players = new Player[playersAmount];
        for (int i = 0; i < playersAmount; ++i) {
            players[i] = new Player(i+1);
        }
    }

    public void start() {
        adventureDeck.initAdventureDeck();
        questDeck.initQuestDeck();
        adventureDeck.shuffle();
        questDeck.shuffle();

        initPlayers();

        List<Player> winners;
        while (true) {
            if (!(winners = checkWinners()).isEmpty()) {
                break;
            }
            break;
        }
        displayWinners(winners);
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

    void displayWinners(List<Player> winners) {
        String[] winnersNames = new String[winners.size()];
        for(int i = 0; i < winners.size(); ++i) {
            winnersNames[i] = winners.get(i).toString();
        }
        output.println("Winners: " + String.join(", ", winnersNames));
    }
}
