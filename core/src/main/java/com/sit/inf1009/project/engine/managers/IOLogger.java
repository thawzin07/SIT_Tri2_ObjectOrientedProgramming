package com.sit.inf1009.project.engine.managers;

import com.sit.inf1009.project.engine.interfaces.IOListener;

import java.time.Instant;

/**
 * Debug logger for all IO events (both input and output).
 * Enable/disable at runtime via setEnabled().
 */
public class IOLogger implements IOListener {

    private boolean enabled;

    public IOLogger(boolean enabledByDefault) {
        this.enabled = enabledByDefault;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled()              { return enabled; }

    @Override
    public void onIOEvent(IOEvent event) {
        if (enabled) {
            System.out.printf("[IO %s] %s%n", Instant.now(), event);
        }
    }
}