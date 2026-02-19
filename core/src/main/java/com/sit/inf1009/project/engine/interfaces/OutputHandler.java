package com.sit.inf1009.project.engine.interfaces;

import com.sit.inf1009.project.engine.managers.IOEvent;

/**
 * Contract for all output handlers (audio, display).
 *
 * Output handlers receive output-type IOEvents from InputOutputManager
 * and act on them — playing a sound, triggering a render, showing an effect.
 *
 * They implement IOListener so they plug into the same event routing system.
 */
public interface OutputHandler extends IOListener {

    /**
     * Called by InputOutputManager to deliver an output event.
     * Implementations decide which event types they handle.
     */
    void onIOEvent(IOEvent event);

    /**
     * Release any resources held by this handler (audio clips, buffers, etc.)
     * Called during shutdown.
     */
    void close();
}