package com.sit.inf1009.project;

import com.sit.inf1009.project.engine.interfaces.FoodCategory;

public class GameSession {

    private int vegetableCount;
    private int proteinCount;
    private int carbCount;
    private int oilCount;

    private int score;
    private float timer;
    private final int healthyScoreBonus;
    private final float healthyTimerBonus;
    private final float unhealthyTimerPenalty;

    public GameSession(float startingTimer) {
        this(startingTimer, 10, 5f, 5f);
    }

    public GameSession(float startingTimer,
                       int healthyScoreBonus,
                       float healthyTimerBonus,
                       float unhealthyTimerPenalty) {
        resetPlate();
        score = 0;
        timer = startingTimer;
        this.healthyScoreBonus = healthyScoreBonus;
        this.healthyTimerBonus = healthyTimerBonus;
        this.unhealthyTimerPenalty = unhealthyTimerPenalty;
    }

    public void addFood(FoodCategory category, int plateValue) {
        switch (category) {
            case VEGETABLE:
                vegetableCount += plateValue;
                break;
            case PROTEIN:
                proteinCount += plateValue;
                break;
            case CARBOHYDRATE:
                carbCount += plateValue;
                break;
            case OIL:
                oilCount += plateValue;
                break;
            default:
                break;
        }
    }

    public boolean isPlateHealthy() {
        return vegetableCount >= 2 && vegetableCount <= 4
                && proteinCount >= 1 && proteinCount <= 3
                && carbCount >= 1 && carbCount <= 2
                && oilCount >= 0 && oilCount <= 1;
    }

    public void submitPlate() {
        if (isPlateHealthy()) {
            score += healthyScoreBonus;
            timer += healthyTimerBonus;
            System.out.println("Healthy plate submitted! Score: " + score);
        } else {
            timer -= unhealthyTimerPenalty;
            System.out.println("Unhealthy plate submitted!");
        }

        resetPlate();
    }

    public void resetPlate() {
        vegetableCount = 0;
        proteinCount = 0;
        carbCount = 0;
        oilCount = 0;
    }

    public int getVegetableCount() {
        return vegetableCount;
    }

    public int getProteinCount() {
        return proteinCount;
    }

    public int getCarbCount() {
        return carbCount;
    }

    public int getOilCount() {
        return oilCount;
    }

    public int getScore() {
        return score;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
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
}
