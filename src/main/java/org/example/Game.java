package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    final Player[] players;
    int currentPlayer;
    Deck adventureDeck, questDeck;
    PrintWriter output;
    Scanner input;

    public Game(int playersAmount, Scanner input, PrintWriter output) {
        this.output = output;
        this.input = input;
        currentPlayer = 0;
        adventureDeck = new Deck();
        questDeck = new Deck();
        players = new Player[playersAmount];
        for (int i = 0; i < playersAmount; ++i) {
            players[i] = new Player(i+1);
        }
    }

    void initPlayers() {
        for (Player player : players) {
            player.pickCards(adventureDeck.draw(12));
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

        Card card = questDeck.draw();
        print("Player " + player + " drew " + card);

        // Handle event card
        if (card.type != 'Q') {
            handleEventCard(player, card);
        }

        print(player + "'s turn is over, press Enter to continue");
        input.nextLine();
        clearHotseat();
    }

    void handleEventCard(Player player, Card card) {
        switch (card.cardType) {
            case "Plague" -> handlePlague(player);
            case "Queen’s favor" -> handleQueensFavor(player);
            case "Prosperity" -> handleProsperity(player);
        }
    }

    private void handleProsperity(Player player) {
        print("All players draw 2 Adventure cards");
        int currentPlayer = player.id - 1;
        for (int i = 0; i < players.length; ++i) {
            Player p = players[(currentPlayer + i) % players.length];
            print("Player " + p + " draws 2 Adventure cards");
            p.pickCards(adventureDeck.draw(2));
            trimHand(p);
        }
    }

    void handleQueensFavor(Player player) {
        print("Player " + player + " draws 2 Adventure cards");
        player.pickCards(adventureDeck.draw(2));
        trimHand(player);
    }

    void handlePlague(Player player) {
        print("Player " + player + " losses 2 shields");
        player.shields  = (player.shields < 2) ? 0 : player.shields - 2;
    }

    Player findSponsor(int currentPlayer, Card card) {
        Player sponsor = null;
        for (int i = 0; i < players.length && sponsor == null; ++i) {
            Player p = players[(currentPlayer + i) % players.length];
            sponsor = promptSponsor(p, card) ? p : null;
        }
        return sponsor;
    }

    boolean promptSponsor(Player p, Card c) {
        print(p + ": Do you want to sponsor the quest " + c + "? (y/n)");
        return input.nextLine().equals("y");
    }

    Card selectCard(Player player) {
        Card card = null;
        while (card == null) {
            print(player.getDeck());
            print("Select a card: ");
            String cardIndex = input.nextLine();
            card = player.getCard(Integer.parseInt(cardIndex));
            if (card == null) {
                print("Invalid card index");
            }
        }
        return card;
    }

    void trimHand(Player player) {
        int cardsToRemove = computeTrim(player);
        if (cardsToRemove == 0) return;
        print("Player " + player + " has more than 12 cards, select " + cardsToRemove + " cards to discard");
        for(int i = 0; i < cardsToRemove; ++i) {
            adventureDeck.discard(player.playCard(selectCard(player)));
            print(player + "'s trimmed hand: " + player.getDeck());
        }
    }

    int computeTrim(Player player) {
        return player.hand.size() > 12 ? player.hand.size() - 12 : 0;
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

    void clearHotseat() {
        for (int i = 0; i < 10; ++i) {
            output.println();
        }
        output.flush();
    }

    void print(Object message) {
        output.println(message);
        output.flush();
    }
}
