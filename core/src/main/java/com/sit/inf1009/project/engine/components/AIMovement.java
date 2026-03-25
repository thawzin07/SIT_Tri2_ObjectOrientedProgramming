package com.sit.inf1009.project.engine.components;

import com.sit.inf1009.project.engine.entities.Entity;

public class AIMovement extends MovementComponent {
    private int dirX;
    private int dirY;
    private boolean customBoundsEnabled;
    private double minXBound;
    private double maxXBound;
    private double minYBound;
    private double maxYBound;

    public AIMovement(double speed, int dirX, int dirY) {
        super(speed);
        this.dirX = dirX == 0 ? 1 : dirX;
        this.dirY = dirY == 0 ? 1 : dirY;
    }

    @Override
    public void update(Entity e, double dt) {
        double vx = dirX * speed;
        double vy = dirY * speed;

        double newX = e.getXPosition() + vx * dt;
        double newY = e.getYPosition() + vy * dt;

        double minX = 0d;
        double minY = 0d;
        double maxX = com.badlogic.gdx.Gdx.graphics.getWidth();
        double maxY = com.badlogic.gdx.Gdx.graphics.getHeight();
        if (customBoundsEnabled) {
            minX = minXBound;
            maxX = maxXBound;
            minY = minYBound;
            maxY = maxYBound;
        }

        float r = 0f;
        if (e.getCollidable() != null) {
            r = (float) e.getCollidable().getCollisionRadius();
        }

        // Bounce horizontally
        if (newX <= minX + r) {
            newX = minX + r;
            dirX *= -1;
        } else if (newX >= maxX - r) {
            newX = maxX - r;
            dirX *= -1;
        }

        // Bounce vertically
        if (newY <= minY + r) {
            newY = minY + r;
            dirY *= -1;
        } else if (newY >= maxY - r) {
            newY = maxY - r;
            dirY *= -1;
        }

        e.setXPosition(newX);
        e.setYPosition(newY);
    }

    public void bounceHorizontal() {
        dirX *= -1;
    }

    public void bounceVertical() {
        dirY *= -1;
    }

    public void setBounds(double minX, double maxX, double minY, double maxY) {
        this.customBoundsEnabled = true;
        this.minXBound = minX;
        this.maxXBound = maxX;
        this.minYBound = minY;
        this.maxYBound = maxY;
    }
}
