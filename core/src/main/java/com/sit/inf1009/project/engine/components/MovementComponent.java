package com.sit.inf1009.project.engine.components;
import com.sit.inf1009.project.engine.entities.Entity;

public abstract class MovementComponent {
    protected double speed;

    public MovementComponent(double speed) {
        this.speed = speed;
    }

    public abstract void update(Entity e, double dt);
}