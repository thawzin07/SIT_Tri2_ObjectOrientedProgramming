package com.sit.inf1009.project;

import com.sit.inf1009.project.engine.components.FoodCollidableComponent;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.entities.FoodFactory;
import com.sit.inf1009.project.engine.interfaces.FoodCategory;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.MovementManager;

public class FoodSpawnCoordinator {

    private final EntityManager entityManager;
    private final MovementManager movementManager;

    public FoodSpawnCoordinator(EntityManager entityManager, MovementManager movementManager) {
        this.entityManager = entityManager;
        this.movementManager = movementManager;
    }

    public int spawnStartingFoods(FoodFactory factory, int startId, int totalFoods) {
        int nextId = startId;
        FoodCategory[] required = new FoodCategory[] {
                FoodCategory.VEGETABLE,
                FoodCategory.PROTEIN,
                FoodCategory.CARBOHYDRATE,
                FoodCategory.OIL
        };

        for (FoodCategory category : required) {
            Entity food = factory.createFoodEntity(nextId++, category);
            spawnGameEntity(food);
        }

        while (nextId < startId + totalFoods) {
            Entity food = factory.createFoodEntity(nextId++, FoodCategory.RANDOM);
            spawnGameEntity(food);
        }
        return nextId;
    }

    public int ensureFoodDiversityAndCount(FoodFactory foodFactory, int nextFoodId, int targetFoodCount) {
        if (foodFactory == null) {
            return nextFoodId;
        }

        int veg = 0;
        int protein = 0;
        int carb = 0;
        int oil = 0;
        int total = 0;

        for (Entity entity : entityManager.getEntities()) {
            if (!(entity.getCollidable() instanceof FoodCollidableComponent component)) {
                continue;
            }

            total++;
            FoodCategory category = component.getFoodCategory();
            if (category == null || category == FoodCategory.RANDOM) {
                continue;
            }

            switch (category) {
                case VEGETABLE -> veg++;
                case PROTEIN -> protein++;
                case CARBOHYDRATE -> carb++;
                case OIL -> oil++;
                default -> {
                }
            }
        }

        if (veg == 0) {
            spawnGameEntity(foodFactory.createFoodEntity(nextFoodId++, FoodCategory.VEGETABLE));
            total++;
        }
        if (protein == 0) {
            spawnGameEntity(foodFactory.createFoodEntity(nextFoodId++, FoodCategory.PROTEIN));
            total++;
        }
        if (carb == 0) {
            spawnGameEntity(foodFactory.createFoodEntity(nextFoodId++, FoodCategory.CARBOHYDRATE));
            total++;
        }
        if (oil == 0) {
            spawnGameEntity(foodFactory.createFoodEntity(nextFoodId++, FoodCategory.OIL));
            total++;
        }

        while (total < targetFoodCount) {
            spawnGameEntity(foodFactory.createFoodEntity(nextFoodId++, FoodCategory.RANDOM));
            total++;
        }

        return nextFoodId;
    }

    private void spawnGameEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        entityManager.addEntity(entity);
        movementManager.addMovable(entity);
    }
}
