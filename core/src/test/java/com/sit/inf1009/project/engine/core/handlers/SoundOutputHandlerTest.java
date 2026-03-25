package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SoundOutputHandlerTest {

    @Test
    void handlesSoundEventsWithoutThrowingWhenAudioBackendUnavailable() {
        SoundOutputHandler handler = new SoundOutputHandler();

        assertDoesNotThrow(() -> handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click")));
        assertDoesNotThrow(() -> handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_PLAY, "collisionmusic")));
        assertDoesNotThrow(() -> handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_PLAY, "foodmenumusic")));
        assertDoesNotThrow(() -> handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_STOP, "foodmenumusic")));
        assertDoesNotThrow(() -> handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_STOP_ALL, null)));
    }

    @Test
    void closeIsSafeToCall() {
        SoundOutputHandler handler = new SoundOutputHandler();
        assertDoesNotThrow(handler::close);
    }
}
