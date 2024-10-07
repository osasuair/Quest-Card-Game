package org.example;

class Card {
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

    public String toString() {
        if (type == 'E' && value == 0) {
            return cardType;
        }
        return String.valueOf(type) + value;
    }
}
