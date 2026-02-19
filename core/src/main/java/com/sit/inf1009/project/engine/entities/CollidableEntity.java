package com.sit.inf1009.project.engine.entities;

import com.sit.inf1009.project.engine.interfaces.CollidableInterface;

public class CollidableEntity extends Entity implements CollidableInterface {

    private double collisionRadius;
    private boolean collisionEnabled;

    public CollidableEntity(double radius) {
        super();
        setCollisionRadius(radius);
        this.collisionEnabled = true;
    }

    @Override
    public double getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(double radius) {
        if (radius < 0) radius = 0;
        this.collisionRadius = radius;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }

    @Override
    public void onCollision(CollidableInterface other) {
        // Default behavior: do nothing. Subclasses can override this.
    }
}