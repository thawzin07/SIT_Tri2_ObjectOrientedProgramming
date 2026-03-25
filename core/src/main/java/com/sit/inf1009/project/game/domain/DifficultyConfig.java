package com.sit.inf1009.project.game.domain;

public class DifficultyConfig {
    private final float startingTimer;
    private final int npcCount;
    private final float npcSpeed;
    private final int healthyScoreBonus;
    private final float healthyTimerBonus;
    private final float unhealthyTimerPenalty;
    private final int foodEntityCount;

    public DifficultyConfig(float startingTimer,
                            int npcCount,
                            float npcSpeed,
                            int healthyScoreBonus,
                            float healthyTimerBonus,
                            float unhealthyTimerPenalty,
                            int foodEntityCount) {
        this.startingTimer = startingTimer;
        this.npcCount = npcCount;
        this.npcSpeed = npcSpeed;
        this.healthyScoreBonus = healthyScoreBonus;
        this.healthyTimerBonus = healthyTimerBonus;
        this.unhealthyTimerPenalty = unhealthyTimerPenalty;
        this.foodEntityCount = foodEntityCount;
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
