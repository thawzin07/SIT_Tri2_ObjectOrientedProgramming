package com.sit.inf1009.project.game.services;

import com.sit.inf1009.project.app.DifficultyPreset;
import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.GameSession;
import com.sit.inf1009.project.game.domain.TutorialState;

public class TutorialCoordinator {

    private static final int TARGET_CORRECT_PLATES = 3;

    private final TutorialState state;
    private final DifficultyConfig config;

    public TutorialCoordinator() {
        this.state = new TutorialState();
        this.config = DifficultyPreset.TUTORIAL.toConfig();
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
        if (gameSession == null) {
            return new TutorialSubmitResult(false, false, "No active game session");
        }

        GameSession.PlateSubmitResult result = gameSession.submitPlate();

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

        boolean finished = state.registerHealthyPlate(TARGET_CORRECT_PLATES);

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
