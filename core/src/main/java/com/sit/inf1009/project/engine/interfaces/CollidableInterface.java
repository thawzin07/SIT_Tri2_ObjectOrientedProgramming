package com.sit.inf1009.project.engine.interfaces;

public interface CollidableInterface {
    double getCollisionRadius();

    void onCollision(CollidableInterface other);

    boolean isCollisionEnabled();
}
