package com.sit.inf1009.project.app.controllers;

import com.sit.inf1009.project.app.GameState;

public final class GameFlowController {
    private GameState state;
    private boolean rulesOpenedFromPause;
    private boolean leaderboardOpenedFromMenu;
    private String statusMessage = "";
    private float statusSecondsLeft = 0f;
    private boolean rulesOpenedFromStart;
    private boolean paused;
    private boolean leaderboardNameEditing;
    private boolean openCredits;

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void togglePaused() {
        paused = !paused;
    }
    public GameFlowController(GameState initialState) {
        this.state = initialState;
    }

    public GameState getState() {
        return state;
    }
    public boolean isPaused() {
		return paused;
	}
    public boolean isLeaderboardNameEditing() {	
    			return leaderboardNameEditing;
    }
    public void setLeaderboardNameEditing(boolean leaderboardNameEditing) {
				this.leaderboardNameEditing = leaderboardNameEditing;
	}

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isRulesOpenedFromPause() {
        return rulesOpenedFromPause;
    }
    public boolean isRulesOpenedFromStart() {
    			return rulesOpenedFromStart;	
    }
    public boolean isLeaderboardOpenedFromMenu() {
        return leaderboardOpenedFromMenu;
    }
    public void setRulesOpenedFromPause(boolean rulesOpenedFromPause) {
        this.rulesOpenedFromPause = rulesOpenedFromPause;
    }
    public void setRulesOpenedFromStart(boolean rulesOpenedFromStart) {
		this.rulesOpenedFromStart = rulesOpenedFromStart;
	}
  
    public void setLeaderboardOpenedFromMenu(boolean leaderboardOpenedFromMenu) {
        this.leaderboardOpenedFromMenu = leaderboardOpenedFromMenu;
    }
    
    public void openLeaderboard(boolean leaderboardOpenedFromMenu) {
        this.leaderboardOpenedFromMenu = leaderboardOpenedFromMenu;
        state = GameState.LEADERBOARD_VIEW;
    }
    
    public void openCredits() {
        this.openCredits = true;
        state = GameState.CREDITS;
    }
    
    public void closeCredits() {
    	this.openCredits = false;
        state = GameState.FOOD_MENU;
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
    public void openHowToPlayFromStart() {
        rulesOpenedFromStart = true;
        rulesOpenedFromPause = false;
        state = GameState.HOW_TO_PLAY;
    }
    
    public void openDifficultySettings()
    {
    	state = GameState.DIFFICULTY_SETTINGS;
    }
    
    public void openTutorial()
    {
    	rulesOpenedFromStart = false;
    	rulesOpenedFromPause = false;
		state = GameState.TUTORIAL;
	}
    
    public void goToMainMenu() {
        state = GameState.FOOD_MENU;
    }

    public void goToAvatarSetup() {
        state = GameState.AVATAR_SETUP;
    }

    public void goToLeaderboardEntry() {
        state = GameState.LEADERBOARD_ENTRY;
    }

    public void startPlaying() {
        state = GameState.PLAYING;
    }

    public void openRulesFromPause() {
        rulesOpenedFromPause = true;
        rulesOpenedFromStart = false;
        state = GameState.HOW_TO_PLAY;
    }    
}
