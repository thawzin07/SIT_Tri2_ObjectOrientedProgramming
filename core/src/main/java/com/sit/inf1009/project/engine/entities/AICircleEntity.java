package com.sit.inf1009.project.engine.entities;

public class AICircleEntity extends Entity {
    private final float radius;

    public AICircleEntity(int id, double x, double y, float radius) {
        super(id);
        setXPosition(x);
        setYPosition(y);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
