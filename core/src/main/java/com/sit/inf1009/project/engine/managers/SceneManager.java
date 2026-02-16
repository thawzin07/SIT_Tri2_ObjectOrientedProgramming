package com.sit.inf1009.project.engine.managers;

public class SceneManager {

    private MovementManager movementManager;   

    public SceneManager() {
        movementManager = new MovementManager();  
    }

    public void update(double deltaTime) {
        movementManager.updateAll(deltaTime);     
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }
}
