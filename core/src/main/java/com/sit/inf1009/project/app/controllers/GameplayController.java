package com.sit.inf1009.project.app.controllers;

import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.FoodCategory;
import com.sit.inf1009.project.game.domain.GameSession;

public final class GameplayController {

    public GameSession createSession(DifficultyConfig config) {
        return new GameSession(
                config.getStartingTimer(),
                config.getHealthyScoreBonus(),
                config.getHealthyTimerBonus(),
                config.getUnhealthyTimerPenalty());
    }

    public void addFood(GameSession session, FoodCategory category, int scoreValue) {
        if (session == null || category == null) {
            return;
        }
        session.addFood(category, scoreValue);
    }

    public boolean isPlateHealthy(GameSession session) {
        return session != null && session.isPlateHealthy();
    }

    public PlateSubmitResult submitPlate(GameSession session) {
        if (session == null) {
            return new PlateSubmitResult(false, 0, 0f);
        }
        boolean healthy = session.isPlateHealthy();
        int scoreBonus = session.getHealthyScoreBonus();
        float timerDelta = healthy ? session.getHealthyTimerBonus() : -session.getUnhealthyTimerPenalty();
        session.submitPlate();
        return new PlateSubmitResult(healthy, scoreBonus, timerDelta);
    }

    public void resetPlate(GameSession session) {
        if (session != null) {
            session.resetPlate();
        }
    }

    public static final class PlateSubmitResult {
        private final boolean healthy;
        private final int healthyScoreBonus;
        private final float timerDeltaSeconds;

        public PlateSubmitResult(boolean healthy, int healthyScoreBonus, float timerDeltaSeconds) {
            this.healthy = healthy;
            this.healthyScoreBonus = healthyScoreBonus;
            this.timerDeltaSeconds = timerDeltaSeconds;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public int getHealthyScoreBonus() {
            return healthyScoreBonus;
        }

        public float getTimerDeltaSeconds() {
            return timerDeltaSeconds;
        }
    }
}
