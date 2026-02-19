package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

/**
 * Captures raw window-level events and dispatches the appropriate IOEvent type.
 *
 * Payloads:
 *  WINDOW_RESIZED      → Dimension (new size)
 *  WINDOW_FOCUS_GAINED → null
 *  WINDOW_FOCUS_LOST   → null
 *  WINDOW_CLOSED       → null
 */
public class WindowInputHandler extends AbstractInputHandler {

    private final JFrame frame;
    private final ComponentAdapter componentAdapter;
    private final WindowFocusListener focusListener;
    private final WindowAdapter windowAdapter;

    public WindowInputHandler(InputOutputManager ioManager, JFrame frame) {
        super(ioManager);
        this.frame = frame;

        this.componentAdapter = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                dispatch(new IOEvent(IOEvent.Type.WINDOW_RESIZED, frame.getSize()));
            }
        };

        this.focusListener = new WindowFocusListener() {
            @Override public void windowGainedFocus(WindowEvent e) { dispatch(new IOEvent(IOEvent.Type.WINDOW_FOCUS_GAINED, null)); }
            @Override public void windowLostFocus(WindowEvent e)   { dispatch(new IOEvent(IOEvent.Type.WINDOW_FOCUS_LOST,   null)); }
        };

        this.windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispatch(new IOEvent(IOEvent.Type.WINDOW_CLOSED, null));
            }
        };

        frame.addComponentListener(this.componentAdapter);
        frame.addWindowFocusListener(this.focusListener);
        frame.addWindowListener(this.windowAdapter);
    }

    @Override
    public void detach() {
        frame.removeComponentListener(componentAdapter);
        frame.removeWindowFocusListener(focusListener);
        frame.removeWindowListener(windowAdapter);
    }
}