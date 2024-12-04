package org.quest;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameState {
    Player[] players;
    int currentPlayer;
    Deck adventureDeck, questDeck;
    Card currentCard;
    List<Player> winners;
    QuestState questState;

    GameState() {
        currentPlayer = 0;
        adventureDeck = new Deck();
        questDeck = new Deck();
        winners = new ArrayList<>();

        int PLAYERS_NUM = 4;
        players = new Player[PLAYERS_NUM];
        for (int i = 0; i < PLAYERS_NUM; ++i) {
            players[i] = new Player(i + 1);
        }
    }

    public QuestState getQuestState() {
        return questState;
    }

    public List<Player> getWinners() {
        return winners;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public Deck getQuestDeck() {
        return questDeck;
    }

    public void setQuestDeck(Deck questDeck) {
        this.questDeck = questDeck;
    }

    public Deck getAdventureDeck() {
        return adventureDeck;
    }

    public void setAdventureDeck(Deck adventureDeck) {
        this.adventureDeck = adventureDeck;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
