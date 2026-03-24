package com.sit.inf1009.project.app.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.components.MovementComponent;
import com.sit.inf1009.project.engine.components.PlayerMovement;
import com.sit.inf1009.project.engine.components.AIMovement;
import com.sit.inf1009.project.engine.core.Scene;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.MovementManager;
import com.sit.inf1009.project.engine.managers.SceneManager;
import com.sit.inf1009.project.game.components.FoodCollidableComponent;
import com.sit.inf1009.project.game.components.PlayerCollidableComponent;
import com.sit.inf1009.project.game.domain.DifficultyConfig;
import com.sit.inf1009.project.game.domain.FoodCategory;
import com.sit.inf1009.project.game.domain.GameSession;
import com.sit.inf1009.project.game.factory.FoodFactory;
import com.sit.inf1009.project.game.services.FoodSpawnCoordinator;

import java.util.Map;
import java.util.Random;

public final class GameplayRuntime {

    private final InputOutputManager ioManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final SceneManager sceneManager;
    private final FoodSpawnCoordinator foodSpawnCoordinator;
    private final Map<FoodCategory, Texture> foodCategoryTextures;
    private final int playerId;
    private final int foodIdStart;

    private FoodFactory foodFactory;
    private int nextFoodId;
    private int referenceViewportWidth;
    private int referenceViewportHeight;
    private double currentGameplayScale = 1.0;
    private GameSession activeGameSession;
    private DifficultyConfig activeDifficultyConfig;

    public GameplayRuntime(InputOutputManager ioManager,
                           EntityManager entityManager,
                           MovementManager movementManager,
                           SceneManager sceneManager,
                           FoodSpawnCoordinator foodSpawnCoordinator,
                           Map<FoodCategory, Texture> foodCategoryTextures,
                           int playerId,
                           int foodIdStart) {
        this.ioManager = ioManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.sceneManager = sceneManager;
        this.foodSpawnCoordinator = foodSpawnCoordinator;
        this.foodCategoryTextures = foodCategoryTextures;
        this.playerId = playerId;
        this.foodIdStart = foodIdStart;
        this.nextFoodId = foodIdStart;
    }

    public void initViewportBaseline(int width, int height) {
        this.referenceViewportWidth = Math.max(1, width);
        this.referenceViewportHeight = Math.max(1, height);
        this.currentGameplayScale = 1.0;
    }

    public void startNewGame(GameSession gameSession,
                             DifficultyConfig difficultyConfig,
                             Texture selectedAvatarTexture) {
        this.activeGameSession = gameSession;
        this.activeDifficultyConfig = difficultyConfig;
        entityManager.clear();
        movementManager.clear();
        sceneManager.push(new Scene("Level 1", new Color(0.1f, 0.2f, 0.3f, 1f)));
        loadEntitiesForLevel(1, gameSession, difficultyConfig, selectedAvatarTexture);
    }

    public void loadEntitiesForLevel(int levelNum,
                                     GameSession gameSession,
                                     DifficultyConfig difficultyConfig,
                                     Texture selectedAvatarTexture) {
        this.activeGameSession = gameSession;
        this.activeDifficultyConfig = difficultyConfig;
        double playerRadius = 18d * currentGameplayScale;
        double foodRadius = 10.5d * currentGameplayScale;
        double playerSpeed = 250d * currentGameplayScale;
        double npcSpeed = difficultyConfig.getNpcSpeed() * currentGameplayScale;
        int worldW = Math.max(1, Gdx.graphics.getWidth());
        int worldH = Math.max(1, Gdx.graphics.getHeight());
        int playableH = Math.max(1, worldH - (int) Math.ceil(getHudBlockHeight()));

        Entity player = new Entity(playerId);
        player.setXPosition(worldW * 0.3);
        player.setYPosition(worldH * 0.35);
        player.setMovement(new PlayerMovement(ioManager, playerSpeed));
        player.setCollidable(new PlayerCollidableComponent(playerRadius));
        if (selectedAvatarTexture != null) {
            player.setTexture(selectedAvatarTexture);
        }
        entityManager.addEntity(player);
        movementManager.addMovable(player);

        Random rng = new Random();
        int npcCount = (levelNum == 1) ? difficultyConfig.getNpcCount() : Math.max(4, difficultyConfig.getNpcCount() / 2);
        int minX = (int) Math.max(40, foodRadius * 2);
        int maxX = Math.max(minX + 1, worldW - minX);
        int minY = (int) Math.max(60, foodRadius * 2);
        int maxY = Math.max(minY + 1, playableH - minY);
        this.foodFactory = new FoodFactory(
                rng,
                foodRadius,
                npcSpeed,
                minX, maxX,
                minY, maxY,
                gameSession,
                foodCategoryTextures
        );

        nextFoodId = foodIdStart;
        nextFoodId = foodSpawnCoordinator.spawnStartingFoods(
                this.foodFactory,
                nextFoodId,
                difficultyConfig.getFoodEntityCount());
    }

    public void ensureFoodDiversityAndCount(DifficultyConfig difficultyConfig) {
        nextFoodId = foodSpawnCoordinator.ensureFoodDiversityAndCount(
                foodFactory,
                nextFoodId,
                difficultyConfig.getFoodEntityCount());
    }

    public void configureMovementBoundsForCurrentViewport() {
        int worldW = Math.max(1, Gdx.graphics.getWidth());
        int worldH = Math.max(1, Gdx.graphics.getHeight());
        double maxY = Math.max(1d, worldH - getHudBlockHeight());
        for (Entity entity : entityManager.getEntities()) {
            MovementComponent movement = entity.getMovement();
            if (movement instanceof AIMovement aiMovement) {
                aiMovement.setBounds(0d, worldW, 0d, maxY);
            }
        }
    }

    public void rescaleEntitiesForViewportChange(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if (oldWidth <= 0 || oldHeight <= 0 || entityManager == null) {
            return;
        }

        double sx = (double) newWidth / (double) oldWidth;
        double sy = (double) newHeight / (double) oldHeight;

        double targetGameplayScale = Math.min(
                (double) newWidth / (double) Math.max(1, referenceViewportWidth),
                (double) newHeight / (double) Math.max(1, referenceViewportHeight));
        if (targetGameplayScale <= 0d) {
            targetGameplayScale = 1d;
        }
        double scaleDelta = targetGameplayScale / Math.max(0.0001d, currentGameplayScale);
        currentGameplayScale = targetGameplayScale;

        for (Entity entity : entityManager.getEntities()) {
            double newX = entity.getXPosition() * sx;
            double newY = entity.getYPosition() * sy;
            entity.setXPosition(newX);
            entity.setYPosition(newY);

            CollidableComponent collidable = entity.getCollidable();
            if (collidable != null) {
                collidable.setCollisionRadius(collidable.getCollisionRadius() * scaleDelta);
            }

            MovementComponent movement = entity.getMovement();
            if (movement != null) {
                movement.setSpeed(movement.getSpeed() * scaleDelta);
            }
        }

        clampEntitiesToPlayableArea();
        rebuildFoodFactoryForCurrentViewport();
    }

    public Color getEntityRenderColor(Entity entity) {
        if (entity == null) {
            return Color.WHITE;
        }
        if (entity.getID() == playerId) {
            return new Color(0.75f, 0.9f, 1f, 1f);
        }
        if (entity.getCollidable() instanceof FoodCollidableComponent food) {
            return switch (food.getFoodCategory()) {
                case VEGETABLE -> new Color(0.2f, 0.85f, 0.2f, 1f);
                case PROTEIN -> new Color(0.9f, 0.25f, 0.25f, 1f);
                case CARBOHYDRATE -> new Color(0.95f, 0.8f, 0.2f, 1f);
                case OIL -> new Color(0.72f, 0.42f, 0.9f, 1f);
                default -> Color.WHITE;
            };
        }
        return Color.WHITE;
    }

    public Texture getFoodTexture(FoodCategory category) {
        if (foodCategoryTextures == null || category == null) {
            return null;
        }
        return foodCategoryTextures.get(category);
    }

    public float getHudScaleFactor() {
        float w = Math.max(1f, Gdx.graphics.getWidth());
        float h = Math.max(1f, Gdx.graphics.getHeight());
        float sx = w / 1280f;
        float sy = h / 720f;
        float scale = Math.min(sx, sy);
        return Math.max(0.85f, Math.min(1.45f, scale));
    }

    public float getHudBlockHeight() {
        return 34f * getHudScaleFactor();
    }

    public void clampEntitiesToPlayableArea() {
        int worldW = Math.max(1, Gdx.graphics.getWidth());
        int worldH = Math.max(1, Gdx.graphics.getHeight());
        double topLimit = Math.max(1d, worldH - getHudBlockHeight());

        for (Entity entity : entityManager.getEntities()) {
            CollidableComponent collidable = entity.getCollidable();
            double radius = (collidable != null) ? collidable.getCollisionRadius() : 0d;

            double x = entity.getXPosition();
            double y = entity.getYPosition();
            double minX = radius;
            double maxX = worldW - radius;
            double minY = radius;
            double maxY = topLimit - radius;

            x = Math.max(minX, Math.min(x, maxX));
            y = Math.max(minY, Math.min(y, maxY));
            entity.setXPosition(x);
            entity.setYPosition(y);
        }
    }

    private void rebuildFoodFactoryForCurrentViewport() {
        if (activeGameSession == null || activeDifficultyConfig == null) {
            return;
        }

        double foodRadius = 10.5d * currentGameplayScale;
        double npcSpeed = activeDifficultyConfig.getNpcSpeed() * currentGameplayScale;
        int worldW = Math.max(1, Gdx.graphics.getWidth());
        int worldH = Math.max(1, Gdx.graphics.getHeight());
        int playableH = Math.max(1, worldH - (int) Math.ceil(getHudBlockHeight()));
        int minX = (int) Math.max(40, foodRadius * 2);
        int maxX = Math.max(minX + 1, worldW - minX);
        int minY = (int) Math.max(60, foodRadius * 2);
        int maxY = Math.max(minY + 1, playableH - minY);

        this.foodFactory = new FoodFactory(
                new Random(),
                foodRadius,
                npcSpeed,
                minX, maxX,
                minY, maxY,
                activeGameSession,
                foodCategoryTextures
        );
    }
}
