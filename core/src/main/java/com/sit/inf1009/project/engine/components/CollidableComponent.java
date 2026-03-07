package com.sit.inf1009.project.engine.components;
import com.sit.inf1009.project.engine.interfaces.CollidableInterface;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;
import com.sit.inf1009.project.engine.managers.IOEvent;

public class CollidableComponent implements CollidableInterface {

    private double collisionRadius;
    private boolean collisionEnabled;
    private boolean removeOnCollision = true;

    public CollidableComponent(double radius, boolean collisionEnabled) 
    {
        setCollisionRadius(radius);
        this.collisionEnabled = collisionEnabled;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public void setCollisionRadius(double radius) {
        if (radius < 0) radius = 0;
        this.collisionRadius = radius;
    }

    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    public void setCollisionEnabled(boolean collisionEnabled) {
        this.collisionEnabled = collisionEnabled;
    }

    @Override
    public void onCollision(Entity self, Entity other, EntityManager em, InputOutputManager io) {
        // 1) Deletion rule (component decides, EntityManager executes later)
        if (removeOnCollision && em != null && self != null) {
            em.queueRemove(self);
        }

        // 2) Sound rule (avoid double-playing by only letting one side play it)
        if (io != null && self != null && other != null) {
            if (self.getID() <= other.getID()) {
                io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "Droplet"));
            }
        }
    }
    public boolean isRemoveOnCollision() {
        return removeOnCollision;
    }

    public void setRemoveOnCollision(boolean removeOnCollision) {
        this.removeOnCollision = removeOnCollision;
    }

}