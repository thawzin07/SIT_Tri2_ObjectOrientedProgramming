package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.interfaces.IOListener;

/**
 * Output handler responsible for display-related output signals.
 *
 * Reacts to:
 *  DISPLAY_RENDER    → tells SceneManager to render the next frame
 *  DISPLAY_SHOW_HUD  → tells SceneManager to show a HUD message (payload: String)
 *  DISPLAY_EFFECT    → tells SceneManager to trigger a visual effect (payload: String)
 *
 * DisplayOutputHandler does NOT render itself — it delegates to SceneManager
 * via the IOListener it is given. This keeps rendering inside SceneManager
 * while IO controls when to trigger it.
 *
 * Usage — other managers trigger display output through IO:
 *   ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_SHOW_HUD, "Game Over"));
 *   ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
 */
public class DisplayOutputHandler extends AbstractOutputHandler {

    private final IOListener sceneManager; // SceneManager implements IOListener

    public DisplayOutputHandler(IOListener sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    protected void handleOutput(IOEvent event) {
        if (event.getType() == IOEvent.Type.DISPLAY_RENDER  ||
            event.getType() == IOEvent.Type.DISPLAY_SHOW_HUD ||
            event.getType() == IOEvent.Type.DISPLAY_EFFECT) {

            sceneManager.onIOEvent(event); // forward to SceneManager to handle
        }
    }

    @Override
    public void close() {
        deactivate();
    }
}