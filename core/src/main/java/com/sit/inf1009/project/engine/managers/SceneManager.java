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

    public SceneManager(EntityManager em, MovementManager mm, CollisionManager cm) {
        this.scenes = new Stack<>();
        this.em = em;
        this.movementManager = mm;
        this.collisionManager = cm;
    }

    public void push(Scene scene) {
        if (em != null) {
            em.clear(); // clears entities from the master database
        }
        if (movementManager != null) {
            movementManager.clear(); // clears physics calculations
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

    public void spawnEntity(Entity entity) {
        // 1. Add to the master database
        em.addEntity(entity);
        
        // 2. Pass it directly to MovementManager
        movementManager.addMovable(entity);
    }

    public void removeEntity(Entity entity) {
        em.removeEntity(entity);
        movementManager.removeMovable(entity);
    }

    public void update(float dt) 
    {
        if (!scenes.isEmpty()) {
            scenes.peek().update(dt, em.getEntities());
        }
    }

    public void render(SpriteBatch batch) {
        if (!scenes.isEmpty()) {
            ScreenUtils.clear(scenes.peek().getBackgroundColor());
        }
    }
}