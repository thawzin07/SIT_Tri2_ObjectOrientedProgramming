package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.interfaces.InputHandler;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

/**
 * Base class for all input handlers.
 *
 * Manages enabled/disabled state and provides a dispatch helper
 * that forwards raw events to InputOutputManager only when active.
 */
public abstract class AbstractInputHandler implements InputHandler {

    private boolean enabled = true;
    protected final InputOutputManager ioManager;

    protected AbstractInputHandler(InputOutputManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override public void enable()       { this.enabled = true; }
    @Override public void disable()      { this.enabled = false; }
    @Override public boolean isEnabled() { return enabled; }

    protected void dispatch(IOEvent event) {
        if (enabled) {
            ioManager.handleEvent(event);
        }
    }
}