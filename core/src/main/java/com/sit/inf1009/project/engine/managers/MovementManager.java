package com.sit.inf1009.project.engine.managers;

import java.util.ArrayList;
import java.util.List;
import com.sit.inf1009.project.engine.entities.MovementEntity;
import com.sit.inf1009.project.engine.core.PhysicsEngine;
import com.sit.inf1009.project.engine.core.Vector2;

public class MovementManager {
    
    // Attributes - stores all entities that can move
    private List<MovementEntity> movableEntities;
    private PhysicsEngine physicsEngine;
    
    // Constructor
    public MovementManager() {
        this.movableEntities = new ArrayList<>();
        this.physicsEngine = new PhysicsEngine();
    }
    
    // Methods - adds an entity to the movement tracking
    public void addMovableEntity(MovementEntity entity) {
        if (entity != null && !movableEntities.contains(entity)) {
            movableEntities.add(entity);
        }
    }
    
    // Removes an entity from tracking
    public void removeMovableEntity(MovementEntity entity) {
        movableEntities.remove(entity);
    }
    
    // Updates all entities based on their positions and delta time
    public void update(double dt) {
        for (MovementEntity entity : movableEntities) {
            // Apply physics forces (gravity, etc.)
            physicsEngine.applyForces(entity);
            
            // Update entity movement
            physicsEngine.integrate(entity, dt);
        }
    }
    
    // Sets velocity for specific entity by ID
    public void setVelocity(int entityId, double vx, double vy) {
        for (MovementEntity entity : movableEntities) {
            if (entity.getId() == entityId) {
                entity.setVelocity(new Vector2(vx, vy));
                break;
            }
        }
    }
    
    // Returns the velocity magnitude for an entity
    public double getVelocity(int entityId) {
        for (MovementEntity entity : movableEntities) {
            if (entity.getId() == entityId) {
                return entity.getVelocity().magnitude();
            }
        }
        return 0.0;
    }
    
    // Sets the entity velocity to zero
    public void stopEntity(int entityId) {
        setVelocity(entityId, 0, 0);
    }
    
    // Clears all movable entities
    public void clear() {
        movableEntities.clear();
    }
    
    // Get physics engine (for external configuration)
    public PhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }
}