
package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.graphics.Color;

public class Scene {
    private String name;
    private Color backgroundColor;
    public float timeAlive = 0;

    public Scene(String name, Color color) {
        this.name = name;
        this.backgroundColor = color;
    }

    public void update(float dt) {
        timeAlive += dt; 
    }

    public Color getBackgroundColor() { 
        return backgroundColor; 
    }
    
    public String getName() { 
        return name; 
    }
}