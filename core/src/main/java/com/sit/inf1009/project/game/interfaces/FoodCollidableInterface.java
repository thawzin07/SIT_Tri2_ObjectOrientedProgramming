package com.sit.inf1009.project.game.interfaces;

import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.game.FoodCategory;
import com.sit.inf1009.project.engine.interfaces.CollidableInterface;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

/**
 * Interface defining food-specific collision behaviour.
 * Food objects will implement this so they can react
 * differently depending on what they collide with.
 */
public interface FoodCollidableInterface extends CollidableInterface {

    /**
     * Called when food collides with the player.
     * Typically used to collect the food and update score.
     */
    void onPlayerCollision(Entity food,
                           Entity player,
                           EntityManager entityManager,
                           InputOutputManager ioManager);

    /**
     * Called when food collides with another object.
     * Used for bounce behaviour.
     */
    void onObjectCollision(Entity food, Entity other);

    /**
     * Called when food collides with a boundary or wall.
     * Used to reverse movement direction.
     */
    void onBoundaryCollision(Entity food);
    
    FoodCategory getFoodCategory();

    /**
     * Returns the score value of this food.
     * Positive = healthy food
     * Negative = unhealthy food
     */
    int getScoreValue();

    /**
     * Returns whether the food has already been collected.
     */
    boolean isCollected();

    void setCollected(boolean collected);
}
