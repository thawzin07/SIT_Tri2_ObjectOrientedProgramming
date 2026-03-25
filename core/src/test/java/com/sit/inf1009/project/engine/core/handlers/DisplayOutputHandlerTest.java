package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisplayOutputHandlerTest {

    @Test
    void forwardsOnlyDisplayEventsToSceneListener() {
        CapturingListener listener = new CapturingListener();
        DisplayOutputHandler handler = new DisplayOutputHandler(listener);

        handler.onIOEvent(new IOEvent(IOEvent.Type.DISPLAY_RENDER, null));
        handler.onIOEvent(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "hello"));
        handler.onIOEvent(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
        handler.onIOEvent(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));

        assertEquals(3, listener.events.size());
        assertEquals(IOEvent.Type.DISPLAY_RENDER, listener.events.get(0).getType());
        assertEquals(IOEvent.Type.DISPLAY_SHOW_HUD, listener.events.get(1).getType());
        assertEquals(IOEvent.Type.DISPLAY_EFFECT, listener.events.get(2).getType());
    }

    @Test
    void closeDeactivatesHandler() {
        CapturingListener listener = new CapturingListener();
        DisplayOutputHandler handler = new DisplayOutputHandler(listener);

        handler.close();
        handler.onIOEvent(new IOEvent(IOEvent.Type.DISPLAY_RENDER, null));

        assertEquals(0, listener.events.size());
    }

    private static class CapturingListener implements IOListener {
        final List<IOEvent> events = new ArrayList<>();

        @Override
        public void onIOEvent(IOEvent event) {
            events.add(event);
        }
    }
}
