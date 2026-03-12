package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerImageInputServiceTest {

    @Test
    void emitsSelectedEventWhenPickerReturnsPath() {
        InputOutputManager io = new InputOutputManager();
        AtomicReference<String> selected = new AtomicReference<>();

        io.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTED, e -> selected.set(e.requirePayload(String.class)));
        new PlayerImageInputService(io, () -> "C:\\tmp\\player.png");

        io.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, null));

        assertEquals("C:\\tmp\\player.png", selected.get());
    }

    @Test
    void emitsFailedEventOnCancel() {
        InputOutputManager io = new InputOutputManager();
        AtomicReference<String> failed = new AtomicReference<>();

        io.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, e -> failed.set(e.requirePayload(String.class)));
        new PlayerImageInputService(io, () -> null);

        io.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, "scene-button"));

        assertEquals("cancelled", failed.get());
    }

    @Test
    void emitsFailedEventOnPickerException() {
        InputOutputManager io = new InputOutputManager();
        AtomicReference<String> failed = new AtomicReference<>();

        io.addListener(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, e -> failed.set(e.requirePayload(String.class)));
        new PlayerImageInputService(io, () -> {
            throw new IllegalStateException("chooser unavailable");
        });

        io.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, null));

        assertEquals("chooser unavailable", failed.get());
    }
}
