package org.example;

import java.io.PrintWriter;
import java.util.*;

public class Game {
    final Player[] players;
    int currentPlayer;
    Deck adventureDeck, questDeck;
    PrintWriter output;
    Scanner input;

    public Game(int playersAmount, Scanner input, PrintWriter output) {
        this.output = output;
        this.input = input;
        currentPlayer = 0;
        adventureDeck = new Deck();
        questDeck = new Deck();
        players = new Player[playersAmount];
        for (int i = 0; i < playersAmount; ++i) {
            players[i] = new Player(i + 1);
        }
    }

    void initPlayers() {
        for (Player player : players) {
            player.pickCards(adventureDeck.draw(12));
        }
    }

    public void start() {
        adventureDeck.initAdventureDeck();
        questDeck.initQuestDeck();
        adventureDeck.shuffle();
        questDeck.shuffle();

        initPlayers();
        List<Player> winners;
        do {
            Player player = players[currentPlayer];
            playTurn(player);
            currentPlayer = (currentPlayer + 1) % players.length;
        } while ((winners = checkWinners()).isEmpty());
        displayWinners(winners);
    }

    void playTurn(Player player) {
        print(player + "'s turn - Hand: " + player.getDeck());

        Card card = questDeck.draw();
        print("Player " + player + " drew " + card);

        // Handle event card
        if (card.type != 'Q') {
            handleEventCard(player, card);
        }

        print(player + "'s turn is over, press Enter to continue");
        input.nextLine();
        clearHotseat();
    }

    void handleEventCard(Player player, Card card) {
        switch (card.cardType) {
            case "Plague" -> handlePlague(player);
            case "Queen’s favor" -> handleQueensFavor(player);
            case "Prosperity" -> handleProsperity(player);
        }
    }

    private void handleProsperity(Player player) {
        print("All players draw 2 Adventure cards");
        int currentPlayer = player.id - 1;
        for (int i = 0; i < players.length; ++i) {
            Player p = players[(currentPlayer + i) % players.length];
            print("Player " + p + " draws 2 Adventure cards");
            p.pickCards(adventureDeck.draw(2));
            trimHand(p);
        }
    }

    void handleQueensFavor(Player player) {
        print("Player " + player + " draws 2 Adventure cards");
        player.pickCards(adventureDeck.draw(2));
        trimHand(player);
    }

    void handlePlague(Player player) {
        print("Player " + player + " losses 2 shields");
        player.shields = (player.shields < 2) ? 0 : player.shields - 2;
    }

    void handleQuestCard(Player player, Card card) {
        Player sponsor = findSponsor(player.id - 1, card);
        if (sponsor == null) {
            print("No sponsor found for the Quest");
            return;
        }
        List<List<Card>> stages = setupQuest(sponsor, card);
        List<Player> winners = playQuest(sponsor, stages);

        if (!winners.isEmpty()) {
            print("Players " + winners + " win the quest and gain " + card.value + " shields");
            // Update winners
            for (Player p : winners) {
                p.shields += card.value;
                print("Player " + p + " now has " + p.shields + " shields");
            }
        } else {
            print("No winners for the quest");
        }
    }

    List<List<Card>> setupQuest(Player sponsor, Card card) {
        List<List<Card>> stages = new ArrayList<>();
        for (int i = 1; i <= card.value; ++i) {
            List<Card> previousStage = i > 1 ? stages.get(i - 2) : new ArrayList<>();
            int previousStageValue = previousStage.stream().mapToInt(card1 -> card1.value).sum();
            stages.add(setupStage(sponsor, i, previousStageValue));
        }
        return stages;
    }

    List<Card> setupStage(Player sponsor, int currStage, int previousStageValue) {
        List<Card> stage = new ArrayList<>();
        print("Player " + sponsor + ": ");
        while (true) {
            print("Select a card for stage " + currStage + " or enter 'Quit' to finish stage setup");
            print(sponsor.getDeck());
            String cardIndex = input.nextLine();

            if (cardIndex.equalsIgnoreCase("Quit")) {
                if (stage.isEmpty())
                    print("A stage cannot be empty");
                else if (!largerThanLastStage(stage, previousStageValue))
                    print("Insufficient value for this stage");
                else {
                    print("Stage " + currStage + ": " + stage);
                    break;
                }
                continue;
            }

            Card cardSelected = sponsor.getCard(Integer.parseInt(cardIndex));
            if (cardSelected == null) {
                print("Invalid card index");
            } else if (multipleFoes(cardSelected, stage)) {
                print("Invalid card, only one foe card is allowed (Sole foe)");
            } else if (repeatedWeapon(cardSelected, stage)) {
                print("Invalid card, Weapon cards must be different (non-repeated weapon card)");
            } else {
                stage.add(sponsor.playCard(cardSelected));
            }
        }
        return stage;
    }

    Player findSponsor(int currentPlayer, Card card) {
        Player sponsor = null;
        for (int i = 0; i < players.length && sponsor == null; ++i) {
            Player p = players[(currentPlayer + i) % players.length];
            sponsor = promptSponsor(p, card) ? p : null;
        }
        return sponsor;
    }

    boolean promptSponsor(Player p, Card c) {
        print(p + ": Do you want to sponsor the quest " + c + "? (y/n)");
        return input.nextLine().equals("y");
    }

    List<Player> playQuest(Player sponsor, List<List<Card>> stages) {
        // Determine Eligible Players
        List<Player> stagePlayers = new LinkedList<>(Arrays.asList(players));
        stagePlayers.remove(sponsor);

        for (List<Card> stage : stages) {
            print("Stage: " + stage);
            if (!playStage(stagePlayers, stage))
                break;
        }
        return stagePlayers;
    }


    boolean playStage(List<Player> stagePlayers, List<Card> stage) {
        print("Eligible Players: " + stagePlayers);
        removeWithdrawnPlayers(stagePlayers);
        if (stagePlayers.isEmpty()) return false;

        for (Player player : stagePlayers) {
            print(player + " draws 1 Adventure card");
            player.pickCards(adventureDeck.draw(1));
            trimHand(player);
        }

        Map<Player, List<Card>> attacks = setupAttacks(stagePlayers);
        adventureDeck.discard(attacks.values().stream().flatMap(List::stream).toList());  // Discard all attack cards
        return resolveAttacks(stagePlayers, attacks, stage.stream().mapToInt(card -> card.value).sum());
    }

    Map<Player, List<Card>> setupAttacks(List<Player> stagePlayers) {
        Map<Player, List<Card>> attacks = new HashMap<>();
        for (Player player : stagePlayers) {
            List<Card> attack = setupAttack(player);
            attacks.put(player, attack);
        }
        return attacks;
    }


    List<Card> setupAttack(Player player) {
        List<Card> attack = new ArrayList<>();
        print("Player " + player + " setup attack");
        while (true) {
            print(player + "'s Deck: " + player.getDeck());
            print("Select a card for the attack or enter 'Quit' to finish attack setup");
            String cardIndex = input.nextLine();
            if (cardIndex.equalsIgnoreCase("Quit")) {
                print(player + "'s attack: " + attack);
                break;
            }

            Card card = player.getCard(Integer.parseInt(cardIndex));
            if (card == null) {
                print("Invalid card index");
            } else if (card.type == 'F') {
                print("Invalid card, Foe cards are not allowed in attack");
            } else if (repeatedWeapon(card, attack)) {
                print("Invalid card, Weapon cards must be different (non-repeated weapon card)");
            } else {
                attack.add(player.playCard(card));
            }
        }
        return attack;
    }

    boolean resolveAttacks(List<Player> stagePlayers, Map<Player, List<Card>> attacks, int stageValue) {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player player : stagePlayers) {
            if (resolveAttack(attacks.get(player), stageValue))
                print("Player " + player + " passed the stage");
            else {
                playersToRemove.add(player);
                print("Player " + player + " failed the quest");
            }
        }
        stagePlayers.removeAll(playersToRemove);
        return !stagePlayers.isEmpty();
    }

    boolean resolveAttack(List<Card> attack, int stageValue) {
        int attackPower = attack.stream().mapToInt(card -> card.value).sum();
        return attackPower >= stageValue;
    }

    void cleanupQuest(Player sponsor, List<List<Card>> stageSetup) {
        for (List<Card> stageDiscard : stageSetup) {
            adventureDeck.discard(stageDiscard);
        }
        int cardsToPickup = stageSetup.stream().mapToInt(List::size).sum() + stageSetup.size();
        sponsor.pickCards(adventureDeck.draw(cardsToPickup));
        trimHand(sponsor);
        print("Sponsor " + sponsor + " deck: " + sponsor.getDeck());
    }

    private boolean multipleFoes(Card cardSelected, List<Card> cards) {
        return cardSelected.type == 'F' && cards.stream().anyMatch(card -> card.type == 'F');
    }

    private boolean repeatedWeapon(Card cardSelected, List<Card> cards) {
        return cards.stream().anyMatch(card -> card.type == cardSelected.type);
    }

    private boolean largerThanLastStage(List<Card> currStage, int previousStageValue) {
        int currentStageValue = currStage.stream().mapToInt(card -> card.value).sum();
        return currentStageValue > previousStageValue;
    }

    void removeWithdrawnPlayers(List<Player> stagePlayers) {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player player : stagePlayers) {
            print(player + ": Do you want to withdraw from the quest? (y/n)");
            String answer = input.nextLine();
            if (answer.equals("y")) {
                playersToRemove.add(player);
            }
        }
        stagePlayers.removeAll(playersToRemove);
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
        int cardsToRemove = computeTrim(player);
        if (cardsToRemove == 0) return;
        print("Player " + player + " has more than 12 cards, select " + cardsToRemove + " cards to discard");
        for (int i = 0; i < cardsToRemove; ++i) {
            adventureDeck.discard(player.playCard(selectCard(player)));
            print(player + "'s trimmed hand: " + player.getDeck());
        }
    }

    int computeTrim(Player player) {
        return player.hand.size() > 12 ? player.hand.size() - 12 : 0;
    }

    List<Player> checkWinners() {
        ArrayList<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.shields >= 7) {
                winners.add(player);
            }
        }
        return winners;
    }

    void displayWinners(List<Player> winners) {
        String[] winnersNames = new String[winners.size()];
        for (int i = 0; i < winners.size(); ++i) {
            winnersNames[i] = winners.get(i).toString();
        }
        print("Winners: " + String.join(", ", winnersNames));
    }

    void clearHotseat() {
        for (int i = 0; i < 10; ++i) {
            output.println();
        }
        output.flush();
    }

    void print(Object message) {
        output.println(message);
        output.flush();
    }
}
