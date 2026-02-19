package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.interfaces.OutputHandler;
import com.sit.inf1009.project.engine.managers.IOEvent;

/**
 * Base class for all output handlers (audio, display).
 *
 * Manages active/inactive state. Subclasses implement
 * {@link #handleOutput(IOEvent)} for their specific output concern.
 */
public abstract class AbstractOutputHandler implements OutputHandler {

    private boolean active = true;

    public void activate()   { this.active = true; }
    public void deactivate() { this.active = false; }
    public boolean isActive(){ return active; }

    @Override
    public final void onIOEvent(IOEvent event) {
        if (active) {
            handleOutput(event);
        }
    }

    /**
     * Subclasses implement their output logic here.
     * Only called when this handler is active.
     */
    protected abstract void handleOutput(IOEvent event);
}