package org.example;

public class Main {
    public static final int PLAYERS_AMOUNT = 4;

    public static void main(String[] args) {
        Game game = new Game(PLAYERS_AMOUNT);
        game.start();
    }
}