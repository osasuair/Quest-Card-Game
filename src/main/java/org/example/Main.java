package org.example;

import java.io.PrintWriter;

public class Main {
    public static final int PLAYERS_AMOUNT = 4;

    public static void main(String[] args) {
        PrintWriter output = new PrintWriter(System.out);
        Game game = new Game(PLAYERS_AMOUNT, output);
        game.start();
        output.flush();
    }
}