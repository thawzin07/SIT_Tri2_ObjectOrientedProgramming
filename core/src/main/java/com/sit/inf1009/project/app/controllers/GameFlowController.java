package com.sit.inf1009.project.app.controllers;

import com.sit.inf1009.project.app.GameState;

public final class GameFlowController {
    private GameState state;
    private boolean rulesOpenedFromPause;
    private boolean leaderboardOpenedFromMenu;
    private String statusMessage = "";
    private float statusSecondsLeft = 0f;

    public GameFlowController(GameState initialState) {
        this.state = initialState;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isRulesOpenedFromPause() {
        return rulesOpenedFromPause;
    }

    public void setRulesOpenedFromPause(boolean rulesOpenedFromPause) {
        this.rulesOpenedFromPause = rulesOpenedFromPause;
    }

    public boolean isLeaderboardOpenedFromMenu() {
        return leaderboardOpenedFromMenu;
    }

    public void setLeaderboardOpenedFromMenu(boolean leaderboardOpenedFromMenu) {
        this.leaderboardOpenedFromMenu = leaderboardOpenedFromMenu;
    }

    public void showStatus(String message, float seconds) {
        statusMessage = message;
        statusSecondsLeft = seconds;
    }

    public void tickStatus(float dt) {
        if (statusSecondsLeft > 0f) {
            statusSecondsLeft -= dt;
        }
    }

    public boolean hasStatus() {
        return statusSecondsLeft > 0f && statusMessage != null && !statusMessage.isBlank();
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
