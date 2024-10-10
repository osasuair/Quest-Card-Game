package org.example;

class Card implements Comparable<Card> {
    final char type;
    final String cardType;
    final int value;

    // For foe, weapon, and quest cards
    Card(String cardType, char type, int value) {
        this.cardType = cardType;
        this.type = type;
        this.value = value;
    }

    // For event cards
    Card(String cardType) {
        this.cardType = cardType;
        this.type = 'E';
        this.value = 0;
    }

    @Override
    public String toString() {
        if (type == 'E' && value == 0) {
            return cardType;
        }
        return String.valueOf(type) + value;
    }

    @Override
    public int compareTo(Card card) {
        if (cardType.equals("Adv") && card.cardType.equals("Adv")) {
            if (type == 'F' && card.type != 'F')
                return -1;
            if (type != 'F' && card.type == 'F')
                return 1;
            if (type == 'H' && card.type == 'S')
                return 1;
            if (type == 'S' && card.type == 'H')
                return -1;
        }
        return value - card.value;
    }
}
