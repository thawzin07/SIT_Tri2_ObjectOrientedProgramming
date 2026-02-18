package com.sit.inf1009.project.engine.core;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.entities.CollidableEntity;

import com.sit.inf1009.project.engine.interfaces.MovementInterface; 

import com.sit.inf1009.project.engine.managers.CollisionManager;
import com.sit.inf1009.project.engine.managers.MovementManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class Scene {
    
    private String name;
    
    private List<Entity> entityList;

    private CollisionManager collisionManager;
    private MovementManager movementManager;
    
    private Color backgroundColor;

    public float timeAlive = 0; 

    public Scene(String name, Color color) {
        this.name = name;
        this.backgroundColor = color;
        this.entityList = new ArrayList<>();
        
        this.collisionManager = new CollisionManager();
        this.movementManager = new MovementManager();
    }

    public void onEnter() {
        System.out.println("Scene " + name + " entered.");
    }

    public void onExit() {
        System.out.println("Scene " + name + " exited.");
        this.dispose(); 
    }
    

    public void addEntity(Entity entity) {
    	entityList.add(entity);

        if (entity instanceof MovementInterface) {
            movementManager.add((MovementInterface) entity);
        }

        if (entity instanceof CollidableEntity) {
            collisionManager.add((CollidableEntity) entity);
        }
    }
    
    public void removeEntity(Entity entity) {
        // Remove from the Scene's main list (so it stops rendering)
        entityList.remove(entity);

        // Remove from MovementManager
        if (entity instanceof MovementInterface) {
            movementManager.remove((MovementInterface) entity);
        }

        // Remove from CollisionManager
        if (entity instanceof CollidableEntity) {
            collisionManager.remove((CollidableEntity) entity);
        }
        
        System.out.println("Scene: Entity removed successfully.");
    }
    
    public List<Entity> getEntities() {
        return entityList;
    }

    public void update(float dt) {
        timeAlive += dt;

        movementManager.updateAll((double) dt);

        collisionManager.update(); 
    }

    public void render(SpriteBatch batch) {
    	ScreenUtils.clear(backgroundColor);
    	
        for (Entity e : entityList) {
            if (e.getTexture() != null) {
                 batch.draw(e.getTexture(), (float)e.getX(), (float)e.getY());
            }
        }
    }
    
    public void dispose() {
    	entityList.clear();
        collisionManager.clear();
        movementManager.clear();
    }
    
    public String getName() { return name; }
    
    public int getEntityCount() { return entityList.size(); }
}