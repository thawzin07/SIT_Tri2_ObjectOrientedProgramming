//package com.sit.inf1009.project.engine.core;
//
//import java.util.ArrayList;
//import java.util.List;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.utils.ScreenUtils;
//import com.sit.inf1009.project.engine.entities.Entity;
//import com.sit.inf1009.project.engine.entities.CollidableEntity;
//import com.sit.inf1009.project.engine.interfaces.MovementInterface;
//import com.sit.inf1009.project.engine.managers.CollisionManager;
//import com.sit.inf1009.project.engine.managers.MovementManager;
//
//public class Scene {
//    // basic details to identify and draw the background of this level
//    private String name;
//    private Color backgroundColor;
//    
//    // the "Inventory" of this scene. 
//    // each entitylist will be different in each scene
//    private List<Entity> entityList;
//    
//    // a simple timer to track how long the player has been in this specific scene
//    public float timeAlive = 0;
//
//    public Scene(String name, Color color) {
//        this.name = name;
//        this.backgroundColor = color;
//        this.entityList = new ArrayList<>();
//    }
//
//    /**
//     * onEnter is called by the SceneManager the exact moment this scene appears on screen.
//     * pass the GLOBAL managers in as arguments.
//     * the Scene takes its private list of entities and hands them over to the Managers.
//     */
//    public void onEnter(MovementManager globalMovement, CollisionManager globalCollision) {
//        System.out.println("Scene " + name + " entered. Wiring up physics...");
//        
//        // Loop through every entity sitting in this scene's inventory
//        for (Entity entity : entityList) {
//            
//            // If the entity is allowed to move, give it to the MovementManager
//            if (entity instanceof MovementInterface) {
//                globalMovement.add((MovementInterface) entity);
//            }
//            
//            // If the entity has a hitbox, give it to the CollisionManager
//            if (entity instanceof CollidableEntity) {
//                globalCollision.add((CollidableEntity) entity);
//            }
//        }
//    }
//
//    /**
//     * onExit is called the exact moment the player leaves this scene.
//     * if we don't do this, the Global Managers will keep calculating
//     * physics for scene 1 entities even when the player is in scene 2
//     */
//    public void onExit(MovementManager globalMovement, CollisionManager globalCollision) {
//        System.out.println("Scene " + name + " exited. Unplugging physics...");
//        
//        // Tell the Global Managers to forget about these entities
//        for (Entity entity : entityList) {
//            if (entity instanceof MovementInterface) {
//                globalMovement.remove((MovementInterface) entity);
//            }
//            if (entity instanceof CollidableEntity) {
//                globalCollision.remove((CollidableEntity) entity);
//            }
//        }
//    }
//
//    // Adds a new entity to this level's inventory
//    public void addEntity(Entity entity) {
//        entityList.add(entity);
//    }
//
//    public void removeEntity(Entity entity) {
//        entityList.remove(entity);
//    }
//    
//    public List<Entity> getEntities() {
//        return entityList;
//    }
//
//    public void update(float dt) {
//        // this method is now only for things specific to this scene (like a countdown timer).
//        timeAlive += dt; 
//    }
//
//    public void render(SpriteBatch batch) {
//        // Paint the background color
//        ScreenUtils.clear(backgroundColor);
//        
//        // Draw every entity in the inventory to the screen
//        for (Entity e : entityList) {
//            if (e.getTexture() != null) {
//                 batch.draw(e.getTexture(), (float)e.getX(), (float)e.getY());
//            }
//        }
//    }
//    
//    // destroys the inventory when the game ends
//    public void dispose() {
//        entityList.clear();
//    }
//    
//    public String getName() { return name; }
//    public int getEntityCount() { return entityList.size(); }
//}


package com.sit.inf1009.project.engine.core;

import com.badlogic.gdx.graphics.Color;

public class Scene {
    private String name;
    private Color backgroundColor;
    public float timeAlive = 0;

    public Scene(String name, Color color) {
        this.name = name;
        this.backgroundColor = color;
    }

    public void update(float dt) {
        timeAlive += dt; 
    }

    public Color getBackgroundColor() { 
        return backgroundColor; 
    }
    
    public String getName() { 
        return name; 
    }
}