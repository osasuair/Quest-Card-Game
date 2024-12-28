package org.quest;

import java.util.ArrayList;
import java.util.List;

public class TestGameStates {
    public static boolean getTestState(GameState gs, String testNum) {
        return switch (testNum) {
            case "winner2_game_2winner_quest" -> winner2_game_2winner_quest_start_state(gs);
            case "winner1_game_with_events" -> winner1_game_with_events_start_state(gs);
            case "winner0_quest" -> winner0_quest(gs);
            case "a1_scenario" -> a1_scenario(gs);
            default -> false;
        };
    }

    private static boolean winner1_game_with_events_start_state(GameState gameState) {
        gameState.adventureDeck.initAdventureDeck();

        // Rig Hands
        gameState.players[0].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F10, F10, F15, F15, F20, F20, D5, D5, D5, D5]"));
        gameState.players[1].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F25, F30, H10, H10, S10, S10, S10, B15, B15, L20, L20, E30]"));
        gameState.players[2].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F25, F30, H10, H10, S10, S10, S10, B15, B15, L20, L20, E30]"));
        gameState.players[3].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F25, F30, F70, H10, H10, S10, S10, S10, B15, B15, L20, L20]"));

        // Rig Adventure Deck
        gameState.adventureDeck.asList().addAll(0, pickCardsFromDeck(gameState.adventureDeck, "[F5, F10, F20, F15, F5, F25, F5, F10, F20, F5, F10, F20, " +
                                                                                              "F5, F5, F10, F10, F15, F15, F15, F15, " +
                                                                                              "F25, F25, H10, S10, B15, F40, D5, D5, " +
                                                                                              "F30, F25, " +
                                                                                              "B15, H10, F50, S10, S10, F40, F50, " +
                                                                                              "H10, H10, H10, S10, S10, S10, S10, F35]"));

        // Rig Quest Deck
        gameState.questDeck.add(List.of(new Card("Quest", 'Q', 4),
                                        new Card("Plague"),
                                        new Card("Prosperity"),
                                        new Card("Queen's favor"),
                                        new Card("Quest", 'Q', 3),
                                        new Card("Plague")));

        return true;
    }

    private static boolean winner2_game_2winner_quest_start_state(GameState gameState) {
        gameState.adventureDeck.initAdventureDeck();

        // Rig Hands
        gameState.players[0].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F10, F10, F15, F15, D5, H10, H10, B15, B15, L20]"));
        gameState.players[1].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F40, F50, H10, H10, S10, S10, S10, B15, B15, L20, L20, E30]"));
        gameState.players[2].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F5, F5, D5, D5, D5, H10, H10, H10, H10, H10]"));
        gameState.players[3].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F50, F70, H10, H10, S10, S10, S10, B15, B15, L20, L20, E30]"));

        // Rig Adventure Deck
        gameState.adventureDeck.asList().addAll(0, pickCardsFromDeck(gameState.adventureDeck, "[F5, F40, F10, F10, F30, F30, F15, F15, F20, " +
                                                                                              "F5, F10, F15, F15, F20, F20, F20, F20, F25, F25, F30, " +
                                                                                              "D5, D5, F15, F15, F25, F25, " +
                                                                                              "F20, F20, F25, F30, S10, B15, B15, L20]"));

        // Rig Quest Deck
        gameState.questDeck.add(List.of(new Card("Quest", 'Q', 4),
                                        new Card("Quest", 'Q', 3),
                                        new Card("Plague")));
        return true;
    }

    private static boolean winner0_quest(GameState gameState) {
        gameState.adventureDeck.initAdventureDeck();

        // Rig Hands
        gameState.players[0].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F50, F70, D5, D5, S10, S10, H10, H10, B15, B15, L20, L20]"));
        gameState.players[1].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F10, F15, F15, F20, F20, F25, F30, F30, F40, E30]"));
        gameState.players[2].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F10, F15, F15, F20, F20, F25, F25, F30, F40, L20]"));
        gameState.players[3].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F10, F15, F15, F20, F20, F25, F25, F30, F50, E30]"));

        // Rig Adventure Deck
        gameState.adventureDeck.asList().addAll(0, pickCardsFromDeck(gameState.adventureDeck, "[F5, F15, F10, " +
                                                                                              "F5, F10, F15, D5, D5, D5, D5, H10, H10, H10, H10, S10, S10, S10]"));

        // Rig Quest Deck
        gameState.questDeck.add(List.of(new Card("Quest", 'Q', 2),
                                        new Card("Plague")));

        return true;
    }

    private static boolean a1_scenario(GameState gameState) {
        gameState.adventureDeck.initAdventureDeck();
        gameState.adventureDeck.shuffle();

        // Rig Hands
        gameState.players[0].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F15, F15, D5, S10, S10, H10, H10, B15, B15, L20]"));
        gameState.players[1].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F15, F15, F40, D5, S10, H10, H10, B15, B15, E30]"));
        gameState.players[2].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F5, F5, F15, D5, S10, S10, S10, H10, H10, B15, L20]"));
        gameState.players[3].pickCards(pickCardsFromDeck(gameState.adventureDeck, "[F5, F15, F15, F40, D5, D5, S10, H10, H10, B15, L20, E30]"));

        gameState.adventureDeck.asList().addAll(0, pickCardsFromDeck(gameState.adventureDeck, "[F30, S10, B15, " +
                                                                                              "F10, L20, L20, " +
                                                                                              "B15, S10, " +
                                                                                              "F30, L20, " +
                                                                                              "F10, F30, F20, F20]"));

        gameState.questDeck.add(List.of(new Card("Quest", 'Q', 4),
                                        new Card("Plague")));

        return true;
    }


    /**
     * Helper function to pick cards from the adventure deck based on a string representation.
     *
     * @param adventureDeck The adventure deck to pick cards from.
     * @param hand          A string representing the cards to pick (e.g., "[F5, F5, F10]").
     * @return A list of cards picked from the deck.
     */
    static List<Card> pickCardsFromDeck(Deck adventureDeck, String hand) {
        List<Card> result = new ArrayList<>();
        for (String card : parseStringList(hand)) {
            char type = card.charAt(0);
            int value = Integer.parseInt(card.substring(1));
            Card c = new Card("Adv", type, value);
            int index = adventureDeck.asList().indexOf(c);
            if (index != -1) result.add(adventureDeck.asList().remove(index));
        }
        return result;
    }

    /**
     * Parses a string representing a list of items into a list of strings.
     *
     * @param listStr A string representing a list of items (e.g., "[F5, F10]").
     * @return A list of strings.
     */
    private static List<String> parseStringList(String listStr) {
        listStr = listStr.substring(1, listStr.length() - 1);
        if (listStr.isEmpty()) {
            return List.of(); // Return an empty list
        } else {
            return List.of(listStr.split(", "));
        }
    }

}
