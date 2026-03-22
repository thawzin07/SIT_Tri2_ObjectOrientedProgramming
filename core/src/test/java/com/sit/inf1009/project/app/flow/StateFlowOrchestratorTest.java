package com.sit.inf1009.project.app.flow;

import com.sit.inf1009.project.app.GameState;
import com.sit.inf1009.project.engine.interfaces.OutputHandler;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateFlowOrchestratorTest {

    @Test
    void playButtonClickEmitsBtnClickSoundEvent() {
        InputOutputManager io = new InputOutputManager();
        CapturingOutputHandler output = new CapturingOutputHandler();
        io.registerOutputHandler(output);

        StateFlowOrchestrator.playButtonClick(io);

        assertEquals(1, output.events.size());
        assertEquals(IOEvent.Type.SOUND_PLAY, output.events.get(0).getType());
        assertEquals("btn_click", output.events.get(0).requirePayload(String.class));
    }

    @Test
    void syncBackgroundMusicForStatePlaysWhenTrackChanges() {
        InputOutputManager io = new InputOutputManager();
        CapturingOutputHandler output = new CapturingOutputHandler();
        io.registerOutputHandler(output);

        String active = StateFlowOrchestrator.syncBackgroundMusicForState(io, GameState.FOOD_MENU, null);

        assertEquals("foodmenumusic", active);
        assertEquals(1, output.events.size());
        assertEquals(IOEvent.Type.SOUND_PLAY, output.events.get(0).getType());
        assertEquals("foodmenumusic", output.events.get(0).requirePayload(String.class));
    }

    @Test
    void syncBackgroundMusicForStateStopsPreviousAndPlaysNewTrack() {
        InputOutputManager io = new InputOutputManager();
        CapturingOutputHandler output = new CapturingOutputHandler();
        io.registerOutputHandler(output);

        String active = StateFlowOrchestrator.syncBackgroundMusicForState(
                io, GameState.DIFFICULTY_SETTINGS, "foodmenumusic");

        assertEquals("settingmusic", active);
        assertEquals(2, output.events.size());

        assertEquals(IOEvent.Type.SOUND_STOP, output.events.get(0).getType());
        assertEquals("foodmenumusic", output.events.get(0).requirePayload(String.class));

        assertEquals(IOEvent.Type.SOUND_PLAY, output.events.get(1).getType());
        assertEquals("settingmusic", output.events.get(1).requirePayload(String.class));
    }

    @Test
    void syncBackgroundMusicForStateNoopWhenTrackUnchanged() {
        InputOutputManager io = new InputOutputManager();
        CapturingOutputHandler output = new CapturingOutputHandler();
        io.registerOutputHandler(output);

        String active = StateFlowOrchestrator.syncBackgroundMusicForState(
                io, GameState.LEADERBOARD_VIEW, "leaderboardmusic");

        assertEquals("leaderboardmusic", active);
        assertTrue(output.events.isEmpty());
    }

    private static class CapturingOutputHandler implements OutputHandler {
        final List<IOEvent> events = new ArrayList<>();

        @Override
        public void onIOEvent(IOEvent event) {
            events.add(event);
        }

        @Override
        public void close() {
            // no-op
        }
    }
}
