package com.sit.inf1009.project.engine.entities;

import com.sit.inf1009.project.engine.core.Vector2;
import com.sit.inf1009.project.engine.interfaces.MovementInterface;

public class MovementEntity extends Entity implements MovementInterface {

    // Movement properties (in addition to Entity's properties)
    private Vector2 position;  // This is separate from Entity's xPosition/yPosition
    private Vector2 velocity;
    private Vector2 acceleration;
    private double maxSpeed;
    private double mass;

    // Constructor
    public MovementEntity(int id, double maxSpeed, double mass, Vector2 position) {
        super(id); // Calls Entity(int id) constructor
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
        this.maxSpeed = maxSpeed;
        this.mass = mass;
    }
    
    // Overloaded constructor
    public MovementEntity(int id, double maxSpeed, double mass) {
        this(id, maxSpeed, mass, new Vector2(0, 0));
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
        capSpeed();
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public Vector2 getAcceleration() {
        return acceleration;
    }

    @Override
    public void applyForce(Vector2 force) {
        if (mass > 0) {
            Vector2 newAcceleration = force.divide(mass);
            this.acceleration = this.acceleration.add(newAcceleration);
        }
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position = position;
        // Also update Entity's xPosition and yPosition for consistency
        setXPosition(position.getX());
        setYPosition(position.getY());
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void update(double dt) {
        // Update velocity based on acceleration
        velocity = velocity.add(acceleration.multiply(dt));

        // Cap at max speed
        capSpeed();

        // Update position based on velocity
        position = position.add(velocity.multiply(dt));
        
        // Sync with Entity's position
        setXPosition(position.getX());
        setYPosition(position.getY());

        // Reset acceleration (forces are applied each frame)
        acceleration = new Vector2(0, 0);
    }
    
    // Helper method
    private void capSpeed() {
        if (maxSpeed > 0 && velocity.magnitude() > maxSpeed) {
            velocity = velocity.normalize().multiply(maxSpeed);
        }
    }

    // Getters and Setters
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}