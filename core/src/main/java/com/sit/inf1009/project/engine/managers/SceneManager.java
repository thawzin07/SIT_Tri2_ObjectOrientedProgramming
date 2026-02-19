package com.sit.inf1009.project.engine.managers;

import java.util.Stack;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.core.Scene;

public class SceneManager {
    
    private Stack<Scene> scenes;
    private MovementManager movementManager;
    private CollisionManager collisionManager;


    public SceneManager(MovementManager mm, CollisionManager cm) {
        this.scenes = new Stack<>();
        this.movementManager = mm;
        this.collisionManager = cm;
    }

    public void push(Scene newScene) {
        // If a scene is currently playing, tell it to "unplug" its entities from the physics engine
        if (!scenes.isEmpty()) {
            scenes.peek().onExit(movementManager, collisionManager);
        }
        
        // Put the new scene on top of the stack
        scenes.push(newScene);
        
        // Tell the new scene to "plug in" its entities to the physics engine
        newScene.onEnter(movementManager, collisionManager);
    }

    public void pop() {
        // Remove the current scene from the top and unplug its entities
        if (!scenes.isEmpty()) {
            Scene oldScene = scenes.pop();
            oldScene.onExit(movementManager, collisionManager);
        }
        
        // If there is a scene underneath it,
        // we need to tell Level 1 to plug its entities back into the physics engine.
        if (!scenes.isEmpty()) {
            scenes.peek().onEnter(movementManager, collisionManager);
        }
    }

    public void update(float dt) {
        // Only trigger the local logic (like timers) for the level we are currently looking at
        if (!scenes.isEmpty()) {
            scenes.peek().update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        // Only draw the level we are currently looking at
        if (!scenes.isEmpty()) {
            scenes.peek().render(batch);
        }
    }
}
