package org.quest;

import java.util.ArrayList;
import java.util.List;

public class TestGameStates {
    public static GameState getTestState(String testNum) {
        return switch (testNum) {
            case "winner2_game_2winner_quest" -> winner2_game_2winner_quest_start_state();
            case "winner1_game_with_events" -> winner1_game_with_events_start_state();
            default -> null;
        };
    }

    private static GameState winner1_game_with_events_start_state() {
        GameState gameState = new GameState();
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

        return gameState;
    }

    public static GameState winner2_game_2winner_quest_start_state() {
        GameState gameState = new GameState();
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

        return gameState;
    }

    /**
     * Helper function to pick cards from the adventure deck based on a string representation.
     *
     * @param adventureDeck The adventure deck to pick cards from.
     * @param hand          A string representing the cards to pick (e.g., "[F5, F5, F10]").
     * @return A list of cards picked from the deck.
     */
    private static List<Card> pickCardsFromDeck(Deck adventureDeck, String hand) {
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