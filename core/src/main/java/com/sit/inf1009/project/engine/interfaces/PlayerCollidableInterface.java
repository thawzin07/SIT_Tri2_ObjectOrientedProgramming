package com.sit.inf1009.project.engine.interfaces;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

/**
 * Interface defining player-specific collision behaviour.
 * The player can react differently depending on what it collides with.
 */
public interface PlayerCollidableInterface extends CollidableInterface {

    /**
     * Called when the player collides with food.
     */
    void onFoodCollision(Entity player,
                         Entity food,
                         EntityManager entityManager,
                         InputOutputManager ioManager);

    /**
     * Called when the player collides with a non-food object.
     */
    void onObjectCollision(Entity player,
                           Entity other,
                           EntityManager entityManager,
                           InputOutputManager ioManager);

    /**
     * Called when the player collides with a boundary or wall.
     */
    void onBoundaryCollision(Entity player);
}