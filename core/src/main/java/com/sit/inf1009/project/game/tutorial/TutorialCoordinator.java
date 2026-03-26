package com.sit.inf1009.project.game.tutorial;

import com.sit.inf1009.project.app.DifficultyPreset;
import com.sit.inf1009.project.app.controllers.GameplayController;
import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.GameSession;

public class TutorialCoordinator {

    private static final int TARGET_CORRECT_PLATES = 3;

    private final GameplayController gameplayController;
    private final TutorialState state;
    private final DifficultyConfig config;

    public TutorialCoordinator(GameplayController gameplayController) {
        this.gameplayController = gameplayController;
        this.state = new TutorialState();
        this.config = createTutorialConfig();
    }


	private DifficultyConfig createTutorialConfig() {
        DifficultyPreset preset = DifficultyPreset.TUTORIAL;
        return new DifficultyConfig(
                preset.getStartingTimer(),
                preset.getNpcCount(),
                preset.getNpcSpeed(),
                preset.getHealthyScoreBonus(),
                preset.getHealthyTimerBonus(),
                preset.getUnhealthyTimerPenalty(),
                preset.getFoodEntityCount());
    }

    public void start() {
        state.start();
    }

    public void stop() {
        state.stop();
    }

    public boolean isActive() {
        return state.isActive();
    }

    public boolean isFinished() {
        return state.isFinished();
    }

    public DifficultyConfig getConfig() {
        return config;
    }

    public DifficultyPreset getPreset() {
        return DifficultyPreset.TUTORIAL;
    }

    public TutorialState getState() {
        return state;
    }

    public TutorialSubmitResult submit(GameSession gameSession) {
        GameplayController.PlateSubmitResult result = gameplayController.submitPlate(gameSession);

        if (!state.isActive()) {
            return new TutorialSubmitResult(result.isHealthy(), false, null);
        }

        if (!result.isHealthy()) {
            return new TutorialSubmitResult(
                    false,
                    false,
                    "Not quite right. Need 2-4 Veg, 1-3 Protein, 1-2 Carbs, 0-1 Oil."
            );
        }

        boolean finished = state.registerHealthyPlate();

        if (finished) {
            return new TutorialSubmitResult(true, true, "Tutorial complete");
        }

        return new TutorialSubmitResult(
                true,
                false,
                "Correct plate! " + state.getCorrectPlateCount() + "/" + TARGET_CORRECT_PLATES + " completed."
        );
    }

    public record TutorialSubmitResult(boolean healthy, boolean finished, String message) {
    }
}