package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.sit.inf1009.project.engine.interfaces.MovementInterface;

public class MovementEntity implements MovementInterface {

    private double x;
    private double y;

    private double velocityX;
    private double velocityY;

    private final double speed;
    private final boolean isPlayerControlled;

    private final float width;
    private final float height;

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
        } else {
            aiMovement();
        }

        x += velocityX * dt;
        y += velocityY * dt;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Clamp / wrap behavior
        if (isPlayerControlled) {
            // Bucket: clamp within screen
            if (x < 0) x = 0;
            if (x > screenWidth - width) x = screenWidth - width;

            if (y < 0) y = 0;
            if (y > screenHeight - height) y = screenHeight - height;
        } else {
            // Droplet: when it hits bottom, respawn at top with random X
            if (y <= 0) {
                y = screenHeight - height;
                x = Math.random() * (screenWidth - width);
            }
        }
    }

    private void playerMovement() {
        velocityX = 0;
        velocityY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocityY = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocityY = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocityX = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocityX = speed;
    }

    private void aiMovement() {
        // Simple droplet AI: always fall down
        velocityX = 0;
        velocityY = -speed;
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

    public boolean isPlayerControlled() { return isPlayerControlled; }
}
