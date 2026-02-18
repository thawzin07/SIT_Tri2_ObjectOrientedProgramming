package com.sit.inf1009.project.engine.managers;

import com.sit.inf1009.project.engine.interfaces.InputHandler;
import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.interfaces.OutputHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Central IO manager for the simulation.
 *
 * ── Input side ────────────────────────────────────────────────────────────────
 *  Owns: KeyboardInputHandler, MouseInputHandler, WindowInputHandler
 *  Flow: Hardware → InputHandler → handleEvent() → IOListeners (other managers)
 *
 * ── Output side ───────────────────────────────────────────────────────────────
 *  Owns: SoundOutputHandler, DisplayOutputHandler
 *  Flow: Any manager → sendOutput(IOEvent) → OutputHandler → device (audio/screen)
 *
 * ── What IO does NOT own ──────────────────────────────────────────────────────
 *  - Collision logic        → CollisionManager
 *  - Simulation state       → SimulationManager
 *  - Rendering logic        → SceneManager
 *  - Entity/bullet spawning → EntityManager
 *  - Movement logic         → MovementManager
 *
 * ── Wiring example ────────────────────────────────────────────────────────────
 * <pre>
 *   InputOutputManager io = new InputOutputManager();
 *
 *   // Input
 *   io.registerInputHandler(new KeyboardInputHandler(io, panel));
 *   io.registerInputHandler(new MouseInputHandler(io, panel));
 *   io.registerInputHandler(new WindowInputHandler(io, frame));
 *
 *   // Output
 *   io.registerOutputHandler(new SoundOutputHandler());
 *   io.registerOutputHandler(new DisplayOutputHandler(sceneManager));
 *
 *   // Other managers subscribe to input events they care about
 *   io.addListener(IOEvent.Type.KEY_PRESSED,    movementManager);
 *   io.addListener(IOEvent.Type.KEY_PRESSED,    simulationManager);
 *   io.addListener(IOEvent.Type.MOUSE_CLICKED,  entityManager);
 *   io.addListener(IOEvent.Type.WINDOW_RESIZED, sceneManager);
 *   io.addListener(IOEvent.Type.WINDOW_CLOSED,  app);
 *
 *   // Any manager triggers output like this:
 *   io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "hit"));
 *   io.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
 * </pre>
 */
public class InputOutputManager {

    // ── Input side ────────────────────────────────────────────────────────────
    private final List<InputHandler> inputHandlers  = new ArrayList<>();
    private final List<IOListener>   globalListeners = new ArrayList<>();
    private final Map<IOEvent.Type, List<IOListener>> typedListeners =
            new EnumMap<>(IOEvent.Type.class);

    // ── Output side ───────────────────────────────────────────────────────────
    private final List<OutputHandler> outputHandlers = new ArrayList<>();

    // ── Debug logger ──────────────────────────────────────────────────────────
    private final IOLogger logger = new IOLogger(false);

    // =========================================================================
    // Registration
    // =========================================================================

    public void registerInputHandler(InputHandler handler) {
        inputHandlers.add(handler);
    }

    public void registerOutputHandler(OutputHandler handler) {
        outputHandlers.add(handler);
    }

    // ── Input listener registration ───────────────────────────────────────────

    /** Subscribe to a specific input event type. */
    public void addListener(IOEvent.Type type, IOListener listener) {
        typedListeners
            .computeIfAbsent(type, k -> new ArrayList<>())
            .add(listener);
    }

    /** Subscribe to ALL event types. */
    public void addGlobalListener(IOListener listener) {
        globalListeners.add(listener);
    }

    public void removeListener(IOEvent.Type type, IOListener listener) {
        List<IOListener> list = typedListeners.get(type);
        if (list != null) list.remove(listener);
    }

    public void removeGlobalListener(IOListener listener) {
        globalListeners.remove(listener);
    }

    // =========================================================================
    // Input event routing — called by AbstractInputHandler.dispatch()
    // =========================================================================

    /**
     * Routes a raw input event to the logger and all subscribed listeners.
     * Called internally by input handlers — do not call directly.
     */
    public void handleEvent(IOEvent event) {
        logger.onIOEvent(event);

        List<IOListener> typed = typedListeners.get(event.getType());
        if (typed != null) {
            for (IOListener listener : typed) {
                listener.onIOEvent(event);
            }
        }

        for (IOListener listener : globalListeners) {
            listener.onIOEvent(event);
        }
    }

    // =========================================================================
    // Output event routing — called by any manager that wants to produce output
    // =========================================================================

    /**
     * Routes an output event (sound, display) to all registered output handlers.
     *
     * Any manager calls this to trigger audio or visual output:
     *   ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "hit"));
     *   ioManager.sendOutput(new IOEvent(IOEvent.Type.DISPLAY_EFFECT, "flash"));
     */
    public void sendOutput(IOEvent event) {
        logger.onIOEvent(event);

        for (OutputHandler handler : outputHandlers) {
            handler.onIOEvent(event);
        }
    }

    // =========================================================================
    // Logger
    // =========================================================================

    public void enableLogger(boolean enabled) {
        logger.setEnabled(enabled);
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================

    public void enableAll() {
        inputHandlers.forEach(InputHandler::enable);
    }

    public void disableAll() {
        inputHandlers.forEach(InputHandler::disable);
    }

    /**
     * Full shutdown — detaches all AWT listeners, closes output handlers,
     * clears all subscriptions. Call on application exit.
     */
    public void shutdown() {
        disableAll();
        inputHandlers.forEach(InputHandler::detach);
        outputHandlers.forEach(OutputHandler::close);
        globalListeners.clear();
        typedListeners.clear();
    }
}