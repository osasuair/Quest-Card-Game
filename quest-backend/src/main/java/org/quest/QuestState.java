package org.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestState {
    Player sponsor;
    int questSize;
    int currentStage;
    int currentAttackStage;
    boolean questCompleted;
    List<Player> participants;
    List<List<Card>> stages;
    Map<Integer, List<Card>> attacks;

    QuestState() {
        stages = new ArrayList<>();
        participants = new ArrayList<>();
        questCompleted = false;
        attacks = new HashMap<>();
        currentStage = 1;
        currentAttackStage = 1;
    }

    public Map<Integer, List<Card>> getAttacks() {
        return attacks;
    }

    public List<List<Card>> getStages() {
        return stages;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public boolean isQuestCompleted() {
        return questCompleted;
    }

    public int getCurrentAttackStage() {
        return currentAttackStage;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public int getQuestSize() {
        return questSize;
    }

    public Player getSponsor() {
        return sponsor;
    }
}
