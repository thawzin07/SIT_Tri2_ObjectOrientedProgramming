package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;
import com.sit.inf1009.project.engine.components.CollidableComponent;

import java.util.Random;

public final class FoodFactory {
    private int nextId;
    private final float radius;
    private final double speed;
    private final Random rng = new Random();

    public FoodFactory(int startingId, float radius, double speed) {
        this.nextId = startingId;
        this.radius = radius;
        this.speed = speed;
    }

    public FoodEntity getFood(FoodCategory type) {
        if (type == null) return null;

        // Sets the colour of the entity here. Change if using image for food.
        final Color color;
        switch (type) {
            case VEGETABLE:     color = Color.GREEN;  break;
            case PROTEIN:       color = Color.RED;    break;
            case CARBOHYDRATE:  color = Color.YELLOW; break;
            case OIL:           color = Color.ORANGE; break;
            default: return null;
        }

        // Create entity
        FoodEntity food = new FoodEntity(nextId++, type, color);

        // Attach collision
        food.setCollidable(new CollidableComponent(radius, true));

        // Attach AI movement
        int dirX = rng.nextBoolean() ? 1 : -1;
        int dirY = rng.nextBoolean() ? 1 : -1;
        food.setMovement(new AIMovement(speed, dirX, dirY));

        // randomise spawn position within inside screen bounds
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        food.setXPosition(radius + rng.nextDouble() * Math.max(1, (w - 2 * radius)));
        food.setYPosition(radius + rng.nextDouble() * Math.max(1, (h - 2 * radius)));

        return food;
    }
}