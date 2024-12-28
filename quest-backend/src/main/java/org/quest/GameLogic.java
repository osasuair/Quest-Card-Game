package org.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class implements the game logic for a card-based quest game.
 * It manages the game state, processes player actions, and enforces the rules of the game.
 */
public class GameLogic {

    /**
     * Discards a collection of lists of cards from the adventure deck.
     *
     * @param adventureDeck The adventure deck to discard the cards from.
     * @param cards         A collection of lists of cards to be discarded.
     */
    static void discardListCards(Deck adventureDeck, Collection<List<Card>> cards) {
        adventureDeck.discard(cards.stream().flatMap(List::stream).toList());  // Discard all cards
    }

    /**
     * Resolves the attacks for a given stage of the quest.
     *
     * @param stagePlayers The list of players participating in the stage.
     * @param attacks      A map of players to their attack cards.
     * @param stageValue   The total value of the stage.
     * @return true if the quest is completed, false otherwise.
     */
    static boolean resolveAttacks(List<Player> stagePlayers, Map<Integer, List<Card>> attacks, int stageValue) {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player player : stagePlayers) {
            if (!resolveAttack(attacks.get(player.id), stageValue))
                playersToRemove.add(player);
        }
        stagePlayers.removeAll(playersToRemove);
        return !stagePlayers.isEmpty();
    }

    /**
     * Resolves a single player's attack against a stage.
     *
     * @param attack     The list of cards used in the attack.
     * @param stageValue The total value of the stage.
     * @return true if the attack is successful, false otherwise.
     */
    static boolean resolveAttack(List<Card> attack, int stageValue) {
        int attackPower = getStageValue(attack);
        return attackPower >= stageValue;
    }

    /**
     * Checks if a stage already contains a weapon card of the same type as the card being added.
     *
     * @param cardSelected The card to be added to the stage.
     * @param cards        The list of cards already in the stage.
     * @return true if the stage already contains a weapon card of the same type, false otherwise.
     */
    static boolean repeatedWeapon(Card cardSelected, List<Card> cards) {
        return cards.stream().anyMatch(card -> card.type == cardSelected.type);
    }

    /**
     * Calculates the total value of a stage.
     *
     * @param stage The list of cards in the stage.
     * @return The total value of the stage.
     */
    static int getStageValue(List<Card> stage) {
        return stage.stream().mapToInt(card -> card.value).sum();
    }

    /**
     * Calculates the number of cards a player needs to trim from their hand.
     *
     * @param player The player whose hand needs to be trimmed.
     * @return The number of cards to trim.
     */
    static int computeTrim(Player player) {
        return player.getDeck().size() > 12 ? player.getDeck().size() - 12 : 0;
    }

    /**
     * Returns the current quest state.
     *
     * @return The current QuestState object, or null if no quest is in progress.
     */
    public QuestState getQuestState(GameState gameState) {
        return gameState.questState;
    }

    /**
     * Processes a player action and updates the game state accordingly.
     *
     * @param action The ActionType object representing the action to be performed.
     * @param arg    Optional arguments required for the action.
     * @return A Response enum value indicating the result of the action.
     */
    public static Response processAction(GameState gameState, ActionType action, Object... arg) {
        return switch (action) {
            case START_GAME -> startGame(gameState);
            case DRAW_QUEST_CARD -> drawQuestCard(gameState);
            case PLAGUE -> plague(gameState);
            case QUEENS_FAVOR -> queensFavor(gameState);
            case PROSPERITY -> prosperity(gameState);
            case TRIM_CARD -> trimCard(gameState, arg[0], arg[1]);
            case START_QUEST -> startQuest(gameState);
            case SPONSOR_QUEST -> sponsorQuest(gameState, arg[0]);
            case ADD_CARD_TO_STAGE -> addCardToStage(gameState, arg[0]);
            case QUIT_STAGE_SETUP -> quitStage(gameState);
            case PARTICIPATE_IN_QUEST -> participateInQuest(gameState, arg[0], arg[1]);
            case ADD_CARD_TO_ATTACK -> addCardToAttack(gameState, arg[0], arg[1]);
            case QUIT_ATTACK_SETUP -> quitAttack(gameState, arg[0]);
            case RESOLVE_ATTACKS -> resolveAttacks(gameState);
            case END_QUEST -> endQuest(gameState);
            case CLEANUP_QUEST -> cleanupQuest(gameState);
            case NEXT_TURN -> nextTurn(gameState);
            default -> Response.INVALID_REQUEST;
        };
    }

    /**
     * Advances the game to the next player's turn.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response nextTurn(GameState gameState) {
        if (gameState.currentCard == null) return Response.NO_CARD_DRAWN;
        for (Player player : gameState.players)
            if (computeTrim(player) > 0) return Response.TRIM_REQUIRED;

        gameState.currentPlayer = (gameState.currentPlayer + 1) % gameState.players.length;
        gameState.questDeck.discard(gameState.currentCard);
        gameState.currentCard = null;
        return Response.SUCCESS;
    }

    /**
     * Performs the cleanup phase after a quest is completed. This includes discarding cards,
     * awarding the sponsor with cards, and checking if the sponsor needs to trim their hand.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response cleanupQuest(GameState gameState) {
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (!gameState.questState.questCompleted) return Response.QUEST_NOT_COMPLETED;

        Response response = Response.SUCCESS;
        if (gameState.questState.sponsor != null) {
            discardListCards(gameState.adventureDeck, gameState.questState.stages);
            int cardsToPickup = gameState.questState.stages.stream()
                                        .mapToInt(List::size)
                                        .sum() + gameState.questState.questSize;
            gameState.questState.sponsor.pickCards(gameState.adventureDeck.draw(cardsToPickup));
            response = (gameState.questState.sponsor.getDeck().size() > 12) ? Response.TRIM_REQUIRED : Response.SUCCESS;
        }
        gameState.questState = null;
        return response;
    }

    /**
     * Ends the current quest and determines the winners.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response endQuest(GameState gameState) {
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.sponsor != null) {
            if (gameState.questState.questCompleted && gameState.questState.participants.isEmpty())
                return Response.NO_WINNERS;
            if (gameState.questState.currentAttackStage <= gameState.questState.stages.size())
                return Response.QUEST_NOT_COMPLETED;

            for (Player player : gameState.questState.participants) {
                player.shields += gameState.questState.questSize;
            }
            updateWinners(gameState);
            gameState.questState.questCompleted = true;
            return Response.WINNERS;
        }
        gameState.questState.questCompleted = true;
        return Response.NO_WINNERS;
    }

    static void updateWinners(GameState gs) {
        gs.winners.clear();
        for (Player player : gs.players) {
            if (player.shields >= 7) {
                gs.winners.add(player);
            }
        }
    }

    /**
     * Resolves the attacks for the current stage of the quest.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response resolveAttacks(GameState gameState) {
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.questCompleted) return Response.QUEST_ALREADY_COMPLETED;
        if (gameState.questState.currentAttackStage > gameState.questState.stages.size())
            return Response.NO_CARDS_IN_STAGE;

        discardListCards(gameState.adventureDeck, gameState.questState.attacks.values());
        List<Card> stage = gameState.questState.stages.get(gameState.questState.currentAttackStage - 1);
        boolean questCompleted = !resolveAttacks(gameState.questState.participants,
                                                 gameState.questState.attacks,
                                                 getStageValue(stage));
        if (questCompleted) {
            gameState.questState.questCompleted = true;
            return Response.QUEST_COMPLETED;
        }
        gameState.questState.attacks.clear();
        gameState.questState.currentAttackStage++;
        return Response.SUCCESS;
    }

    /**
     * Handles a player quitting the attack setup phase.
     *
     * @param playerIDArg The ID of the player quitting the attack setup.
     * @return A Response enum value indicating the result of the action.
     */
    static Response quitAttack(GameState gameState, Object playerIDArg) {
        if (!(playerIDArg instanceof Integer playerID)) return Response.INVALID_INPUT;
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.questCompleted) return Response.QUEST_ALREADY_COMPLETED;

        if (!gameState.questState.attacks.containsKey(playerID))
            gameState.questState.attacks.put(playerID, List.of());
        return Response.SUCCESS;
    }

    /**
     * Handles a player adding a card to their attack.
     *
     * @param playerIDArg The ID of the player adding the card.
     * @param cardArg     The card object to be added.
     * @return A Response enum value indicating the result of the action.
     */
    static Response addCardToAttack(GameState gameState, Object playerIDArg, Object cardArg) {
        if (!(cardArg instanceof Card card) || !(playerIDArg instanceof Integer playerId))
            return Response.INVALID_INPUT;

        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.questCompleted) return Response.QUEST_ALREADY_COMPLETED;

        Player player = gameState.players[playerId - 1];
        if (!gameState.questState.attacks.containsKey(playerId))
            gameState.questState.attacks.put(playerId, new ArrayList<>());

        List<Card> attack = gameState.questState.attacks.get(playerId);
        if (!player.hasCard(card)) return Response.NOT_IN_HAND;
        if (card.type == 'F') return Response.NO_FOES_IN_ATTACK;
        if (repeatedWeapon(card, attack)) return Response.REPEATED_WEAPON;

        attack.add(player.playCard(card));
        return Response.SUCCESS;
    }

    /**
     * Handles a player deciding whether to participate in the quest or not.
     *
     * @param playerIDArg    The ID of the player.
     * @param participateArg A boolean value indicating whether the player wants to participate.
     * @return A Response enum value indicating the result of the action.
     */
    static Response participateInQuest(GameState gameState, Object playerIDArg, Object participateArg) {
        if (!(participateArg instanceof Boolean participate) || !(playerIDArg instanceof Integer playerID))
            return Response.INVALID_INPUT;
        Player player = gameState.players[playerID - 1];
        if (!gameState.questState.participants.contains(player)) return Response.PLAYER_NOT_IN_QUEST;

        if (!participate) {
            gameState.questState.participants.remove(player);
            if (gameState.questState.participants.isEmpty())
                gameState.questState.questCompleted = true;
            return Response.SUCCESS;
        }

        List<Card> drawn = gameState.adventureDeck.draw(1);
        player.pickCards(drawn);
        if (computeTrim(player) > 0)
            return Response.TRIM_REQUIRED;
        return Response.SUCCESS;
    }

    /**
     * Handles the sponsor quitting the stage setup phase.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response quitStage(GameState gameState) {
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.questCompleted) return Response.QUEST_ALREADY_COMPLETED;
        if (gameState.questState.stages.size() < gameState.questState.currentStage) return Response.NO_CARDS_IN_STAGE;
        if (gameState.questState.stages.get(gameState.questState.currentStage - 1).isEmpty())
            return Response.NO_CARDS_IN_STAGE;

        int previousStageValue = gameState.questState.currentStage == 1
                ? 0
                : getStageValue(gameState.questState.stages.get(gameState.questState.currentStage - 2));
        int currentStageValue = getStageValue(gameState.questState.stages.get(gameState.questState.currentStage - 1));
        if (currentStageValue <= previousStageValue) return Response.INSUFFICIENT_STAGE_VALUE;

        return (++gameState.questState.currentStage) > gameState.questState.questSize ?
                Response.ALL_STAGES_SET :
                Response.SUCCESS;
    }

    /**
     * Handles the sponsor adding a card to the current stage of the quest.
     *
     * @param arg The card object to be added to the stage.
     * @return A Response enum value indicating the result of the action.
     */
    static Response addCardToStage(GameState gameState, Object arg) {
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;
        if (gameState.questState.currentStage > gameState.questState.questSize) return Response.ALL_STAGES_SET;
        if (gameState.questState.stages.size() < gameState.questState.currentStage)
            gameState.questState.stages.add(new ArrayList<>());
        if (!(arg instanceof Card card)) return Response.INVALID_INPUT;


        List<Card> stage = gameState.questState.stages.get(gameState.questState.currentStage - 1);
        if (!gameState.questState.sponsor.hasCard(card)) return Response.NOT_IN_HAND;
        if (multipleFoes(card, stage)) return Response.MULTIPLE_FOES;
        if (repeatedWeapon(card, stage)) return Response.REPEATED_WEAPON;

        stage.add(gameState.questState.sponsor.playCard(card));
        return Response.SUCCESS;
    }

    /**
     * Checks if a stage already contains a foe card.
     *
     * @param cardSelected The card to be added to the stage.
     * @param cards        The list of cards already in the stage.
     * @return true if the stage already contains a foe card, false otherwise.
     */
    private static boolean multipleFoes(Card cardSelected, List<Card> cards) {
        return cardSelected.type == 'F' && cards.stream().anyMatch(card -> card.type == 'F');
    }

    /**
     * Starts a new quest with the given quest card.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response startQuest(GameState gameState) {
        Card questCard = gameState.currentCard;
        if (questCard == null) return Response.NO_QUEST_STARTED;
        if (questCard.type != 'Q') return Response.INVALID_REQUEST;

        gameState.questState = new QuestState();
        gameState.questState.questSize = questCard.value;
        gameState.questState.participants.addAll(List.of(gameState.players));
        return Response.SUCCESS;
    }

    /**
     * Sets the sponsor for the current quest.
     *
     * @param sponsorIdArg The ID of the player sponsoring the quest.
     * @return A Response enum value indicating the result of the action.
     */
    static Response sponsorQuest(GameState gameState, Object sponsorIdArg) {
        if (!(sponsorIdArg instanceof Integer sponsorID)) return Response.INVALID_INPUT;
        if (gameState.questState == null) return Response.NO_QUEST_STARTED;

        if (sponsorID <= 0) {
            gameState.questState.questCompleted = true;
            return Response.QUEST_COMPLETED;
        }

        Player sponsor = gameState.players[sponsorID - 1];
        gameState.questState.sponsor = sponsor;
        gameState.questState.participants.remove(sponsor);
        return Response.SUCCESS;
    }

    /**
     * Handles a player trimming a Card from their hand
     *
     * @param playerArg The ID of the player trimming their hand.
     * @param cardArg   The Card to be trimmed.
     * @return A Response enum value indicating the result of the action.
     */
    static Response trimCard(GameState gameState, Object playerArg, Object cardArg) {
        if (!(playerArg instanceof Integer playerID) || !(cardArg instanceof Card card)) return Response.INVALID_INPUT;

        Player player = gameState.players[playerID - 1];
        int trimSize = computeTrim(player);
        if (trimSize == 0) return Response.NO_TRIM_REQUIRED;
        gameState.adventureDeck.discard(player.playCard(card));
        return trimSize == 1 ? Response.SUCCESS : Response.TRIM_REQUIRED;
    }

    /**
     * Executes the Queen's Favor event, where the current player draws two cards.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response queensFavor(GameState gameState) {
        Player player = gameState.players[gameState.currentPlayer];
        player.pickCards(gameState.adventureDeck.draw(2));
        return player.getDeck().size() > 12 ? Response.TRIM_REQUIRED : Response.SUCCESS;
    }

    /**
     * Executes the Prosperity event, where all players draw two cards.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response prosperity(GameState gameState) {
        boolean trimRequired = false;
        for (Player player : gameState.players) {
            player.pickCards(gameState.adventureDeck.draw(2));
            if (player.getDeck().size() > 12)
                trimRequired = true;
        }
        return trimRequired ? Response.TRIM_REQUIRED : Response.SUCCESS;
    }

    /**
     * Executes the Plague event, where the current player loses two shields.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response plague(GameState gameState) {
        Player player = gameState.players[gameState.currentPlayer];
        player.shields = Math.max(player.shields - 2, 0);
        return Response.SUCCESS;
    }

    /**
     * Draws a quest card from the quest deck.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response drawQuestCard(GameState gameState) {
        if (gameState.currentCard != null) return Response.CARD_ALREADY_DRAWN;
        gameState.currentCard = gameState.questDeck.draw();
        return Response.SUCCESS;
    }

    /**
     * Starts a new game and sets up the initial game state.
     *
     * @return A Response enum value indicating the result of the action.
     */
    static Response startGame(GameState gameState) {
        gameState.clear();
        setupGame(gameState);
        return Response.SUCCESS;
    }

    /**
     * Sets up the game by initializing and shuffling the decks, and dealing cards to the players.
     */
    private static void setupGame(GameState gs) {
        gs.adventureDeck.initAdventureDeck();
        gs.questDeck.initQuestDeck();
        gs.adventureDeck.shuffle();
        gs.questDeck.shuffle();
        initPlayers(gs);
        gs.currentPlayer = 0;
    }

    static void initPlayers(GameState gs) {
        for (Player player : gs.players) {
            player.pickCards(gs.adventureDeck.draw(12));
        }
    }

    public static boolean setupGameState(GameState gs, String test) {
        return TestGameStates.getTestState(gs, test);
    }
}
