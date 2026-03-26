package com.sit.inf1009.project.game.tutorial;

public class TutorialState {

    private boolean active;
    private boolean finished;
    private int correctPlateCount;

    public void start() {
        active = true;
        finished = false;
        correctPlateCount = 0;
    }

    public void stop() {
        active = false;
        finished = false;
        correctPlateCount = 0;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getCorrectPlateCount() {
        return correctPlateCount;
    }

    public boolean registerHealthyPlate() {
        if (!active || finished) {
            return finished;
        }

        correctPlateCount++;

        if (correctPlateCount >= 3) {
            finished = true;
        }

        return finished;
    }
}