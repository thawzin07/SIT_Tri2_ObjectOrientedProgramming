package com.sit.inf1009.project.engine.interfaces;

import com.sit.inf1009.project.engine.core.Vector2;

public interface MovementInterface {
    
    // Set the velocity of the entity
    void setVelocity(Vector2 velocity);
    
    // Get the current velocity
    Vector2 getVelocity();
    
    // Set the acceleration of the entity
    void setAcceleration(Vector2 acceleration);
    
    // Get the current acceleration
    Vector2 getAcceleration();
    
    // Apply a force to the entity
    void applyForce(Vector2 force);
    
    // Set the position of the entity
    void setPosition(Vector2 position);
    
    // Get the current position
    Vector2 getPosition();
    
    // Update the entity's movement (called each frame)
    void update(double dt);
}