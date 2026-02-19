package com.sit.inf1009.project.engine.managers;

import java.util.Stack;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.entities.Entity;

public class SceneManager {
    private Stack<Scene> scenes;
    
    private EntityManager em;
    private MovementManager movementManager;
    private CollisionManager collisionManager;

    // Updated Constructor
    public SceneManager(EntityManager em, MovementManager mm, CollisionManager cm) {
        this.scenes = new Stack<>();
        this.em = em;
        this.movementManager = mm;
        this.collisionManager = cm;
    }

    public void push(Scene scene) {
        if (em != null) {
            em.clear(); // Wipes entities from the master database
        }
        if (movementManager != null) {
            movementManager.clear(); // Wipes physics calculations
        }
        scenes.push(scene);
    }

    public void pop() {
        if (!scenes.isEmpty()) {
            scenes.pop();
        }
        if (em != null) {
            em.clear(); 
        }
        if (movementManager != null) {
            movementManager.clear(); 
        }
    }

    // The new spawn method Main.java is looking for!
    public void spawnEntity(Entity entity) {
        // 1. Add to the master database
        em.addEntity(entity);
        
        // 2. Pass it directly to MovementManager
        movementManager.addMovable(entity);
        
        // Note: CollisionManager automatically checks EntityManager now, 
        // so we don't need to add it manually here!
    }

    public void removeEntity(Entity entity) {
        em.removeEntity(entity);
        movementManager.removeMovable(entity);
    }

    public void update(float dt) {
        if (!scenes.isEmpty()) {
            scenes.peek().update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        if (!scenes.isEmpty()) {
            ScreenUtils.clear(scenes.peek().getBackgroundColor());
        }
    }
}