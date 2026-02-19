package com.sit.inf1009.project.engine.components;
import com.sit.inf1009.project.engine.interfaces.CollidableInterface;

public class CollidableComponent implements CollidableInterface {

    private double collisionRadius;
    private boolean collisionEnabled;

    public CollidableComponent(double radius, boolean collisionEnabled) 
    {
        setCollisionRadius(radius);
        this.collisionEnabled = collisionEnabled;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(double radius) {
        if (radius < 0) radius = 0;
        this.collisionRadius = radius;
    }

    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }

    public void onCollision(CollidableInterface other) {
        // Default behavior: do nothing. Subclasses can override this.
    }
}