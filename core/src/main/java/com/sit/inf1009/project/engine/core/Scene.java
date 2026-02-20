
package com.sit.inf1009.project.engine.core;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import java.util.List;

public class Scene {
    private String name;
    private Color backgroundColor;
    public float timeAlive = 0;

    public Scene(String name, Color color) {
        this.name = name;
        this.backgroundColor = color;
    }

    public void update(float dt, List<Entity> entities) {
        timeAlive += dt;

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        for (Entity e : entities) {
            clampToScreen(e, w, h);
        }
    }

    private void clampToScreen(Entity e, int w, int h) {
        float r = 0f;
        CollidableComponent c = e.getCollidable();
        if (c != null) r = (float) c.getCollisionRadius();

        double x = e.getXPosition();
        double y = e.getYPosition();

        x = Math.max(r, Math.min(x, w - r));
        y = Math.max(r, Math.min(y, h - r));

        e.setXPosition(x);
        e.setYPosition(y);
    }

    public Color getBackgroundColor() { 
        return backgroundColor; 
    }
    
    public String getName() { 
        return name; 
    }
}