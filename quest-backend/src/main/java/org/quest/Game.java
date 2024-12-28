package org.quest;

import java.io.PrintWriter;
import java.util.*;

public class Game {
    GameState gameState;
    PrintWriter output;
    Scanner input;

    public Game(Scanner input, PrintWriter output) {
        this.output = output;
        this.input = input;
        gameState = new GameState();
    }

    void startGameState() {
        GameLogic.startGame(gameState);
    }

    public void start() {
        startGameState();
        List<Player> winners;
        do {
            Player player = gameState.players[gameState.currentPlayer];
            playTurn(player);
            GameLogic.nextTurn(gameState);
            GameLogic.updateWinners(gameState);
        } while ((winners = gameState.winners).isEmpty());
        displayWinners(winners);
    }

    void playTurn(Player player) {
        print(player + "'s turn - Hand: " + player.getDeck());
        GameLogic.drawQuestCard(gameState);
        Card card = gameState.currentCard;
        print("Player " + player + " drew " + card);

        if (card.type == 'Q') {
            handleQuestCard(player);
        } else {
            handleEventCard(player);
        }

        clearHotseat(player + "'s turn is over, press Enter to continue");
    }

    void handleEventCard(Player player) {
        switch (gameState.currentCard.cardType) {
            case "Plague" -> handlePlague(player);
            case "Queen's favor" -> handleQueensFavor(player);
            case "Prosperity" -> handleProsperity(player);
        }
    }

    private void handleProsperity(Player player) {
        print("All players draw 2 Adventure cards");
        GameLogic.prosperity(gameState);
        int currentPlayer = player.id - 1;
        for (int i = 0; i < 4; ++i) {
            Player p = gameState.players[(currentPlayer + i) % 4];
            print("Player " + p + " draws 2 Adventure cards");
            trimHand(p);
            clearScreen();
        }
    }

    void handleQueensFavor(Player player) {
        print("Player " + player + " draws 2 Adventure cards");
        GameLogic.queensFavor(gameState);
        trimHand(player);
    }

    void handlePlague(Player player) {
        print("Player " + player + " losses 2 shields");
        GameLogic.plague(gameState);
    }

    void handleQuestCard(Player player) {
        GameLogic.startQuest(gameState);
        int sponsor = findSponsor(player.id - 1, gameState.currentCard);
        if (sponsor == -1) {
            print("No sponsor found for the Quest");
            GameLogic.endQuest(gameState);
            GameLogic.cleanupQuest(gameState);
            return;
        }
        GameLogic.sponsorQuest(gameState, sponsor);

        setupQuest(gameState.questState.sponsor, gameState.questState.questSize);
        clearScreen();

        playQuest(gameState.questState.questSize);
        clearScreen();

        GameLogic.endQuest(gameState);
        declareWinners(gameState.currentCard, gameState.questState.participants);
        cleanupQuest();
    }

    public void declareWinners(Card card, List<Player> winners) {
        if (!winners.isEmpty()) {
            print("Players " + winners + " win the quest and gain " + card.value + " shields");
            for (Player p : winners) {
                print("Player " + p + " now has " + p.shields + " shields");
            }
        } else {
            print("No winners for the quest");
        }
    }

    void setupQuest(Player sponsor, int stages) {
        for (int i = 1; i <= stages; ++i) {
            setupStage(sponsor, i);
        }
    }

    void setupStage(Player sponsor, int currStage) {
        print("Player " + sponsor + ": ");
        while (true) {
            print("Select a card for stage " + currStage + " or enter 'Quit' to finish stage setup");
            print(sponsor.getDeck());
            String cardIndex = input.nextLine();

            if (cardIndex.equalsIgnoreCase("Quit")) {
                Response res = GameLogic.quitStage(gameState);
                if (res == Response.NO_CARDS_IN_STAGE)
                    print("A stage cannot be empty");
                else if (res == Response.INSUFFICIENT_STAGE_VALUE)
                    print("Insufficient value for this stage");
                else {
                    print("Stage " + currStage + ": " + gameState.questState.stages.get(currStage - 1));
                    break;
                }
                continue;
            }

            try { Integer.parseInt(cardIndex);}
            catch (NumberFormatException e) {
                print("Invalid card index");
                continue;
            }
            Card cardSelected = sponsor.getCard(Integer.parseInt(cardIndex));
            Response res = GameLogic.addCardToStage(gameState, cardSelected);
            if (res == Response.INVALID_INPUT) {
                print("Invalid card index");
            } else if (res == Response.MULTIPLE_FOES) {
                print("Invalid card, only one foe card is allowed (Sole foe)");
            } else if (res == Response.REPEATED_WEAPON) {
                print("Invalid card, Weapon cards must be different (non-repeated weapon card)");
            }
        }
    }

    int findSponsor(int currentPlayer, Card card) {
        int sponsor = -1;
        for (int i = 0; i < 4 && sponsor == -1; ++i) {
            int player = (currentPlayer + i) % 4;
            sponsor = promptSponsor(player+1, card) ? player+1 : -1;
        }
        return sponsor;
    }

    boolean promptSponsor(int p, Card c) {
        print("P" + p + ": Do you want to sponsor the quest " + c + "? (y/n)");
        return input.nextLine().equals("y");
    }

    void playQuest(int stages) {
        for (int stage = 0; stage<stages; ++stage) {
            print("Stage: " + stage);
            if (!playStage())
                break;
        }
    }


    boolean playStage() {
        print("Eligible Players: " + gameState.questState.participants);
        withdrawPlayers();
        if (gameState.questState.participants.isEmpty()) return false;
        setupAttacks();
        return resolveAttacks();
    }

    void setupAttacks() {
        for (Player player : gameState.questState.participants) {
            setupAttack(player);
            clearScreen();
        }
    }

    void setupAttack(Player player) {
        print("Player " + player + " setup attack");
        while (true) {
            print(player + "'s Deck: " + player.getDeck());
            print("Select a card for the attack or enter 'Quit' to finish attack setup");
            String cardIndex = input.nextLine();

            if (cardIndex.equalsIgnoreCase("Quit")) {
                GameLogic.quitAttack(gameState, player.id);
                print(player + "'s attack: " + gameState.questState.attacks.get(player.id));
                break;
            }

            Card card = player.getCard(Integer.parseInt(cardIndex));
            Response res = GameLogic.addCardToAttack(gameState, player.id, card);
            if (res == Response.INVALID_INPUT) {
                print("Invalid card index");
            } else if (res == Response.NO_FOES_IN_ATTACK) {
                print("Invalid card, Foe cards are not allowed in attack");
            } else if (res == Response.REPEATED_WEAPON) {
                print("Invalid card, Weapon cards must be different (non-repeated weapon card)");
            }
        }
    }

    boolean resolveAttacks() {
        Response res = GameLogic.resolveAttacks(gameState);
        return res != Response.QUEST_COMPLETED;
    }

    void cleanupQuest() {
        Player sponsor = gameState.questState.sponsor;
        GameLogic.cleanupQuest(gameState);
        trimHand(sponsor);
        print("Sponsor " + sponsor + " deck: " + sponsor.getDeck());
    }

    void withdrawPlayers() {
        List<Player> tempParticipants = new ArrayList<>(gameState.questState.participants);
        for (Player player : tempParticipants) {
            print(player + ": Do you want to withdraw from the quest? (y/n)");
            String answer = input.nextLine();
            Response res = GameLogic.participateInQuest(gameState, player.id, answer.equals("n"));
            if (answer.equals("n")) print(player + " draws 1 Adventure card");
            if (res == Response.TRIM_REQUIRED) {
                trimHand(player);
            }
            clearScreen();
        }
    }

    Card selectCard(Player player) {
        Card card = null;
        while (card == null) {
            print(player.getDeck());
            print("Select a card: ");
            String cardIndex = input.nextLine();
            card = player.getCard(Integer.parseInt(cardIndex));
            if (card == null) {
                print("Invalid card index");
            }
        }
        return card;
    }

    void trimHand(Player player) {
        int cardsToRemove = GameLogic.computeTrim(player);
        if (cardsToRemove == 0) return;
        print("Player " + player + " has more than 12 cards, select " + cardsToRemove + " cards to discard");
        print("Player" + player + "'s hand: " + player.getDeck());
        for (int i = 0; i < cardsToRemove; ++i) {
            GameLogic.trimCard(gameState, player.id, selectCard(player));
            print(player + "'s trimmed hand: " + player.getDeck());
        }
    }

    void displayWinners(List<Player> winners) {
        String[] winnersNames = new String[winners.size()];
        for (int i = 0; i < winners.size(); ++i) {
            winnersNames[i] = winners.get(i).toString();
        }
        print("Winners: " + String.join(", ", winnersNames));
    }

    void clearHotseat(String message) {
        print(message);
        input.nextLine();
        clearScreen();
    }

    void clearScreen() {
        for (int i = 0; i < 20; ++i) {
            output.println();
        }
        output.flush();
    }

    void print(Object message) {
        output.println(message);
        output.flush();
    }
}
