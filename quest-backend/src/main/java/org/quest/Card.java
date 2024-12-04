package org.quest;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.NonNull;

public class Card implements Comparable<Card> {
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

    @JsonValue
    @Override
    public String toString() {
        if (type == 'E' && value == 0) {
            return cardType;
        }
        return String.valueOf(type) + value;
    }

    @Override
    public int compareTo(@NonNull Card card) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Card other)) {
            return false;
        }
        return type == other.type && value == other.value && cardType.equals(other.cardType);
    }
}
