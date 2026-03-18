package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import java.util.List;

public class Scene {
    private String name;
    private Color backgroundColor;
    private float timeAlive = 0f;

    public Scene(String name, Color color) {
        this.name = name;
        this.backgroundColor = color;
    }

    // Override in subclasses to set up textures, stage, etc.
    public void create() {}

    // Override in subclasses to draw
    public void render(SpriteBatch batch) {}
    
    public void resize(int width, int height) {}

    // Override in subclasses for custom update; base handles clamping
    public void update(float dt, List<Entity> entities) {
        timeAlive += dt;
        if (entities == null) return;
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        for (Entity e : entities) {
            clampToScreen(e, w, h);
        }
    }

    // Override in subclasses to clean up resources
    public void dispose() {}

    public float getTimeAlive() { return timeAlive; }

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

    public Color getBackgroundColor() { return backgroundColor; }
    public String getName() { return name; }
}