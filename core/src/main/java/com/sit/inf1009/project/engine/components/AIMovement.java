package com.sit.inf1009.project.engine.components;

import com.sit.inf1009.project.engine.entities.Entity;

public class AIMovement extends MovementComponent {
    private int dirX;
    private int dirY;

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

        int w = com.badlogic.gdx.Gdx.graphics.getWidth();
        int h = com.badlogic.gdx.Gdx.graphics.getHeight();

        float r = 0f;
        if (e.getCollidable() != null) {
            r = (float) e.getCollidable().getCollisionRadius();
        }

        // Bounce horizontally
        if (newX <= r) {
            newX = r;
            dirX *= -1;
        } else if (newX >= w - r) {
            newX = w - r;
            dirX *= -1;
        }

        // Bounce vertically
        if (newY <= r) {
            newY = r;
            dirY *= -1;
        } else if (newY >= h - r) {
            newY = h - r;
            dirY *= -1;
        }

        e.setXPosition(newX);
        e.setYPosition(newY);
    }
}