package com.sit.inf1009.project.engine.core;

import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.components.CollidableComponent;

public class Scene {
    private String name;
    private Color backgroundColor;

    public Scene(String name, Color backgroundColor) {
        this.name = name;
        this.backgroundColor = backgroundColor;
    }

    // This method is called by SceneManager, which gets the list from Main
    public void update(float dt, List<Entity> entities) {
        // Use Gdx.graphics to get the window size every frame
        int screenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        int screenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();

        for (Entity e : entities) {
            clampToScreen(e, screenWidth, screenHeight); 
        }
    }

    private void clampToScreen(Entity e, int screenWidth, int screenHeight) {
        float radius = 0;
        
        // Check if the entity has a collision component to get its radius
        CollidableComponent cc = e.getCollidable();
        if (cc != null) {
            radius = (float) cc.getCollisionRadius();
        }

        // Horizontal boundaries
        if (e.getXPosition() < radius) e.setXPosition(radius);
        if (e.getXPosition() > screenWidth - radius) e.setXPosition(screenWidth - radius);
        
        // Vertical boundaries
        if (e.getYPosition() < radius) e.setYPosition(radius);
        if (e.getYPosition() > screenHeight - radius) e.setYPosition(screenHeight - radius);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public String getName() {
        return name;
    }
}