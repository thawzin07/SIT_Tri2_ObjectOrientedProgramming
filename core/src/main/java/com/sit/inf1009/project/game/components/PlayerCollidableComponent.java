package com.sit.inf1009.project.game.components;
import com.sit.inf1009.project.engine.components.CollidableComponent;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.game.interfaces.FoodCollidableInterface;
import com.sit.inf1009.project.game.interfaces.PlayerCollidableInterface;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.IOEvent;

public class PlayerCollidableComponent extends CollidableComponent
        implements PlayerCollidableInterface {

    public PlayerCollidableComponent(double radius) {
        super(radius, true);
        setRemoveOnCollision(false); // player should not disappear on collision
    }

    @Override
    public void onCollision(Entity self, Entity other,
                            EntityManager entityManager,
                            InputOutputManager ioManager) {
        if (self == null || other == null) return;

        if (other.getCollidable() instanceof FoodCollidableInterface) {
            onFoodCollision(self, other, entityManager, ioManager);
        } else {
            onObjectCollision(self, other, entityManager, ioManager);
        }
    }

    @Override
    public void onFoodCollision(Entity player, Entity food,
                                EntityManager entityManager,
                                InputOutputManager ioManager) {
        // Food component should handle scoring/removal.
        // Player side can be used for sound/effects/animation.
        if (ioManager != null && player.getID() <= food.getID()) {
            ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "collisionmusic"));
        }
    }

    @Override
    public void onObjectCollision(Entity player, Entity other,
                                  EntityManager entityManager,
                                  InputOutputManager ioManager) {
        // Leave blank for now.
        // Later you can add obstacle damage, bounce, knockback, etc.
    }

    @Override
    public void onBoundaryCollision(Entity player) {
        // Usually handled by movement component or scene bounds.
    }
}
