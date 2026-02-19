package com.sit.inf1009.project.engine.core.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

/**
 * Captures LibGDX keyboard events and dispatches KEY_PRESSED / KEY_RELEASED.
 * Payload is the LibGDX keycode (int).
 */
public class KeyboardInputHandler extends AbstractInputHandler {

    private final InputAdapter processor;

    public KeyboardInputHandler(InputOutputManager ioManager) {
        super(ioManager);

        this.processor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                dispatch(new IOEvent(IOEvent.Type.KEY_PRESSED, keycode));
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                dispatch(new IOEvent(IOEvent.Type.KEY_RELEASED, keycode));
                return true;
            }
        };

        Gdx.input.setInputProcessor(processor);
    }

    @Override
    public void detach() {
        if (Gdx.input.getInputProcessor() == processor) {
            Gdx.input.setInputProcessor(null);
        }
    }
}
