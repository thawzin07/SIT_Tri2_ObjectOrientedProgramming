package com.sit.inf1009.project.engine.core;

import com.sit.inf1009.project.engine.entities.MovementEntity;

public class PhysicsEngine {
    private Vector2 gravity;
    private boolean gravityEnabled;

    public PhysicsEngine() {
        this.gravity = new Vector2(0, 0); // Start with no gravity
        this.gravityEnabled = false;
    }

    public void applyGravity(MovementEntity entity) {
        if (gravityEnabled) {
            // Use applyForce instead of directly modifying velocity
            entity.applyForce(gravity);
        }
    }

    public void applyForces(MovementEntity entity) {
        if (gravityEnabled) {
            applyGravity(entity);
        }
    }

    public void integrate(MovementEntity entity, double dt) {
        // Just call the entity's update method
        // The entity handles its own physics integration
        entity.update(dt);
    }

    // Getters and Setters
    public Vector2 getGravity() {
        return gravity;
    }

    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
    }

    public boolean isGravityEnabled() {
        return gravityEnabled;
    }

    public void setGravityEnabled(boolean gravityEnabled) {
        this.gravityEnabled = gravityEnabled;
    }
}