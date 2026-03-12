package com.sit.inf1009.project.engine.managers;

import com.sit.inf1009.project.engine.interfaces.IOListener;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Debug logger for all IO events (both input and output).
 * Enable/disable at runtime via setEnabled().
 */
public class IOLogger implements IOListener {

    private boolean enabled;
    private final Map<IOEvent.Type, Integer> eventCounts = new EnumMap<>(IOEvent.Type.class);
    private final ArrayDeque<IOEvent> recentEvents = new ArrayDeque<>();
    private int recentEventLimit = 25;

    public IOLogger(boolean enabledByDefault) {
        this.enabled = enabledByDefault;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled()              { return enabled; }

    @Override
    public void onIOEvent(IOEvent event) {
        if (event == null || event.getType() == null) return;

        eventCounts.merge(event.getType(), 1, Integer::sum);
        addRecentEvent(event);

        if (enabled) {
            System.out.printf("[IO %s] %s%n", Instant.now(), event);
        }
    }

    private void addRecentEvent(IOEvent event) {
        if (recentEventLimit <= 0) return;

        recentEvents.addLast(event);
        while (recentEvents.size() > recentEventLimit) {
            recentEvents.removeFirst();
        }
    }

    public int getCount(IOEvent.Type type) {
        if (type == null) return 0;
        return eventCounts.getOrDefault(type, 0);
    }

    public Map<IOEvent.Type, Integer> getCountsSnapshot() {
        return Collections.unmodifiableMap(new EnumMap<>(eventCounts));
    }

    public List<IOEvent> getRecentEventsSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recentEvents));
    }

    public void setRecentEventLimit(int limit) {
        this.recentEventLimit = Math.max(0, limit);
        while (recentEvents.size() > recentEventLimit) {
            recentEvents.removeFirst();
        }
    }

    public int getRecentEventLimit() {
        return recentEventLimit;
    }

    public void clear() {
        eventCounts.clear();
        recentEvents.clear();
    }
}
