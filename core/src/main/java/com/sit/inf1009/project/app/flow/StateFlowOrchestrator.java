package com.sit.inf1009.project.app.flow;

import com.sit.inf1009.project.app.GameState;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import java.util.Objects;

public final class StateFlowOrchestrator {

    private StateFlowOrchestrator() {
    }

    public static void playButtonClick(InputOutputManager ioManager) {
        ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));
    }

    public static String syncBackgroundMusicForState(InputOutputManager ioManager,
                                                     GameState gameState,
                                                     String activeBackgroundTrack) {
        String nextTrack = backgroundTrackForState(gameState);
        if (Objects.equals(nextTrack, activeBackgroundTrack)) {
            return activeBackgroundTrack;
        }

        if (activeBackgroundTrack != null) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_STOP, activeBackgroundTrack));
        }
        if (nextTrack != null) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, nextTrack));
        }
        return nextTrack;
    }

    private static String backgroundTrackForState(GameState state) {
        if (state == null) {
            return null;
        }

        return switch (state) {
            case FOOD_MENU -> "foodmenumusic";
            case DIFFICULTY_SETTINGS -> "settingmusic";
            case HOW_TO_PLAY -> "howtoplaymusic";
            case LEADERBOARD_ENTRY, LEADERBOARD_VIEW -> "leaderboardmusic";
            default -> null;
        };
    }
}
