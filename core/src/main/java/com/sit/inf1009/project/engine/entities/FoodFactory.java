package com.sit.inf1009.project.engine.entities;

import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.GameSession;
import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.components.FoodCollidableComponent;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;

import java.util.Map;
import java.util.Random;

public final class FoodFactory {

    private static final FoodCategory[] CONCRETE_TYPES = new FoodCategory[]{
            FoodCategory.VEGETABLE,
            FoodCategory.PROTEIN,
            FoodCategory.CARBOHYDRATE,
            FoodCategory.OIL
    };

    private final Random rng;
    private final double foodRadius;
    private final double npcSpeed;
    private final int minX, maxX, minY, maxY;

    private final GameSession gameSession;
    private final Map<FoodCategory, Texture> foodCategoryTextures;

    public FoodFactory(Random rng,
                       double foodRadius,
                       double npcSpeed,
                       int minX, int maxX,
                       int minY, int maxY,
                       GameSession gameSession,
                       Map<FoodCategory, Texture> foodCategoryTextures) {
        this.rng = rng;
        this.foodRadius = foodRadius;
        this.npcSpeed = npcSpeed;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.gameSession = gameSession;
        this.foodCategoryTextures = foodCategoryTextures;
    }
    
    public Entity createFoodEntity(int id, FoodCategory requestedCategory) {
        if (requestedCategory == null) return null;

        FoodCategory category = resolveCategory(requestedCategory);

        Entity npc = new Entity(id);

        npc.setXPosition(minX + rng.nextInt(Math.max(1, maxX - minX)));
        npc.setYPosition(minY + rng.nextInt(Math.max(1, maxY - minY)));

        int dirX = rng.nextBoolean() ? 1 : -1;
        int dirY = rng.nextBoolean() ? 1 : -1;

        npc.setMovement(new AIMovement(npcSpeed, dirX, dirY));
        npc.setCollidable(new FoodCollidableComponent(foodRadius, category, 1, gameSession));

        if (foodCategoryTextures != null) {
            Texture tex = foodCategoryTextures.get(category);
            if (tex != null) npc.setTexture(tex);
        }

        return npc;
    }

    private FoodCategory resolveCategory(FoodCategory requested) {
        if (requested != FoodCategory.RANDOM) return requested;
        return CONCRETE_TYPES[rng.nextInt(CONCRETE_TYPES.length)];
    }
}