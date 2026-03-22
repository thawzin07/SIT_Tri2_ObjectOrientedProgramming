package com.sit.inf1009.project.app;

public enum DifficultyPreset {
    EASY("Easy", 75f, 6, 95f, 8, 6f, 3f, 10),
    NORMAL("Normal", 60f, 8, 120f, 10, 5f, 5f, 15),
    HARD("Hard", 45f, 10, 150f, 12, 4f, 6f, 20);

    private final String label;
    private final float startingTimer;
    private final int npcCount;
    private final float npcSpeed;
    private final int healthyScoreBonus;
    private final float healthyTimerBonus;
    private final float unhealthyTimerPenalty;
    private final int foodEntityCount;

    DifficultyPreset(String label,
                     float startingTimer,
                     int npcCount,
                     float npcSpeed,
                     int healthyScoreBonus,
                     float healthyTimerBonus,
                     float unhealthyTimerPenalty,
                     int foodEntityCount) {
        this.label = label;
        this.startingTimer = startingTimer;
        this.npcCount = npcCount;
        this.npcSpeed = npcSpeed;
        this.healthyScoreBonus = healthyScoreBonus;
        this.healthyTimerBonus = healthyTimerBonus;
        this.unhealthyTimerPenalty = unhealthyTimerPenalty;
        this.foodEntityCount = foodEntityCount;
    }

    public String getLabel() {
        return label;
    }

    public float getStartingTimer() {
        return startingTimer;
    }

    public int getNpcCount() {
        return npcCount;
    }

    public float getNpcSpeed() {
        return npcSpeed;
    }

    public int getHealthyScoreBonus() {
        return healthyScoreBonus;
    }

    public float getHealthyTimerBonus() {
        return healthyTimerBonus;
    }

    public float getUnhealthyTimerPenalty() {
        return unhealthyTimerPenalty;
    }

    public int getFoodEntityCount() {
        return foodEntityCount;
    }
}
