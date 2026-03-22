package com.sit.inf1009.project.engine.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IOLoggerTest {

    @Test
    void tracksCountsAndRecentEvents() {
        IOLogger logger = new IOLogger(false);
        logger.setRecentEventLimit(2);

        logger.onIOEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 1));
        logger.onIOEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 2));
        logger.onIOEvent(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));

        assertEquals(2, logger.getCount(IOEvent.Type.KEY_PRESSED));
        assertEquals(1, logger.getCount(IOEvent.Type.SOUND_PLAY));
        assertEquals(2, logger.getRecentEventsSnapshot().size());
        assertEquals(IOEvent.Type.KEY_PRESSED, logger.getRecentEventsSnapshot().get(0).getType());
        assertEquals(IOEvent.Type.SOUND_PLAY, logger.getRecentEventsSnapshot().get(1).getType());
    }

    @Test
    void clearRemovesCountsAndRecentEvents() {
        IOLogger logger = new IOLogger(false);
        logger.onIOEvent(new IOEvent(IOEvent.Type.KEY_RELEASED, 10));
        logger.onIOEvent(new IOEvent(IOEvent.Type.SOUND_STOP_ALL, null));

        logger.clear();

        assertEquals(0, logger.getCount(IOEvent.Type.KEY_RELEASED));
        assertEquals(0, logger.getCount(IOEvent.Type.SOUND_STOP_ALL));
        assertTrue(logger.getRecentEventsSnapshot().isEmpty());
        assertTrue(logger.getCountsSnapshot().isEmpty());
    }
}
