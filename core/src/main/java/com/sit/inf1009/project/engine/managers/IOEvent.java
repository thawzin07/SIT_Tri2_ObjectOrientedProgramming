package com.sit.inf1009.project.engine.managers;

/**
 * Represents an event flowing through the IO system.
 *
 * Input types  — raw signals from hardware into the game.
 * Output types — signals from the game out to display or audio.
 */
public class IOEvent {

    public enum Type {
        // ── Input: Keyboard ───────────────────────────────────────────────────
        KEY_PRESSED,
        KEY_RELEASED,

        // ── Input: Mouse ──────────────────────────────────────────────────────
        MOUSE_CLICKED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_WHEEL,

        // ── Input: Window ─────────────────────────────────────────────────────
        WINDOW_RESIZED,
        WINDOW_FOCUS_LOST,
        WINDOW_FOCUS_GAINED,
        WINDOW_CLOSED,

        // ── Output: Audio ─────────────────────────────────────────────────────
        SOUND_PLAY,        // payload: String  — sound clip name
        SOUND_STOP,        // payload: String  — sound clip name
        SOUND_STOP_ALL,    // payload: null

        // ── Output: Display ───────────────────────────────────────────────────
        DISPLAY_RENDER,    // payload: null    — request a frame render
        DISPLAY_SHOW_HUD,  // payload: String  — HUD message to display
        DISPLAY_EFFECT     // payload: String  — visual effect name (e.g. "flash")
    }

    private final Type   type;
    private final Object payload;
    private final long   timestamp;

    public IOEvent(Type type, Object payload) {
        this.type      = type;
        this.payload   = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType()      { return type; }
    public Object getPayload() { return payload; }
    public long getTimestamp() { return timestamp; }

    @SuppressWarnings("unchecked")
    public <T> T getPayload(Class<T> clazz) {
        return clazz.cast(payload);
    }

    @Override
    public String toString() {
        return "IOEvent[type=" + type + ", payload=" + payload + ", ts=" + timestamp + "]";
    }
}