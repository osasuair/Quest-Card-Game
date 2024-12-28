package org.quest;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(new Scanner(System.in), new PrintWriter(System.out));
        game.start();
    }
}