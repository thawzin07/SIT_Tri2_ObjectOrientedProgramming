package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;

/**
 * Captures raw keyboard events and dispatches KEY_PRESSED / KEY_RELEASED.
 * No key interpretation — raw KeyEvent is the payload.
 */
public class KeyboardInputHandler extends AbstractInputHandler {

    private final JComponent target;
    private final KeyAdapter keyAdapter;

    public KeyboardInputHandler(InputOutputManager ioManager, JComponent target) {
        super(ioManager);
        this.target = target;

        this.keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                dispatch(new IOEvent(IOEvent.Type.KEY_PRESSED, e));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                dispatch(new IOEvent(IOEvent.Type.KEY_RELEASED, e));
            }
        };

        target.addKeyListener(this.keyAdapter);
        target.setFocusable(true);
        target.requestFocusInWindow();
    }

    @Override
    public void detach() {
        target.removeKeyListener(keyAdapter);
    }
}