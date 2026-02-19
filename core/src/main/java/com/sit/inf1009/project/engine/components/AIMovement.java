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

        e.setXPosition(e.getXPosition() + vx * dt);
        e.setYPosition(e.getYPosition() + vy * dt);

        int w = com.badlogic.gdx.Gdx.graphics.getWidth();
        int h = com.badlogic.gdx.Gdx.graphics.getHeight();

        if (e.getXPosition() < 0) { e.setXPosition(0); dirX *= -1; }
        if (e.getXPosition() > w) { e.setXPosition(w); dirX *= -1; }
        if (e.getYPosition() < 0) { e.setYPosition(0); dirY *= -1; }
        if (e.getYPosition() > h) { e.setYPosition(h); dirY *= -1; }
    }
}