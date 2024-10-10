package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Game {
    final Player[] players;
    int currentPlayer;
    Deck adventureDeck, questDeck;
    PrintWriter output;

    public Game(int playersAmount, PrintWriter output) {
        this.output = output;
        currentPlayer = 0;
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
        do {
            Player player = players[currentPlayer];
            playTurn(player);
            currentPlayer = (currentPlayer + 1) % players.length;
        } while ((winners = checkWinners()).isEmpty());
        displayWinners(winners);
    }

    void playTurn(Player player) {
        print(player + "'s turn - Hand: " + player.getDeck());
    }

    void initPlayers() {
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
        print("Winners: " + String.join(", ", winnersNames));
    }

    void print(Object message) {
        output.println(message);
        output.flush();
    }
}
