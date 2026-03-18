package com.sit.inf1009.project.engine.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.core.StartMenuScene;
import com.sit.inf1009.project.engine.interfaces.IOListener;

import java.util.Stack;

public class SceneManager implements IOListener {

    private static SceneManager instance;

    private final Stack<Scene> scenes = new Stack<>();
    private final EntityManager em;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final InputOutputManager ioManager;

    public SceneManager(EntityManager em, MovementManager mm,
                        CollisionManager cm, InputOutputManager ioManager) {
        instance = this;
        this.em               = em;
        this.movementManager  = mm;
        this.collisionManager = cm;
        this.ioManager        = ioManager;

        // Listen for window resize and display events
        ioManager.addListener(IOEvent.Type.WINDOW_RESIZED, this);
        ioManager.addListener(IOEvent.Type.DISPLAY_RENDER, this);

        // Push the first scene
        push(new StartMenuScene());
    }

    public static SceneManager getInstance() {
        return instance;
    }

    // ── Scene stack operations ────────────────────────────────────────────────

    /** Push a new scene on top — previous scene is paused but kept */
    public void push(Scene scene) {
        scenes.push(scene);
        scene.create();
    }

    /** Pop current scene — returns to previous scene */
    public void pop() {
        if (!scenes.isEmpty()) {
            scenes.peek().dispose();
            scenes.pop();
        }
    }

    /** Replace current scene — no going back */
    public void setScene(Scene scene) {
        if (!scenes.isEmpty()) {
            scenes.peek().dispose();
            scenes.pop();
        }
        push(scene);
    }

    public Scene current() {
        return scenes.isEmpty() ? null : scenes.peek();
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    public void update(float dt) {
        // Update movement and collision for gameplay scenes
        movementManager.updateAll(dt);
        collisionManager.update();
        em.flushRemovals();

        if (current() != null)
            current().update(dt, em.getEntities());
    }

    public void render(SpriteBatch batch) {
        if (current() != null)
            current().render(batch);
    }

    public void resize(int width, int height) {
        if (current() != null)
            current().resize(width, height);
    }

    // ── IOListener ────────────────────────────────────────────────────────────

    @Override
    public void onIOEvent(IOEvent event) {
        if (event == null) return;

        switch (event.getType()) {
            case WINDOW_RESIZED:
                // Forward resize to current scene
                if (event.getPayload() instanceof int[]) {
                    int[] size = (int[]) event.getPayload();
                    resize(size[0], size[1]);
                }
                break;

            case DISPLAY_RENDER:
                // Could trigger a render pass if needed
                break;

            default:
                break;
        }
    }
}