package com.sit.inf1009.project.engine.interfaces;

import com.sit.inf1009.project.engine.managers.IOEvent;

/**
 * Any manager that reacts to IO events (input or output) implements this.
 *
 * Input side:  SimulationManager, MovementManager, EntityManager, SceneManager
 * Output side: SoundOutputHandler, DisplayOutputHandler
 */
public interface IOListener {
    void onIOEvent(IOEvent event);
}