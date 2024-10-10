package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static final int PLAYERS_AMOUNT = 4;

    public static void main(String[] args) {
        PrintWriter output = new PrintWriter(System.out);
        Scanner input = new Scanner(System.in);
        Game game = new Game(PLAYERS_AMOUNT, input, output);
        game.start();
        output.flush();
    }
}