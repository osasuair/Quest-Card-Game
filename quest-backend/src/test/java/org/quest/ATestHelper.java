package org.quest;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.quest.Main.PLAYERS_AMOUNT;

public class ATestHelper {
    static final List<Card> p1Hand = List.of(new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'D', 5),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'L', 20));
    static final List<Card> p2Hand = List.of(new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'F', 40),
                                             new Card("Adv", 'D', 5),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'E', 30));
    static final List<Card> p3Hand = List.of(new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'D', 5),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'L', 20));
    static final List<Card> p4Hand = List.of(new Card("Adv", 'F', 5),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'F', 15),
                                             new Card("Adv", 'F', 40),
                                             new Card("Adv", 'D', 5),
                                             new Card("Adv", 'D', 5),
                                             new Card("Adv", 'S', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'H', 10),
                                             new Card("Adv", 'B', 15),
                                             new Card("Adv", 'L', 20),
                                             new Card("Adv", 'E', 30));

    static Game rigGameSetupATest1(Scanner input, PrintWriter output) {
        return new Game(PLAYERS_AMOUNT, input, output) {
            @Override
            void setupGame() {
                super.setupGame(); // decks created, hands of players drawn randomly
                Card questCard = new Card("Quest", 'Q', 4);
                questDeck.asList().addFirst(questCard); // Rig quest deck to start with Q4

                // Rig Player's hands
                for(Player p : players)
                    p.getDeck().asList().clear();
                players[0].getDeck().add(p1Hand);
                players[1].getDeck().add(p2Hand);
                players[2].getDeck().add(p3Hand);
                players[3].getDeck().add(p4Hand);

                // Rig adventure deck to draw specific cards in order
                List<Card> drawnCards = new ArrayList<>();
                drawnCards.addAll(List.of(new Card("Adv", 'F', 30),   // Stage 1
                                          new Card("Adv", 'S', 10),
                                          new Card("Adv", 'B', 15)));
                drawnCards.addAll(List.of(new Card("Adv", 'F', 10),   // Stage 2
                                          new Card("Adv", 'L', 20),
                                          new Card("Adv", 'L', 20)));
                drawnCards.addAll(List.of(new Card("Adv", 'B', 15),   // Stage 3
                                          new Card("Adv", 'S', 10)));
                drawnCards.addAll(List.of(new Card("Adv", 'F', 30),   // Stage 4
                                          new Card("Adv", 'L', 20)));
                adventureDeck.asList().addAll(0, drawnCards);
            }
        };
    }
}
