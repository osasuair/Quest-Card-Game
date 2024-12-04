package org.quest;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static final int PLAYERS_AMOUNT = 4;

    public static void main(String[] args) {
        Game game = new Game(PLAYERS_AMOUNT, new Scanner(System.in), new PrintWriter(System.out));
        game.start();
    }
}