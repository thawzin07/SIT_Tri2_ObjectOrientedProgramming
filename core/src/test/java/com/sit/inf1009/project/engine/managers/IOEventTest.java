package com.sit.inf1009.project.engine.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IOEventTest {

    @Test
    void requirePayloadReturnsTypedValueWhenValid() {
        IOEvent event = new IOEvent(IOEvent.Type.KEY_PRESSED, 42);
        assertEquals(42, event.requirePayload(Integer.class));
    }

    @Test
    void requirePayloadThrowsWhenPayloadIsNull() {
        IOEvent event = new IOEvent(IOEvent.Type.SOUND_STOP_ALL, null);
        assertThrows(IllegalStateException.class, () -> event.requirePayload(String.class));
    }

    @Test
    void requirePayloadThrowsWhenPayloadTypeMismatches() {
        IOEvent event = new IOEvent(IOEvent.Type.KEY_PRESSED, 7);
        assertThrows(IllegalStateException.class, () -> event.requirePayload(String.class));
    }

    @Test
    void getPayloadOrNullReturnsNullWhenTypeMismatches() {
        IOEvent event = new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "hello");
        assertNull(event.getPayloadOrNull(Integer.class));
    }

    @Test
    void toStringContainsType() {
        IOEvent event = new IOEvent(IOEvent.Type.MOUSE_CLICKED, "x");
        assertTrue(event.toString().contains("MOUSE_CLICKED"));
    }
}
