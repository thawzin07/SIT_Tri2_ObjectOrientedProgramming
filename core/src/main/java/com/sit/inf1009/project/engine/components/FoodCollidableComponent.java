package com.sit.inf1009.project.engine.components;

import com.sit.inf1009.project.GameSession;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;
import com.sit.inf1009.project.engine.interfaces.FoodCollidableInterface;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public class FoodCollidableComponent extends CollidableComponent
        implements FoodCollidableInterface {

    private final FoodCategory category;
    private final int plateValue;
    private boolean collected;
    private final GameSession gameSession;

    public FoodCollidableComponent(double radius,
                                   FoodCategory category,
                                   int plateValue,
                                   GameSession gameSession) {
        super(radius, true);
        this.category = category;
        this.plateValue = plateValue;
        this.gameSession = gameSession;
        this.collected = false;
        setRemoveOnCollision(false);
    }

    @Override
    public void onCollision(Entity self, Entity other,
                            EntityManager entityManager,
                            InputOutputManager ioManager) {
        if (collected) return;

        if (other.getID() == 1) {
            onPlayerCollision(self, other, entityManager, ioManager);
        } else {
            onObjectCollision(self, other);
        }
    }

    @Override
    public void onPlayerCollision(Entity food, Entity player,
                                  EntityManager entityManager,
                                  InputOutputManager ioManager) {
        collected = true;
        gameSession.addFood(category, plateValue);
        entityManager.queueRemove(food);
    }

    @Override
    public void onObjectCollision(Entity food, Entity other) {
        // leave blank for now, or handle food-food bounce later
    }

    @Override
    public void onBoundaryCollision(Entity food) {
        // movement component should usually handle wall bounce
    }

    @Override
    public FoodCategory getFoodCategory() {
        return category;
    }

    @Override
    public int getScoreValue() {
        return plateValue;
    }

    @Override
    public boolean isCollected() {
        return collected;
    }

    @Override
    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
