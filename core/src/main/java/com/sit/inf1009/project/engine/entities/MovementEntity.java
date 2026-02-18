package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.sit.inf1009.project.engine.interfaces.MovementInterface;

public class MovementEntity implements MovementInterface {

    private double x;
    private double y;

    private double velocityX;
    private double velocityY;

    private double speed;
    private boolean isPlayerControlled;

    private float width;
    private float height;

    public MovementEntity(double x, double y,
                          double speed,
                          boolean isPlayerControlled,
                          float width,
                          float height) {

        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isPlayerControlled = isPlayerControlled;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(double dt) {

        if (isPlayerControlled) {
            playerMovement();
        }

        x += velocityX * dt;
        y += velocityY * dt;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Clamp X
        if (x < 0) x = 0;
        if (x > screenWidth - width)
            x = screenWidth - width;

        // Clamp Y
        if (y < 0) y = 0;
        if (y > screenHeight - height)
            y = screenHeight - height;
    }

    private void playerMovement() {
        velocityX = 0;
        velocityY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocityY = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocityY = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocityX = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocityX = speed;
    }

    @Override
    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    @Override
    public double getVelocityX() { return velocityX; }

    @Override
    public double getVelocityY() { return velocityY; }

    public float getX() { return (float) x; }
    public float getY() { return (float) y; }
}
