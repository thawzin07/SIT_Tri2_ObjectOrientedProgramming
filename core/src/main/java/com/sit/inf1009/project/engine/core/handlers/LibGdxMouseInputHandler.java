package com.sit.inf1009.project.engine.core.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MouseEventData;

/**
 * Captures libGDX mouse events and dispatches IOEvent mouse types.
 */
public class LibGdxMouseInputHandler extends AbstractInputHandler {

    private final InputAdapter processor;

    public LibGdxMouseInputHandler(InputOutputManager ioManager) {
        super(ioManager);

        this.processor = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                MouseEventData data = MouseEventData.pointer(screenX, screenY, button, pointer);
                dispatch(new IOEvent(IOEvent.Type.MOUSE_PRESSED, data));
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                MouseEventData data = MouseEventData.pointer(screenX, screenY, button, pointer);
                dispatch(new IOEvent(IOEvent.Type.MOUSE_RELEASED, data));
                dispatch(new IOEvent(IOEvent.Type.MOUSE_CLICKED, data));
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                dispatch(new IOEvent(IOEvent.Type.MOUSE_DRAGGED, MouseEventData.pointer(screenX, screenY, -1, pointer)));
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                dispatch(new IOEvent(IOEvent.Type.MOUSE_MOVED, MouseEventData.move(screenX, screenY)));
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                dispatch(new IOEvent(IOEvent.Type.MOUSE_WHEEL, MouseEventData.wheel(amountX, amountY)));
                return false;
            }
        };

        attachToGdxInput(processor);
    }

    @Override
    public void detach() {
        InputProcessor current = Gdx.input.getInputProcessor();
        if (current instanceof InputMultiplexer multiplexer) {
            multiplexer.removeProcessor(processor);
            if (multiplexer.size() == 0) {
                Gdx.input.setInputProcessor(null);
            }
        } else if (current == processor) {
            Gdx.input.setInputProcessor(null);
        }
    }

    private static void attachToGdxInput(InputProcessor nextProcessor) {
        InputProcessor current = Gdx.input.getInputProcessor();
        if (current instanceof InputMultiplexer multiplexer) {
            multiplexer.addProcessor(nextProcessor);
            return;
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        if (current != null) {
            multiplexer.addProcessor(current);
        }
        multiplexer.addProcessor(nextProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }
}
