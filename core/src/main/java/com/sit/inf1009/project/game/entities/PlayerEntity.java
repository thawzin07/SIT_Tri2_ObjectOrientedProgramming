package com.sit.inf1009.project.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.sit.inf1009.project.engine.components.PlayerMovement;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.game.components.PlayerCollidableComponent;
import com.sit.inf1009.project.game.interfaces.PlayerCollidableInterface;

public final class PlayerEntity extends Entity implements PlayerCollidableInterface {

    private static PlayerEntity instance;

    private final PlayerMovement playerMovement;
    private final PlayerCollidableComponent playerCollidableComponent;

    private PlayerEntity(int id,
                         InputOutputManager ioManager,
                         double speed,
                         double collisionRadius,
                         Texture texture) {
        super(id);
        this.playerMovement = new PlayerMovement(ioManager, speed);
        this.playerCollidableComponent = new PlayerCollidableComponent(collisionRadius);
        setMovement(playerMovement);
        setCollidable(playerCollidableComponent);
        setTexture(texture);
    }

    public static synchronized PlayerEntity getInstance(int id,
                                                        InputOutputManager ioManager,
                                                        double speed,
                                                        double collisionRadius,
                                                        Texture texture) {
        if (instance == null) {
            instance = new PlayerEntity(id, ioManager, speed, collisionRadius, texture);
        } else {
            instance.reconfigure(id, speed, collisionRadius, texture);
        }
        return instance;
    }

    private void reconfigure(int id,
                             double speed,
                             double collisionRadius,
                             Texture texture) {
        setID(id);
        setVisible(true);
        setRotation(0d);
        setMovement(playerMovement);
        setCollidable(playerCollidableComponent);
        playerMovement.setSpeed(speed);
        playerCollidableComponent.setCollisionRadius(collisionRadius);
        playerCollidableComponent.setCollisionEnabled(true);
        playerCollidableComponent.setRemoveOnCollision(false);
        setTexture(texture);
    }


    @Override
    public double getCollisionRadius() {
        return playerCollidableComponent.getCollisionRadius();
    }

    @Override
    public boolean isCollisionEnabled() {
        return playerCollidableComponent.isCollisionEnabled();
    }

    @Override
    public void onCollision(Entity self,
                            Entity other,
                            EntityManager entityManager,
                            InputOutputManager ioManager) {
        playerCollidableComponent.onCollision(self, other, entityManager, ioManager);
    }

    @Override
    public void onFoodCollision(Entity player,
                                Entity food,
                                EntityManager entityManager,
                                InputOutputManager ioManager) {
        playerCollidableComponent.onFoodCollision(player, food, entityManager, ioManager);
    }

    @Override
    public void onObjectCollision(Entity player,
                                  Entity other,
                                  EntityManager entityManager,
                                  InputOutputManager ioManager) {
        playerCollidableComponent.onObjectCollision(player, other, entityManager, ioManager);
    }

    @Override
    public void onBoundaryCollision(Entity player) {
        playerCollidableComponent.onBoundaryCollision(player);
    }
}
