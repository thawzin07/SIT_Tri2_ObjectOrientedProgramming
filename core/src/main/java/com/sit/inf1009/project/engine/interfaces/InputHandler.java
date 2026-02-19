package com.sit.inf1009.project.engine.interfaces;

/**
 * Contract for all input capture handlers.
 *
 * Each handler attaches to a specific input source (keyboard, mouse, window)
 * and forwards raw events to the InputOutputManager.
 */
public interface InputHandler {

    /**
     * Activates this handler so it forwards events.
     */
    void enable();

    /**
     * Deactivates this handler — events are silently dropped.
     */
    void disable();

    /**
     * @return true if this handler is currently active
     */
    boolean isEnabled();

    /**
     * Detaches listeners from the underlying AWT source (component, frame, etc.)
     * Called during shutdown to avoid memory leaks.
     */
    void detach();
}