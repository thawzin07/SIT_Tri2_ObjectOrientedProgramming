package com.sit.inf1009.project.engine.interfaces;
import com.sit.inf1009.project.engine.entities.Entity;
import com.sit.inf1009.project.engine.managers.EntityManager;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

public interface CollidableInterface {
    double getCollisionRadius();

    void onCollision(Entity self, Entity other, EntityManager em, InputOutputManager io);

    boolean isCollisionEnabled();
}
